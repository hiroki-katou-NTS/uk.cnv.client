package nts.uk.ctx.sys.assist.dom.datarestoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.shr.infra.file.storage.stream.FileStoragePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import nts.arc.system.ServerSystemProperties;
import nts.arc.time.GeneralDateTime;
import nts.gul.csv.CSVBufferReader;
import nts.gul.error.ThrowableAnalyzer;
import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.assist.dom.category.CategoryRepository;
import nts.uk.ctx.sys.assist.dom.category.StorageRangeSaved;
import nts.uk.ctx.sys.assist.dom.datarestoration.common.CsvFileUtil;
import nts.uk.ctx.sys.assist.dom.tablelist.TableList;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
public class RecoveryStorageService {
	@Resource
	private SessionContext scContext;

	@Inject
	private PerformDataRecoveryRepository performDataRecoveryRepository;

	@Inject
	private CategoryRepository categoryRepository;
	
	@Inject
	private DataRecoveryMngRepository dataRecoveryMngRepository;

	@Inject
	private DataReEmployeeAdapter empDataMngRepo;

	@Inject
	private DataRecoveryResultRepository dataRecoveryResultRepository;

	private RecoveryStorageService self;
	
	@Inject
	private SaveLogDataRecoverServices saveLogDataRecoverServices; 
	
	@Inject
	private ProcessRecoverOneEmpHandle processRecoverOneEmpHandle;
	
	@Inject
	private ProcessRecoverListTblByCompanyHandle processRecoverListTblByCompanyHandle;
	
	@PostConstruct
	public void init() {
		this.self = scContext.getBusinessObject(RecoveryStorageService.class);
	}

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

	private static final Logger LOGGER = LoggerFactory.getLogger(RecoveryStorageService.class);

	private static final Map<String, Integer> datetimeRange = new HashMap<String, Integer>();
	static {
		datetimeRange.put(YEAR_MONTH_DAY, 10);
		datetimeRange.put(YEAR_MONTH, 7);
		datetimeRange.put(YEAR, 4);
	}

	public void recoveryStorage(String dataRecoveryProcessId) throws Exception {
		Optional<PerformDataRecovery> performRecoveries = performDataRecoveryRepository
				.getPerformDatRecoverById(dataRecoveryProcessId);
		String uploadId = performRecoveries.get().getUploadfileId();
		
		Set<String> listCtgId = categoryRepository.getListCategoryId(dataRecoveryProcessId, SELECTION_TARGET_FOR_RES);
		
		Optional<DataRecoveryMng> dataRecoveryMng = dataRecoveryMngRepository
				.getDataRecoveryMngById(dataRecoveryProcessId);
		if (dataRecoveryMng.isPresent() && dataRecoveryMng.get().getSuspendedState() == NotUseAtr.USE) {
			dataRecoveryResultRepository.updateEndDateTimeExecutionResult(dataRecoveryProcessId,
					DataRecoveryOperatingCondition.INTERRUPTION_END);
			performDataRecoveryRepository.remove(dataRecoveryProcessId);
			performDataRecoveryRepository.deleteTableListByDataStorageProcessingId(dataRecoveryProcessId);
			return;
		}

		// update OperatingCondition
		dataRecoveryMngRepository.updateByOperatingCondition(dataRecoveryProcessId,
				DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS);

		DataRecoveryOperatingCondition condition = DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS;
		int numberCateSucess = 0;
		
		// get list table by dataRecoveryProcessId (khg co trong eap)
		List<TableList> listTbl = performDataRecoveryRepository.getByDataRecoveryId(dataRecoveryProcessId);

		// 処理対象のカテゴリを処理する
		if (!listCtgId.isEmpty()) {
			for (String categoryId : listCtgId) {

				List<TableList> tableUse = performDataRecoveryRepository.getByStorageRangeSaved(
						categoryId, dataRecoveryProcessId, StorageRangeSaved.EARCH_EMP);
				List<TableList> tableNotUse = performDataRecoveryRepository.getByStorageRangeSaved(
						categoryId, dataRecoveryProcessId, StorageRangeSaved.ALL_EMP);

				TableListByCategory tableListByCategory = new TableListByCategory(categoryId, tableUse);
				TableListByCategory tableNotUseCategory = new TableListByCategory(categoryId, tableNotUse);

				// カテゴリ単位の復旧
				condition = exCurrentCategory(tableListByCategory, tableNotUseCategory, uploadId, dataRecoveryProcessId, listTbl);

				// のカテゴリカウントをカウントアップ

				if (condition != DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS) {
					break;
				}
				numberCateSucess++;
				dataRecoveryMngRepository.updateCategoryCnt(dataRecoveryProcessId, numberCateSucess);
			}
		}
		
		if (condition == DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS) {
			dataRecoveryMngRepository.updateByOperatingCondition(dataRecoveryProcessId,
					DataRecoveryOperatingCondition.DONE);
			dataRecoveryResultRepository.updateEndDateTimeExecutionResult(dataRecoveryProcessId,
					DataRecoveryOperatingCondition.DONE);
		} else {
			if (condition.equals(DataRecoveryOperatingCondition.INTERRUPTION_END)) {
				this.saveLogDataRecoverServices.saveErrorLogDataRecover(dataRecoveryProcessId, "", "", 
						null, TextResource.localize("CMF003_645"), "");
			}
			dataRecoveryMngRepository.updateByOperatingCondition(dataRecoveryProcessId, condition);
			dataRecoveryResultRepository.updateEndDateTimeExecutionResult(dataRecoveryProcessId, condition);
		}

//		performDataRecoveryRepository.deleteTableListByDataStorageProcessingId(dataRecoveryProcessId);

	}

