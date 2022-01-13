package nts.uk.ctx.sys.assist.dom.datarestoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.file.storage.stream.FileStoragePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import nts.arc.system.ServerSystemProperties;
import nts.gul.csv.CSVBufferReader;
import nts.gul.error.ThrowableAnalyzer;
import nts.uk.ctx.sys.assist.dom.tablelist.TableList;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class ProcessRecoverOneEmpHandle {
	public static final String DATE_FORMAT = "yyyyMMdd";

	public static final int SELECTION_TARGET_FOR_RES = 1;

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
	
	public static final String SQL_EXCEPTION = "113";

	public static final String SETTING_EXCEPTION = "5";
	
	public static final String SETTING_DATE_EXCEPTION = "8";
	
	public static final String GET_TABLE_EXCEPTION = "9";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRecoverOneEmpHandle.class);
	
	private static final Map<String, Integer> datetimeRange = new HashMap<String, Integer>();
	static {
		datetimeRange.put(YEAR_MONTH_DAY, 10);
		datetimeRange.put(YEAR_MONTH, 7);
		datetimeRange.put(YEAR, 4);
	}
	@Inject
	private DataRecoveryMngRepository dataRecoveryMngRepository;
	@Inject
	private RecoveryDataByEmployee recoveryDataByEmployee;
	@Inject
	private SaveLogDataRecoverServices saveLogDataRecoverServices; 
	
	

	public DataRecoveryOperatingCondition recoverDataOneEmp(String dataRecoveryProcessId,
			List<EmployeeDataReInfoImport> listEmployee, List<DataRecoveryTable> tableList, 
			List<Target> listTarget, List<TableList> listTbl) throws Exception {
		
		DataRecoveryOperatingCondition condition = DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS;
		int NUMBER_ERROR = 0;

		HashMap<String, CSVBufferReader> csvByteReadMaper_TableUse = new HashMap<>();
		List<DataRecoveryTable> parentTables = new ArrayList<>();
		List<DataRecoveryTable> childTables= new ArrayList<>();

		// khởi tạo csv Reader
		// order lại danh sách table trước khi restore.
		for (int i = 0; i < tableList.size(); i++) {
			String filePath = getExtractDataStoragePath(tableList.get(i).getUploadId()) + "//"
					+ tableList.get(i).getFileNameCsv() + ".csv";
			CSVBufferReader reader = new CSVBufferReader(new File(filePath));
			reader.setCharset("UTF-8");
			csvByteReadMaper_TableUse.put(tableList.get(i).getFileNameCsv(), reader);
			
			if (tableList.get(i).getHasParentTblFlg() == NotUseAtr.USE) {
				childTables.add(tableList.get(i));
			} else {
				parentTables.add(tableList.get(i));
			}
		}
		parentTables.sort((o2, o1) -> {
			return o1.getTableNo().compareTo(o2.getTableNo());
		});

		childTables.sort((o2, o1) -> {
			return o1.getTableNo().compareTo(o2.getTableNo());
		});
		
		List<DataRecoveryTable> listTableOrder = new ArrayList<>();
		
		listTableOrder.addAll(childTables);
		listTableOrder.addAll(parentTables);
		
		// アルゴリズム「対象社員データ処理」を実行する
		for (EmployeeDataReInfoImport employeeData : listEmployee) {
			
			// Update current employeeCode
			// ドメインモデル「データ復旧動作管理」の処理対象社員コードを更新する
			dataRecoveryMngRepository.updateProcessTargetEmpCode(dataRecoveryProcessId,employeeData.getEmployeeCode());

			// アルゴリズム「対象社員データ処理」を実行する (Xử lý data nhân viên đối tượng)
			try {
				condition = recoveryDataByEmployee.recoveryDataByEmployee(dataRecoveryProcessId,
						employeeData.getEmployeeId(), listTableOrder,
						csvByteReadMaper_TableUse,employeeData.getEmployeeCode(), listTbl);
			} catch (Exception e) {
				val analyzer = new ThrowableAnalyzer(e);
				if(analyzer.findByClass(SettingException.class).isPresent()){
					SettingException settingException = (SettingException) analyzer.findByClass(SettingException.class).get();
					// ghi log
					saveLogDataRecoverServices.saveErrorLogDataRecover(settingException.dataRecoveryProcessId, settingException.target, settingException.errorContent, settingException.targetDate,
							settingException.processingContent, settingException.contentSql);
					NUMBER_ERROR++;
					dataRecoveryMngRepository.updateErrorCount(dataRecoveryProcessId, NUMBER_ERROR);
				}else if(analyzer.findByClass(DelDataException.class).isPresent()){
					DelDataException delDataException = (DelDataException) analyzer.findByClass(DelDataException.class).get();
					// ghi log
					saveLogDataRecoverServices.saveErrorLogDataRecover(delDataException.dataRecoveryProcessId, delDataException.target, delDataException.errorContent, delDataException.targetDate,
							delDataException.processingContent, delDataException.contentSql);
					return DataRecoveryOperatingCondition.ABNORMAL_TERMINATION;
				}else if (analyzer.findByClass(SqlException.class).isPresent()) {
					SqlException sqlException = (SqlException) analyzer.findByClass(SqlException.class).get();
					// ghi log
					saveLogDataRecoverServices.saveErrorLogDataRecover(sqlException.dataRecoveryProcessId, sqlException.target, sqlException.errorContent, sqlException.targetDate,
							sqlException.processingContent, sqlException.contentSql);
					NUMBER_ERROR++;
					dataRecoveryMngRepository.updateErrorCount(dataRecoveryProcessId, NUMBER_ERROR);
				}
			}
			
			// check interruption [中断]
			Optional<DataRecoveryMng> dataRecovery = dataRecoveryMngRepository
					.getDataRecoveryMngById(dataRecoveryProcessId);
			if (dataRecovery.isPresent() && dataRecovery.get().getSuspendedState() == NotUseAtr.USE) {
				return DataRecoveryOperatingCondition.INTERRUPTION_END;
			}
		}
		return condition;
	}
	
	
	public static String getExtractDataStoragePath(String fileId) {
		return new FileStoragePath().getPathOfCurrentTenant().toString() + "//packs//" + fileId;
	}
}
