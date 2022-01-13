package nts.uk.ctx.sys.assist.dom.datarestoration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.shr.infra.file.storage.stream.FileStoragePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import nts.arc.system.ServerSystemProperties;
import nts.arc.time.GeneralDate;
import nts.gul.csv.CSVBufferReader;
import nts.gul.error.ThrowableAnalyzer;
import nts.uk.ctx.sys.assist.dom.categoryfieldmt.HistoryDiviSion;
import nts.uk.ctx.sys.assist.dom.tablelist.TableList;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;


@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ProcessRecoverListTblByCompanyHandle {
	
	@Inject
	private PerformDataRecoveryRepository performDataRecoveryRepository;

	@Inject
	private DataRecoveryMngRepository dataRecoveryMngRepository;

	@Inject
	private SaveLogDataRecoverServices saveLogDataRecoverServices; 
	
	@Inject
	private ProcesscrudDataByTable crudDataByTable;
	

	public static final String DATE_FORMAT = "yyyyMMdd";

	public static final int SELECTION_TARGET_FOR_RES = 1;

	public static final String SQL_EXCEPTION = "113";

	public static final String SETTING_EXCEPTION = "5";

	public static final String INDEX_CID_CSV = "0";

	public static final String INDEX_SID_CSV = "5";

	public static final String NONE_DATE = "9";

	public static final String YEAR = "6";

	public static final String YEAR_MONTH = "7";

	public static final String YEAR_MONTH_DAY = "8";

	public static final String GET_CLS_KEY_QUERY = "getClsKeyQuery";

	public static final String GET_FILED_KEY_UPDATE = "getFiledKeyUpdate";

	// fix bug #125405
	public static final String GET_FILED_KEY_QUERY = "getFieldKeyQuery";

	public static final String INDEX_HEADER = "indexUpdate";

	public static final Integer HEADER_CSV = 0;

	public static final Integer INDEX_SID = 1;

	public static final Integer INDEX_H_DATE = 2;

	public static final Integer INDEX_H_START_DATE = 3;

	public static Integer NUMBER_ERROR = 0;

	private static final Logger LOGGER = LoggerFactory.getLogger(RecoveryStorageService.class);

	private static final Map<String, Integer> datetimeRange = new HashMap<String, Integer>();
	static {
		datetimeRange.put(YEAR_MONTH_DAY, 10);
		datetimeRange.put(YEAR_MONTH, 7);
		datetimeRange.put(YEAR, 4);
	}

	
	public DataRecoveryOperatingCondition recoverDataOneTable(TableList table, String dataRecoveryProcessId,DataRecoveryOperatingCondition condition,
			DataRecoveryTable dataRecoveryTable,HashMap<String, CSVBufferReader> csvByteReadMaper) throws Exception {

		// アルゴリズム「日付処理の設定」を実行し日付設定を取得する
		List<String> dateSetting = new ArrayList<>();
		try {
			dateSetting = this.settingDate(table);
		} catch (Exception e) {
			String target = null;
			String errorContent = null;
			GeneralDate targetDate = null;
			String contentSql = null;
			String processingContent = "日付処理の設定  " + TextResource.localize("CMF004_463") + " " + table.getTableJapaneseName();
			throw new SettingException(dataRecoveryProcessId, target, errorContent, targetDate, contentSql, processingContent);
		}

		if (dateSetting.isEmpty()) {
			String target = null;
			String errorContent = null;
			GeneralDate targetDate = null;
			String contentSql = null;
			String processingContent = "日付処理の設定  " + TextResource.localize("CMF004_463") + " " + table.getTableJapaneseName();
			throw new SettingException(dataRecoveryProcessId, target, errorContent, targetDate, contentSql, processingContent);
		}

		// 履歴区分の判別する - check phân loại lịch sử
		if (table.getHistoryCls() == HistoryDiviSion.HAVE_HISTORY) {
			try {
				deleteDataTableHistory(table, false, null, dataRecoveryProcessId);
				System.out.println("DELETE TABLE BY COMPANY : " + table.getTableEnglishName());
			} catch (Exception e) {
				val analyzer = new ThrowableAnalyzer(e);
				if(analyzer.findByClass(DelDataException.class).isPresent()){
					DelDataException delDataException = (DelDataException) analyzer.findByClass(DelDataException.class).get();
					throw delDataException;
				}
			}
		}

		Optional<PerformDataRecovery> performDataRecovery = performDataRecoveryRepository
				.getPerformDatRecoverById(dataRecoveryProcessId);
		try {
			condition = this.crudDataByTable.crudDataByTable(dataRecoveryTable, null, dataRecoveryProcessId, table,
					performDataRecovery, dateSetting, false, csvByteReadMaper, null);
		} catch (Exception e) {
			val analyzer = new ThrowableAnalyzer(e);
			if(analyzer.findByClass(SettingException.class).isPresent()){
				SettingException settingException = (SettingException) analyzer.findByClass(SettingException.class).get();
				throw settingException;
			}else if (analyzer.findByClass(SqlException.class).isPresent()) {
				SqlException sqlException = (SqlException) analyzer.findByClass(SqlException.class).get();
				throw sqlException;
			}
		}
		return condition;	
	}
	
	@SuppressWarnings("unchecked")
	public void deleteDataTableHistory(TableList tableList, Boolean tableNotUse, String employeeId,
			String dataRecoveryProcessId) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, Exception {

		String[] whereCid = { "" };
		String[] whereSid = { "" };

		//
		Optional<Object> keyQuery;
		Optional<Object> filedKey;
		for (int i = 1; i < 11; i++) {
			Method m1 = TableList.class.getMethod(GET_CLS_KEY_QUERY + i);
			keyQuery = (Optional<Object>) m1.invoke(tableList);
			Method m2 = TableList.class.getMethod(GET_FILED_KEY_QUERY + i);
			filedKey = (Optional<Object>) m2.invoke(tableList);
			if (keyQuery.isPresent()) {
				if (keyQuery.get().equals(INDEX_CID_CSV)) {
					whereCid[0] = (String) filedKey.get();
				} else if (keyQuery.get().equals(INDEX_SID_CSV) && tableNotUse) {
					whereSid[0] = (String) filedKey.get();
				}
			}
		}

		String cidCurrent = AppContexts.user().companyId();
		if (tableNotUse) {
			performDataRecoveryRepository.deleteTransactionEmployeeHis(tableList, whereCid[0], whereSid[0], cidCurrent, employeeId, null, dataRecoveryProcessId);
		} else {
			performDataRecoveryRepository.deleteEmployeeHis(tableList, whereCid[0], whereSid[0], cidCurrent, employeeId, null, dataRecoveryProcessId);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> settingDate(TableList tableList) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// 「テーブル一覧」の抽出キー区から日付項目を設定する
		List<String> checkKeyQuery = new ArrayList<>();
		List<String> resultsSetting = new ArrayList<>();
		int countY = 0, countYm = 0, countYmd = 0;

		Optional<Object> keyQuery = Optional.empty();

		for (int i = 1; i < 11; i++) {
			Method m1 = TableList.class.getMethod(GET_CLS_KEY_QUERY + i);
			keyQuery = (Optional<Object>) m1.invoke(tableList);
			if (keyQuery.isPresent() && !((String) keyQuery.get()).isEmpty()) {
				checkKeyQuery.add((String) keyQuery.get());
			}
		}

		for (String currentKeyQuery : checkKeyQuery) {
			switch (currentKeyQuery) {
			case YEAR:
				countY++;
				break;
			case YEAR_MONTH:
				countYm++;
				break;
			case YEAR_MONTH_DAY:
				countYmd++;
				break;
			}
		}

		// không date
		if (countY == 0 && countYm == 0 && countYmd == 0) {
			resultsSetting.add(NONE_DATE);
		} else if (countY != 0 && countYm == 0 && countYmd == 0) {
			// năm hoặc phạm vi năm
			resultsSetting.add(YEAR);
			if (countY == 2) {
				resultsSetting.add(YEAR);
			}
		} else if (countY == 0 && countYm != 0 && countYmd == 0) {
			// tháng năm hoặc là phạm vi tháng năm
			resultsSetting.add(YEAR_MONTH);
			if (countYm == 2) {
				resultsSetting.add(YEAR_MONTH);
			}
		} else if (countY == 0 && countYm == 0 && countYmd != 0) {
			// ngày tháng năm hoặc phạm vi ngày tháng năm
			resultsSetting.add(YEAR_MONTH_DAY);
			if (countYmd == 2) {
				resultsSetting.add(YEAR_MONTH_DAY);
			}
		}
		return resultsSetting;
	}
	
	public static String getExtractDataStoragePath(String fileId) {
		return new FileStoragePath().getPathOfCurrentTenant().toString() + "//packs//" + fileId;
	}
	
}