	public DataRecoveryOperatingCondition exCurrentCategory(TableListByCategory tableListByCategory,
			TableListByCategory tableNotUseByCategory, String uploadId, String dataRecoveryProcessId, List<TableList> listTbl) throws Exception {

		DataRecoveryOperatingCondition condition = DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS;
		
		// カテゴリの中の社員単位の処理 
		condition = processEmpInCategory(tableListByCategory, dataRecoveryProcessId, uploadId, listTbl);

		if (condition == DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS) {
			// の処理対象社員コードをクリアする
			dataRecoveryMngRepository.updateProcessTargetEmpCode(dataRecoveryProcessId, null);

			// カテゴリの中の日付単位の処理
			condition = processByDateInCategory(tableNotUseByCategory, dataRecoveryProcessId, uploadId, listTbl);
		}

		dataRecoveryMngRepository.updateByOperatingCondition(dataRecoveryProcessId, condition);
		return condition;
	}

	// Xử lý những table StorageRangeSaved = EARCH_EMP
	public DataRecoveryOperatingCondition processEmpInCategory(TableListByCategory tableListByCategory,
			String dataRecoveryProcessId, String uploadId, List<TableList> listTbl) throws Exception {

		DataRecoveryOperatingCondition condition = DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS;
		List<DataRecoveryTable> tableList = new ArrayList<>();

		// カテゴリ単位の復旧
		if (tableListByCategory.getTables().size() > 0) {
			// -- Get List data from CSV file

			// Create [対象データ] TargetData
			Set<String> hashId = new HashSet<>();
			for (int j = 0; j < tableListByCategory.getTables().size(); j++) {

				Set<String> listSid = CsvFileUtil.getListSid(uploadId,
						tableListByCategory.getTables().get(j).getInternalFileName());

				// -- Tổng hợp ID Nhân viên duy nhất từ List Data
				hashId.addAll(listSid);

				DataRecoveryTable targetData = new DataRecoveryTable(uploadId,
						tableListByCategory.getTables().get(j).getInternalFileName(), listSid.isEmpty() ? false : true,
						tableListByCategory.getTables().get(j).getTableEnglishName(), 
						tableListByCategory.getTables().get(j).getTableJapaneseName(),
						tableListByCategory.getTables().get(j).getTableNo(),
						tableListByCategory.getTables().get(j).getHasParentTblFlg()
						);
					tableList.add(targetData);
			}
			
			// 対象社員コード＿ID
			List<EmployeeDataReInfoImport> employeeInfos = empDataMngRepo
					.findByIdsEmployee(new ArrayList<String>(hashId));

			// fix bug cho trường hợp retore từ màn hình cmf005 - start
			// Trường hợp này list employeeInfos lấy dữ liệu từ table
			// BSYMT_EMP_DTA_MNG_INFO bị rỗng, do đã bị xóa từ màn cmf005.
			// Nên list nhân viên tôi sẽ lấy ra từ file excel.
			if (employeeInfos.size() != hashId.size()) {
				List<String> listSidHasData = employeeInfos.stream().map(i -> i.getEmployeeId())
						.collect(Collectors.toList());
				List<String> listSid = new ArrayList<String>(hashId);
				for (int i = 0; i < listSid.size(); i++) {
					if (!listSidHasData.contains(listSid.get(i))) {
						employeeInfos.add(new EmployeeDataReInfoImport("", "", listSid.get(i), "",
								GeneralDateTime.now(), "", ""));
					}
				}
			}
			// - end

			// check employeeId in Target of PreformDataRecovery
			List<Target> listTarget = performDataRecoveryRepository.findByDataRecoveryId(dataRecoveryProcessId);

			// trường hợp list nhân viên tổng hợp từ các file excel băng null.
			// thì lấy list nhân viên từ màn hình truyền lên.
			if (hashId.isEmpty()) {
				employeeInfos = listTarget.stream().map(i -> {
					return new EmployeeDataReInfoImport(null, null, i.getSid(), i.getScd().orElse(""), null, null,
							null);
				}).collect(Collectors.toList());
			}
			
			// order list employee by empCode
			List<EmployeeDataReInfoImport> empsOrderByScd = employeeInfos.stream()
					.sorted(Comparator.comparing(EmployeeDataReInfoImport::getEmployeeCode))
					.collect(Collectors.toList());

			Optional<PerformDataRecovery> performDataRecovery = performDataRecoveryRepository
					.getPerformDatRecoverById(dataRecoveryProcessId);
			
			// Recover childTables , parentTables
			// Check recovery method [復旧方法]
			if (performDataRecovery.isPresent() && performDataRecovery.get().getRecoveryMethod() == RecoveryMethod.RESTORE_SELECTED_RANGE) {
				List<EmployeeDataReInfoImport> empSelectRange = listTarget.stream().map(i -> {
					return new EmployeeDataReInfoImport(null, null, i.getSid(), i.getScd().orElse(""), null, null,
							null);
				}).collect(Collectors.toList());
				
				if (empSelectRange.isEmpty())
					return DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS;
				
				condition = this.processRecoverOneEmpHandle.recoverDataOneEmp(dataRecoveryProcessId, empSelectRange, tableList, listTarget, listTbl);
				
			} else {
				condition = this.processRecoverOneEmpHandle.recoverDataOneEmp(dataRecoveryProcessId, empsOrderByScd, tableList, listTarget, listTbl);
			}
		 }
		return condition;
	}

	// Xử lý những table StorageRangeSaved = ALL_EMP 
	public DataRecoveryOperatingCondition processByDateInCategory(TableListByCategory tableNotUseByCategory,
			String dataRecoveryProcessId, String uploadId, List<TableList> listTbl) throws Exception {

		DataRecoveryOperatingCondition condition = DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS;
		if (tableNotUseByCategory.getTables().size() == 0) {
			return condition;
		}

		// Create [対象データ] TargetData
		Set<String> hashId = new HashSet<>();
		
		List<DataRecoveryTable> listTablesOrder = new ArrayList<>();
		List<DataRecoveryTable> parentTables = new ArrayList<>();
		List<DataRecoveryTable> childTables= new ArrayList<>();
		
		HashMap<String, CSVBufferReader> csvByteReadMaper_TableNotUse = new HashMap<>();
		for (int j = 0; j < tableNotUseByCategory.getTables().size(); j++) {

			Set<String> listSid = CsvFileUtil.getListSid(uploadId,
					tableNotUseByCategory.getTables().get(j).getInternalFileName());

			// -- Tổng hợp ID Nhân viên duy nhất từ List Data
			hashId.addAll(listSid);

			String filePath = getExtractDataStoragePath(uploadId) + "//"
					+ tableNotUseByCategory.getTables().get(j).getInternalFileName() + ".csv";

			CSVBufferReader reader = new CSVBufferReader(new File(filePath));
			reader.setCharset("UTF-8");
			csvByteReadMaper_TableNotUse.put(tableNotUseByCategory.getTables().get(j).getInternalFileName(), reader);
			
			DataRecoveryTable targetData = new DataRecoveryTable(uploadId,
					tableNotUseByCategory.getTables().get(j).getInternalFileName(), listSid.isEmpty() ? false : true,
							tableNotUseByCategory.getTables().get(j).getTableEnglishName(),
							tableNotUseByCategory.getTables().get(j).getTableJapaneseName(),
							tableNotUseByCategory.getTables().get(j).getTableNo(),
							tableNotUseByCategory.getTables().get(j).getHasParentTblFlg());
			
			if (tableNotUseByCategory.getTables().get(j).getHasParentTblFlg() == NotUseAtr.USE) {
				childTables.add(targetData);
			} else {
				parentTables.add(targetData);
			}
		}
		
		parentTables.sort((o2, o1) -> {
			return o1.getTableNo().compareTo(o2.getTableNo());
		});

		childTables.sort((o2, o1) -> {
			return o1.getTableNo().compareTo(o2.getTableNo());
		});
		
		listTablesOrder.addAll(childTables);
		listTablesOrder.addAll(parentTables);
		 int NUMBER_ERROR = 0;
		// テーブル一覧のカレントの1行分の項目を取得する
		for (DataRecoveryTable table : listTablesOrder) {
			
			System.out.println("=== Table: "  + table.getTableEnglishName() + " Table No: " + table.getTableNo());
			
			Optional<TableList> tableOpt = listTbl.stream().filter(tbl -> tbl.getTableEnglishName().equals(table.getTableEnglishName())).findFirst();
			
			if (!tableOpt.isPresent()) {
				return DataRecoveryOperatingCondition.ABNORMAL_TERMINATION;
			}
			
			// get trạng thái domain データ復旧動作管理
			Optional<DataRecoveryMng> dataRecoveryMng = dataRecoveryMngRepository
					.getDataRecoveryMngById(dataRecoveryProcessId);
			if (dataRecoveryMng.isPresent() && dataRecoveryMng.get().getSuspendedState() == NotUseAtr.USE) {
				return DataRecoveryOperatingCondition.INTERRUPTION_END;
			}

			Set<String> hasSidInCsv = CsvFileUtil.getListSid(uploadId, tableOpt.get().getInternalFileName().toString());

			DataRecoveryTable dataRecoveryTable = new DataRecoveryTable(uploadId, tableOpt.get().getInternalFileName(),
																		hasSidInCsv.isEmpty() ? false : true, tableOpt.get().getTableEnglishName(), tableOpt.get().getTableJapaneseName(),
					tableOpt.get().getTableNo(), tableOpt.get().getHasParentTblFlg());
			try {
				condition = exDataTabeRangeDate(dataRecoveryTable, tableOpt, dataRecoveryProcessId,
						csvByteReadMaper_TableNotUse);
			}  catch (Exception e) {
				val analyzer = new ThrowableAnalyzer(e);
				if(analyzer.findByClass(SettingException.class).isPresent()){
					SettingException settingException = (SettingException) analyzer.findByClass(SettingException.class).get();
					// ghi log
					saveLogDataRecoverServices.saveErrorLogDataRecover(settingException.dataRecoveryProcessId, settingException.target, settingException.errorContent, settingException.targetDate,
							settingException.processingContent, settingException.contentSql);
					NUMBER_ERROR++;
					dataRecoveryMngRepository.updateErrorCount(dataRecoveryProcessId, NUMBER_ERROR);
					return DataRecoveryOperatingCondition.ABNORMAL_TERMINATION;
				}else if (analyzer.findByClass(DelDataException.class).isPresent()) {
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
			
			// Xác định trạng thái error
			if (condition == DataRecoveryOperatingCondition.ABNORMAL_TERMINATION) {
				return condition;
			}
		}
		saveLogDataRecoverServices.saveErrorLogDataRecover(dataRecoveryProcessId, "", "", null,
				TextResource.localize("CMF003_644"), "");
		return condition;
	}
	
	public DataRecoveryOperatingCondition exDataTabeRangeDate(DataRecoveryTable dataRecoveryTable,
			Optional<TableList> table, String dataRecoveryProcessId,
			HashMap<String, CSVBufferReader> csvByteReadMaper) throws ParseException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {


		DataRecoveryOperatingCondition condition = DataRecoveryOperatingCondition.FILE_READING_IN_PROGRESS;

		// khởi tạo csv Reader
		String filePath = getExtractDataStoragePath(dataRecoveryTable.getUploadId()) + "//" + dataRecoveryTable.getFileNameCsv() + ".csv";
		CSVBufferReader reader = new CSVBufferReader(new File(filePath));
		reader.setCharset("UTF-8");
		csvByteReadMaper.put(dataRecoveryTable.getFileNameCsv(), reader);
		
		condition = this.processRecoverListTblByCompanyHandle.recoverDataOneTable(table.get(),dataRecoveryProcessId,condition,dataRecoveryTable,csvByteReadMaper);
		
		return condition;
	}


	@SuppressWarnings("unchecked")
	public List<String> settingDate(Optional<TableList> tableList) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// 「テーブル一覧」の抽出キー区から日付項目を設定する
		List<String> checkKeyQuery = new ArrayList<>();
		List<String> resultsSetting = new ArrayList<>();
		int countY = 0, countYm = 0, countYmd = 0;

		Optional<Object> keyQuery = Optional.empty();

		if (tableList.isPresent()) {
			for (int i = 1; i < 11; i++) {
				Method m1 = TableList.class.getMethod(GET_CLS_KEY_QUERY + i);
				keyQuery = (Optional<Object>) m1.invoke(tableList.get());
				if (keyQuery.isPresent() && !((String) keyQuery.get()).isEmpty()) {
					checkKeyQuery.add((String) keyQuery.get());
				}
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
	
	private List<List<String>> sortByDate(List<List<String>> targetDataTable) {
		List<List<String>> dataTableCus = new ArrayList<>();
		for (List<String> list : targetDataTable) {
			dataTableCus.add(list);
		}
		dataTableCus.remove(0);
		Collections.sort(dataTableCus, (o1, o2) -> {
			if (StringUtil.isNullOrEmpty(o1.get(2), true)) {
				return StringUtil.isNullOrEmpty(o2.get(2), true) ? 0 : -1;
			}
			if (StringUtil.isNullOrEmpty(o2.get(2), true)) {
				return 1;
			}
			return o1.get(2).compareTo(o2.get(2));
		});
		return dataTableCus;
	}


	static InputStream createInputStreamFromFile(String fileId, String fileName) {
		String filePath = getExtractDataStoragePath(fileId) + "//" + fileName + ".csv";
		try {
			return new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static String getExtractDataStoragePath(String fileId) {
		return new FileStoragePath().getPathOfCurrentTenant().toString() + "//packs//" + fileId;
	}
	
}
