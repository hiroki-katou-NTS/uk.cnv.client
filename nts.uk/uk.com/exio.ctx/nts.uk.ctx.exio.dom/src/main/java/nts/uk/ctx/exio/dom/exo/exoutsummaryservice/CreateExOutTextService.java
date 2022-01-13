package nts.uk.ctx.exio.dom.exo.exoutsummaryservice;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.uk.ctx.exio.dom.exo.category.*;
import org.apache.commons.lang3.StringUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.i18n.I18NText;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.arc.layer.app.file.storage.StoredFileInfo;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.text.StringLength;
import nts.uk.ctx.exio.dom.exo.adapter.bs.employee.PersonInfoAdapter;
import nts.uk.ctx.exio.dom.exo.base.ItemType;
import nts.uk.ctx.exio.dom.exo.categoryitemdata.CtgItemData;
import nts.uk.ctx.exio.dom.exo.categoryitemdata.CtgItemDataRepository;
import nts.uk.ctx.exio.dom.exo.cdconvert.CdConvertDetail;
import nts.uk.ctx.exio.dom.exo.cdconvert.ConvertCode;
import nts.uk.ctx.exio.dom.exo.cdconvert.OutputCodeConvert;
import nts.uk.ctx.exio.dom.exo.cdconvert.OutputCodeConvertRepository;
import nts.uk.ctx.exio.dom.exo.commonalgorithm.AcquisitionExOutSetting;
import nts.uk.ctx.exio.dom.exo.commonalgorithm.OutCndDetailItemCustom;
import nts.uk.ctx.exio.dom.exo.condset.Delimiter;
import nts.uk.ctx.exio.dom.exo.condset.StandardAtr;
import nts.uk.ctx.exio.dom.exo.condset.StdOutputCondSet;
import nts.uk.ctx.exio.dom.exo.condset.StringFormat;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.AwDataFormatSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.CharacterDataFmSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.DateFormatSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.InstantTimeDataFmSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.NumberDataFmSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.dataformatsetting.TimeDataFmSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.init.AwDataFormatSet;
import nts.uk.ctx.exio.dom.exo.dataformat.init.ChacDataFmSet;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataFormatCharacterDigit;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataFormatDecimalDigit;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataFormatFixedValueOperation;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataFormatIntegerDigit;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataFormatNullReplacement;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataFormatSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataFormatSettingRepository;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DataTypeFixedValue;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DateFormatSet;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DateOutputFormat;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DecimalDivision;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DecimalPointClassification;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DecimalSelection;
import nts.uk.ctx.exio.dom.exo.dataformat.init.DelimiterSetting;
import nts.uk.ctx.exio.dom.exo.dataformat.init.EditSpace;
import nts.uk.ctx.exio.dom.exo.dataformat.init.FixedLengthEditingMethod;
import nts.uk.ctx.exio.dom.exo.dataformat.init.FixedValueOperationSymbol;
import nts.uk.ctx.exio.dom.exo.dataformat.init.HourMinuteClassification;
import nts.uk.ctx.exio.dom.exo.dataformat.init.InTimeDataFmSet;
import nts.uk.ctx.exio.dom.exo.dataformat.init.NextDayOutputMethod;
import nts.uk.ctx.exio.dom.exo.dataformat.init.NumberDataFmSet;
import nts.uk.ctx.exio.dom.exo.dataformat.init.PreviousDayOutputMethod;
import nts.uk.ctx.exio.dom.exo.dataformat.init.Rounding;
import nts.uk.ctx.exio.dom.exo.dataformat.init.TimeDataFmSet;
import nts.uk.ctx.exio.dom.exo.execlog.ExecutionForm;
import nts.uk.ctx.exio.dom.exo.execlog.ExterOutExecLog;
import nts.uk.ctx.exio.dom.exo.execlog.ExterOutExecLogRepository;
import nts.uk.ctx.exio.dom.exo.execlog.ExternalOutLog;
import nts.uk.ctx.exio.dom.exo.execlog.ExternalOutLogRepository;
import nts.uk.ctx.exio.dom.exo.execlog.ProcessingClassification;
import nts.uk.ctx.exio.dom.exo.execlog.ResultStatus;
import nts.uk.ctx.exio.dom.exo.execlog.StandardClassification;
import nts.uk.ctx.exio.dom.exo.execlog.UploadFileName;
import nts.uk.ctx.exio.dom.exo.executionlog.ExIoOperationState;
import nts.uk.ctx.exio.dom.exo.executionlog.ExOutOpMng;
import nts.uk.ctx.exio.dom.exo.executionlog.ExOutOpMngRepository;
import nts.uk.ctx.exio.dom.exo.outcnddetail.ConditionSymbol;
import nts.uk.ctx.exio.dom.exo.outcnddetail.OutCndDetail;
import nts.uk.ctx.exio.dom.exo.outcnddetail.OutCndDetailItem;
import nts.uk.ctx.exio.dom.exo.outputitem.CategoryItem;
import nts.uk.ctx.exio.dom.exo.outputitem.OperationSymbol;
import nts.uk.ctx.exio.dom.exo.outputitem.StandardOutputItem;
import nts.uk.ctx.exio.dom.exo.outputitem.StandardOutputItemRepository;
import nts.uk.ctx.exio.dom.exo.outputitemorder.StandardOutputItemOrder;
import nts.uk.ctx.exio.dom.exo.outputitemorder.StandardOutputItemOrderRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.japanese.JapaneseEraName;
import nts.uk.shr.com.time.japanese.JapaneseEras;
import nts.uk.shr.com.time.japanese.JapaneseErasAdapter;

@Stateless
public class CreateExOutTextService extends ExportService<Object> {

	@Inject
	private FileGenerator generator;

	@Inject
	private CtgItemDataRepository ctgItemDataRepo;

	@Inject
	private ExOutOpMngRepository exOutOpMngRepo;

	@Inject
	private ExterOutExecLogRepository exterOutExecLogRepo;

	@Inject
	private ExternalOutLogRepository externalOutLogRepo;

	@Inject
	private AcquisitionExOutSetting acquisitionExOutSetting;

	@Inject
	private ExOutCtgRepository exOutCtgRepo;

	@Inject
	private ExOutLinkTableRepository exCndOutputRepo;

	@Inject
	private StandardOutputItemRepository stdOutItemRepo;

	@Inject
	private StandardOutputItemOrderRepository stdOutItemOrderRepo;

	@Inject
	private OutputCodeConvertRepository outputCodeConvertRepo;

	@Inject
	private StatusOfEmploymentAdapter statusOfEmploymentAdapter;

	@Inject
	private DataFormatSettingRepository dataFormatSettingRepo;
	
	@Inject
	private JapaneseErasAdapter japaneseErasAdapter;
	
	@Inject
	private PersonInfoAdapter personInfoAdapter;
	
	@Inject
	private RegulationInfoEmployeeAdapter regulationInfoEmployeeAdapter;
	
	@Inject
	private FileStorage fileStorage;


	private final static String GET_ASSOCIATION = "getOutCondAssociation";
	private final static String GET_ITEM_NAME = "getOutCondItemName";
	private final static String USE_NULL_VALUE_ON = "on";
	private final static String USE_NULL_VALUE_OFF = "off";
	private final static String RESULT_OK = "ok";
	private final static String RESULT_NG = "ng";
	private final static String RESULT_STATE = "state";
	private final static String RESULT_VALUE = "value";
	private final static String ERROR_MESS = "ErrorMessage";
	private final static String ITEM_VALUE = "itemValue";
	private final static String USE_NULL_VALUE = "useNullValue";
	private final static String LINE_DATA_CSV = "lineDataCSV";
	private final static String yyyy_MM_dd = "yyyy-MM-dd";
	private final static String SELECT_COND = "select ";
	private final static String FROM_COND = " ";
	private final static String WHERE_COND = " where 1=1 ";
	private final static String AND_COND = " and ";
	private final static String ORDER_BY_COND = " order by ";
	private final static String ASC = " asc;";
	private final static String COMMA = ", ";
	private final static String DOT = ".";
	private final static String SLASH = "/";
	private final static String SPACE = " ";
	private final static String CID= "cid";
	private final static String CID_PARAM = "?cid";
	private final static String SID= "sid";
	private final static String SID_PARAM = "?sid";
	private final static String START_DATE = "STRDATE";
	private final static String START_DATE_PARAM = "?STRDATE";
	private final static String END_DATE = "ENDDATE";
	private final static String END_DATE_PARAM = "?ENDDATE";
	private final static String BASE_DATE = "BASEDATE";
	private final static String BASE_DATE_PARAM = "?BASEDATE";
	private final static String SYSTEM_DATE = "SYSDATE";
	private final static String SQL = "sql";
	private final static String CSV = ".csv";
	private final static String STEREO_TYPE = "csvfile";

	@Override
	protected void handle(ExportServiceContext<Object> context) {
		ExOutSetting domain = (ExOutSetting) context.getQuery();
		executeServerExOutManual(domain, context.getGeneratorContext(),domain.getReferenceDate());
	}

	public void executeServerExOutManual(ExOutSetting exOutSetting, FileGeneratorContext generatorContext,GeneralDate baseDate) {
		// サーバ外部出力手動実行
		if (exOutSetting.getExecuteForm().equals(ExecutionForm.MANUAL_EXECUTION)) {
			ExOutSettingResult settingResult = getServerExOutSetting(exOutSetting);
			if (settingResult == null) {
				finishFaultWhenStart(exOutSetting.getProcessingId());
				return;
			}
			initExOutLogInformation(exOutSetting, settingResult);
			serverExOutExecution(generatorContext, exOutSetting, settingResult, baseDate);
		}
		// サーバ外部出力自動実行
		else {
			// アルゴリズム「サーバ外部出力設定取得」を実行する
			ExOutSettingResult settingResult = getServerExOutSetting(exOutSetting);
			if (settingResult == null) {
				this.saveAutoExecutionLog(exOutSetting);
				return;
			}
			// アルゴリズム「サーバ外部出力ログ情報初期値」を実行する
			this.initExOutLogInformation(exOutSetting, settingResult);
			// 外部出力設定のカテゴリがデータ系
			settingResult.getExOutCtg().ifPresent(exOutCtg -> {
				if (exOutCtg.getCategorySet().equals(CategorySetting.DATA_TYPE)) {
					RegulationInfoEmployeeQueryImport queryDto = RegulationInfoEmployeeQueryImport.builder()
							.baseDate(this.toDateTime(exOutSetting.getReferenceDate()))
							.filterByClassification(false)
							.filterByClosure(false)
							.filterByDepartment(false)
							.filterByEmployment(false)
							.filterByJobTitle(false)
							.filterByWorkplace(false)
							.filterByWorktype(false)
							.includeIncumbents(true)
							.includeRetirees(false)
							.includeWorkersOnLeave(false)
							.includeOccupancy(false)
							.periodStart(this.toDateTime(exOutSetting.getStartDate()))
							.periodEnd(this.toDateTime(exOutSetting.getEndDate()))
							.referenceRange(SearchReferenceRange.ALL_REFERENCE_RANGE.value)
							.systemType(CCG001SystemType.EMPLOYMENT.value)
							.build();
					// アルゴリズム「管理者条件で社員を検索して並び替える」を実行する
					List<String> empIds = this.regulationInfoEmployeeAdapter
							.findEmployees(queryDto)
							.stream()
							.map(RegulationInfoEmployeeImport::getEmployeeId)
							.collect(Collectors.toList());
					exOutSetting.setSidList(empIds);
				}
			});
			// アルゴリズム「サーバ外部出力実行」を実行する
			this.serverExOutExecution(generatorContext, exOutSetting, settingResult, baseDate);
			// ドメインモデル「外部出力動作管理」を削除する
			this.exOutOpMngRepo.remove(exOutSetting.getProcessingId());
		}
	}

	// サーバ外部出力設定取得
	private ExOutSettingResult getServerExOutSetting(ExOutSetting exOutSetting) {
		List<StdOutputCondSet> stdOutputCondSetList = acquisitionExOutSetting.getExOutSetting(null,
				StandardAtr.STANDARD, exOutSetting.getConditionSetCd());

		if (stdOutputCondSetList.size() == 0)
			return null;

		StdOutputCondSet stdOutputCondSet = stdOutputCondSetList.get(0);

		Optional<OutCndDetail> cndDetailOtp = acquisitionExOutSetting.getExOutCond(exOutSetting.getConditionSetCd(),
				null, StandardAtr.STANDARD, true, null);
		List<OutCndDetailItem> outCndDetailItemList = cndDetailOtp.isPresent()
				? cndDetailOtp.get().getListOutCndDetailItem() : Collections.emptyList();

		List<OutputItemCustom> outputItemCustomList = getExOutItemList(exOutSetting.getConditionSetCd(), null, "",
				StandardAtr.STANDARD, true);

		List<CtgItemData> ctgItemDataList = new ArrayList<CtgItemData>();
		for (OutputItemCustom outputItemCustom : outputItemCustomList) {
			ctgItemDataList.addAll(outputItemCustom.getCtgItemDataList());
		}

		Optional<ExOutCtg> exOutCtg = exOutCtgRepo.getExOutCtgByIdAndCtgSetting(stdOutputCondSet.getCategoryId().v());
		Optional<ExOutLinkTable> exCndOutput = exCndOutputRepo.getExCndOutputById(stdOutputCondSet.getCategoryId().v());

		return new ExOutSettingResult(stdOutputCondSet, outCndDetailItemList, exOutCtg, exCndOutput,
				outputItemCustomList, ctgItemDataList);
	}

	private void finishFaultWhenStart(String processingId) {
		ExOutOpMng exOutOpMng = new ExOutOpMng(processingId, 0, 0, 0, NotUseAtr.NOT_USE.value, "",
				ExIoOperationState.FAULT_FINISH.value);
		exOutOpMngRepo.add(exOutOpMng);
	}

	// サーバ外部出力ログ情報初期値
	private void initExOutLogInformation(ExOutSetting exOutSetting, ExOutSettingResult settingResult) {
		String companyId = AppContexts.user().companyId();
		String sid = AppContexts.user().employeeId();
		String processingId = exOutSetting.getProcessingId();

		int proCnt = 0;
		int errCnt = 0;
		int totalProCnt = 0;
		int doNotInterrupt = NotUseAtr.NOT_USE.value;
		String proUnit = "";
		int opCond = ExIoOperationState.IN_PREPARATION.value;
		ExOutOpMng exOutOpMng = new ExOutOpMng(processingId, proCnt, errCnt, totalProCnt, doNotInterrupt, proUnit,
				opCond);
		Optional<ExOutCtg> exOutCtgOpt = settingResult.getExOutCtg();
		OutingPeriodClassific outingPeriodClassific = null;
		ClassificationToUse classificationToUse =null;
		if(exOutCtgOpt.isPresent()){
			val exOutCtg = exOutCtgOpt.get();
			outingPeriodClassific = exOutCtg.getOutingPeriodClassific();
			classificationToUse = 	exOutCtg.getClassificationToUse();
		}

		String userId = null;
		int totalErrorCount = 0;
		int totalCount = 0;
		String fileId = null;
		Long fileSize = null;
		int deleteFile = NotUseAtr.USE.value;
		String fileName = null;
		Integer categoryID = (settingResult.getStdOutputCondSet() != null)
				? settingResult.getStdOutputCondSet().getCategoryId().v() : null;
		String processUnit = null;
		GeneralDateTime processEndDateTime = null;
		GeneralDateTime processStartDateTime = GeneralDateTime.now();
		int standardClass = StandardClassification.STANDARD.value;
		int executeForm = exOutSetting.getExecuteForm().value;
		String executeId = sid;
		GeneralDate designatedReferenceDate = exOutSetting.getReferenceDate();
		GeneralDate specifiedEndDate = exOutSetting.getEndDate();
		GeneralDate specifiedStartDate = exOutSetting.getStartDate();
		if( outingPeriodClassific == OutingPeriodClassific.YEAR_MONTH){
			specifiedEndDate = specifiedEndDate.addDays(1);
		}
		String codeSettingCondition = exOutSetting.getConditionSetCd();
		Integer resultStatus = null;
		String nameSetting = settingResult.getStdOutputCondSet() == null ? 
				"" : settingResult.getStdOutputCondSet().getConditionSetName().v();
		ExterOutExecLog exterOutExecLog = new ExterOutExecLog(companyId, processingId, userId, totalErrorCount,
				totalCount, fileId, fileSize, deleteFile, fileName, categoryID, processUnit, processEndDateTime,
				processStartDateTime, standardClass, executeForm, executeId, designatedReferenceDate, specifiedEndDate,
				specifiedStartDate, codeSettingCondition, resultStatus, nameSetting);

		String errorContent = I18NText.getText("CMF002_524");
		String errorTargetValue = null;
		GeneralDate errorDate = null;
		String errorEmployee = null;
		String errorItem = null;
		GeneralDateTime logRegisterDateTime = GeneralDateTime.now();
		int logSequenceNumber = 0;
		int processCount = 0;
		int processContent = ProcessingClassification.START_PROCESSING.value;
		ExternalOutLog externalOutLog = new ExternalOutLog(companyId, processingId, errorContent, errorTargetValue,
				errorDate, errorEmployee, errorItem, logRegisterDateTime, logSequenceNumber, processCount,
				processContent);


		exOutOpMngRepo.add(exOutOpMng);
		exterOutExecLogRepo.add(exterOutExecLog);
		externalOutLogRepo.add(externalOutLog);
	}

	// サーバ外部出力実行
	private void serverExOutExecution(FileGeneratorContext generatorContext, ExOutSetting exOutSetting,
			ExOutSettingResult settingResult,GeneralDate baseDate) {

		String processingId = exOutSetting.getProcessingId();
		OperationStateResult state;
		Optional<ExOutCtg> exOutCtg = settingResult.getExOutCtg();
		Optional<ExOutLinkTable> exCndOutput = settingResult.getExCndOutput();
		StdOutputCondSet stdOutputCondSet = settingResult.getStdOutputCondSet();
		String settingName = "";
		if (stdOutputCondSet != null)
			settingName = stdOutputCondSet.getConditionSetName().v();
		String fileName = exOutSetting.getConditionSetCd() + settingName + GeneralDateTime.now().toString("yyyyMMddHHmmss");

		Optional<ExOutOpMng> exOutOpMngOptional = exOutOpMngRepo.getExOutOpMngById(processingId);
		if (!exOutOpMngOptional.isPresent()) {
			state = new OperationStateResult(ExIoOperationState.FAULT_FINISH);
			createOutputLogInfoEnd(generatorContext, processingId, state, fileName);
			return;
		}

		ExOutOpMng exOutOpMng = exOutOpMngOptional.get();
		exOutOpMng.setOpCond(ExIoOperationState.EXPORTING);
		if ((stdOutputCondSet == null) || !exOutCtg.isPresent() || !exCndOutput.isPresent()
				|| (!exCndOutput.get().getForm1().isPresent() && !exCndOutput.get().getForm2().isPresent())) {
			state = new OperationStateResult(ExIoOperationState.FAULT_FINISH);
			createOutputLogInfoEnd(generatorContext, processingId, state, fileName);
			return;
		}

		if (exOutCtg.get().getCategorySet() == CategorySetting.DATA_TYPE) {
			exOutOpMng.setProUnit(I18NText.getText("CMF002_527"));
			exOutOpMng.setProCnt(0);
			exOutOpMng.setTotalProCnt(exOutSetting.getSidList().size());
		} else {
			exOutOpMng.setProUnit(I18NText.getText("CMF002_528"));
			exOutOpMng.setProCnt(0);
			exOutOpMng.setTotalProCnt(exOutSetting.getSidList().size());
		}

		exOutOpMngRepo.update(exOutOpMng);

		CategorySetting type = exOutCtg.get().getCategorySet();
		state = serverExOutTypeDataOrMaster(type, generatorContext, exOutSetting, settingResult, fileName, baseDate);

		createOutputLogInfoEnd(generatorContext, processingId, state, fileName);
	}

	// サーバ外部出力ログ情報終了値
	private void createOutputLogInfoEnd(FileGeneratorContext generatorContext, String processingId,
			OperationStateResult operationState, String fileName) {
		Optional<ExOutOpMng> exOutOpMng = exOutOpMngRepo.getExOutOpMngById(processingId);

		if (!exOutOpMng.isPresent())
			return;
		exOutOpMng.get().setOpCond(operationState.getState());
		exOutOpMngRepo.update(exOutOpMng.get());

		String companyId = AppContexts.user().companyId();
		String outputProcessId = processingId;
		String errorContent = I18NText.getText("CMF002_525");
		String errorTargetValue = null;
		GeneralDate errorDate = null;
		String errorEmployee = null;
		String errorItem = null;
		GeneralDateTime logRegisterDateTime = GeneralDateTime.now();
		int logSequenceNumber = exOutOpMng.get().getErrCnt() + 1;
		int processCount = exOutOpMng.get().getProCnt();
		int processContent = ProcessingClassification.END_PROCESSING.value;

		ExternalOutLog externalOutLog = new ExternalOutLog(companyId, outputProcessId, errorContent, errorTargetValue,
				errorDate, errorEmployee, errorItem, logRegisterDateTime, logSequenceNumber, processCount,
				processContent);
		externalOutLogRepo.add(externalOutLog);

		ResultStatus statusEnd;
		switch (operationState.getState()) {
		case INTER_FINISH:
			statusEnd = ResultStatus.INTERRUPTION;
			break;
		case EXPORT_FINISH:
			statusEnd = ResultStatus.SUCCESS;
			break;
		default:
			statusEnd = ResultStatus.FAILURE;
			break;
		}
		
		String fileId = operationState.getFileId();
		Optional<ExterOutExecLog> exterOutExecLogOptional = exterOutExecLogRepo.getExterOutExecLogById(companyId,
				processingId);
		if (!exterOutExecLogOptional.isPresent())
			return;

		ExterOutExecLog exterOutExecLog = exterOutExecLogOptional.get();
		exterOutExecLog.setProcessEndDateTime(Optional.of(GeneralDateTime.now()));
		exterOutExecLog.setFileId(Optional.ofNullable(fileId));
		exterOutExecLog.setFileName(statusEnd == ResultStatus.INTERRUPTION ? Optional.of(new UploadFileName("")) : Optional.of(new UploadFileName(fileName)));
		exterOutExecLog.setTotalCount(exOutOpMng.get().getProCnt());
		exterOutExecLog.setTotalErrorCount(exOutOpMng.get().getErrCnt());
		exterOutExecLog.setProcessUnit(Optional.of(exOutOpMng.get().getProUnit()));
		exterOutExecLog.setResultStatus(Optional.of(statusEnd));
		if (statusEnd == ResultStatus.SUCCESS)
			exterOutExecLog.setDeleteFile(NotUseAtr.NOT_USE);

		exterOutExecLogRepo.update(exterOutExecLog);
	}

	
	@SuppressWarnings("unchecked")
	private OperationStateResult serverExOutTypeDataOrMaster(CategorySetting type, FileGeneratorContext generatorContext,
			ExOutSetting exOutSetting, ExOutSettingResult settingResult, String fileName,GeneralDate baseDate) {
		String loginSid = AppContexts.user().employeeId();
		Map<String,String> header = new LinkedHashMap<>();
		List<Map<String, Object>> csvData = new ArrayList<>();
		StdOutputCondSet stdOutputCondSet = (StdOutputCondSet) settingResult.getStdOutputCondSet();
		List<OutputItemCustom> outputItemCustomList = settingResult.getOutputItemCustomList();
		Map<String, Object> lineDataResult;
		Map<String, Object> lineDataCSV;
		String stateResult;
		String condSetName = null;
		boolean drawHeader = true;
		Delimiter delimiter = Delimiter.COMMA;
		StringFormat stringFormat = StringFormat.NONE;

		// サーバ外部出力ファイル項目ヘッダ
		if (stdOutputCondSet != null) {
			condSetName = (stdOutputCondSet.getConditionOutputName() == NotUseAtr.USE) ? (stdOutputCondSet.getConditionSetCode().v() + " " + stdOutputCondSet.getConditionSetName().v()) : null;
			drawHeader = stdOutputCondSet.getItemOutputName() == NotUseAtr.USE;
			delimiter = stdOutputCondSet.getDelimiter();
			stringFormat = stdOutputCondSet.getStringFormat();
		}
		
		// fixbug 102767
		// if(delimiter == Delimiter.COMMA) {
		fileName = fileName + CSV;
		// }
		
		for (OutputItemCustom outputItemCustom : outputItemCustomList) {
			String outputName = stringFormat.character + outputItemCustom.getStandardOutputItem().getOutputItemName().v() + stringFormat.character;
			if (stringFormat == StringFormat.SINGLE_QUOTATION) {
				outputName = stringFormat.character + outputName;
			}
			header.put(outputItemCustom.getStandardOutputItem().getOutputItemCode().v(),outputName);
		}

		Map<String, String> sqlAndParam;
		List<List<String>> data;

		// サーバ外部出力タイプデータ系
		if (type == CategorySetting.DATA_TYPE) {
			for (String sid : exOutSetting.getSidList()) {
				ExIoOperationState checkResult = checkInterruptAndIncreaseProCnt(exOutSetting.getProcessingId());
				if ((checkResult == ExIoOperationState.FAULT_FINISH)
						|| (checkResult == ExIoOperationState.INTER_FINISH))
					return new OperationStateResult(checkResult);
				try {
					sqlAndParam = getExOutDataSQL(sid, true, exOutSetting, settingResult);
					data = exOutCtgRepo.getData(sqlAndParam);
					
					for (List<String> lineData : data) {
						lineDataResult = fileLineDataCreation(exOutSetting.getProcessingId(), lineData,
								outputItemCustomList, sid, stringFormat, baseDate, type);
						stateResult = (String) lineDataResult.get(RESULT_STATE);
						lineDataCSV = (Map<String, Object>) lineDataResult.get(LINE_DATA_CSV);
						if ((lineDataCSV != null) && RESULT_OK.equals(stateResult))
							csvData.add(lineDataCSV); 
					}
				} catch (Exception e) {
					e.printStackTrace();
					String str = e.getMessage();
					str.indexOf("Internal Exception");
					str.indexOf("Error Code");
					createOutputLogError(exOutSetting.getProcessingId(), str.substring(str.indexOf("Internal Exception"), str.indexOf("Error Code")-2).trim().split(":")[2], null, sid, null);
				}
			}
			// サーバ外部出力タイプマスター系
		} else {
			try {
				sqlAndParam = getExOutDataSQL(null, false, exOutSetting, settingResult);
				data = exOutCtgRepo.getData(sqlAndParam);
			
				Optional<ExOutOpMng> exOutOpMngOptional = exOutOpMngRepo.getExOutOpMngById(exOutSetting.getProcessingId());
				if (!exOutOpMngOptional.isPresent()) {
					return new OperationStateResult(ExIoOperationState.FAULT_FINISH);
				}
	
				ExOutOpMng exOutOpMng = exOutOpMngOptional.get();
				exOutOpMng.setProCnt(0);
				exOutOpMng.setTotalProCnt(data.size());
				exOutOpMngRepo.update(exOutOpMng);
	
				for (List<String> lineData : data) {
					ExIoOperationState checkResult = checkInterruptAndIncreaseProCnt(exOutSetting.getProcessingId());
					if ((checkResult == ExIoOperationState.FAULT_FINISH)
							|| (checkResult == ExIoOperationState.INTER_FINISH))
						return new OperationStateResult(checkResult);
					lineDataResult = fileLineDataCreation(exOutSetting.getProcessingId(), lineData, outputItemCustomList,
							loginSid, stringFormat,baseDate, type);
					stateResult = (String) lineDataResult.get(RESULT_STATE);
					lineDataCSV = (Map<String, Object>) lineDataResult.get(LINE_DATA_CSV);
					if (RESULT_OK.equals(stateResult) && (lineDataCSV != null))
						csvData.add(lineDataCSV);
				}
			} catch (Exception e) {
				e.printStackTrace();
				String str = e.getMessage();
				str.indexOf("Internal Exception");
				str.indexOf("Error Code");
				createOutputLogError(exOutSetting.getProcessingId(), str.substring(str.indexOf("Internal Exception"), str.indexOf("Error Code")-2).trim().split(":")[2], null, null, null);
			}
		}

		FileData fileData = new FileData(fileName, header, csvData);
		generator.generate(generatorContext, fileData, condSetName, drawHeader, delimiter);

		// create file
		Path pathTemp = generatorContext.getWorkingFiles().get(0).getTempFile().getPath();
		StoredFileInfo fileInfo = fileStorage.store(pathTemp, fileName, STEREO_TYPE);

		return new OperationStateResult(ExIoOperationState.EXPORT_FINISH, fileInfo.getId());
	}

	public ExIoOperationState checkInterruptAndIncreaseProCnt(String processingId) {
		ExIoOperationState result = ExIoOperationState.EXPORTING;
		Optional<ExOutOpMng> exOutOpMng = exOutOpMngRepo.getExOutOpMngById(processingId);
		if (!exOutOpMng.isPresent()) {
			return ExIoOperationState.FAULT_FINISH;
		}

		if (exOutOpMng.get().getDoNotInterrupt() == NotUseAtr.USE) {
			return ExIoOperationState.INTER_FINISH;
		}

		exOutOpMng.get().setProCnt(exOutOpMng.get().getProCnt() + 1);
		exOutOpMngRepo.update(exOutOpMng.get());

		return result;
	}

	// サーバ外部出力データ取得SQL
	@SuppressWarnings("unchecked")
	private Map<String, String> getExOutDataSQL(String sid, boolean isdataType, ExOutSetting exOutSetting,
			ExOutSettingResult settingResult) throws ReflectiveOperationException {
		String cid = AppContexts.user().companyId();
		StringBuilder sql = new StringBuilder();
		String sidAlias = null;
		List<String> keyOrderList = new ArrayList<String>();
		Optional<ExOutCtg> exOutCtgOpt = settingResult.getExOutCtg();
		OutingPeriodClassific outingPeriodClassific = null;
		ClassificationToUse classificationToUse =null;
		if(exOutCtgOpt.isPresent()){
			val exOutCtg = exOutCtgOpt.get();
			 outingPeriodClassific = exOutCtg.getOutingPeriodClassific();
			 classificationToUse = 	exOutCtg.getClassificationToUse();
		}

		Map<String, String> sqlAndParams = new HashMap<String, String>();
		sqlAndParams.put(START_DATE, exOutSetting.getStartDate().toString(yyyy_MM_dd));
		sqlAndParams.put(END_DATE, exOutSetting.getEndDate().toString(yyyy_MM_dd));
		sqlAndParams.put(BASE_DATE, exOutSetting.getReferenceDate().toString(yyyy_MM_dd));
		sqlAndParams.put(SYSTEM_DATE, GeneralDate.today().toString(yyyy_MM_dd));
		sqlAndParams.put(CID, cid);
		sqlAndParams.put(SID, sid);
		
		sql.append(SELECT_COND);

		List<CtgItemData> ctgItemDataList = settingResult.getCtgItemDataList();
		for (CtgItemData ctgItemData : ctgItemDataList) {
			sql.append(ctgItemData.getTblAlias());
			sql.append(DOT);
			sql.append(ctgItemData.getFieldName());
			sql.append(COMMA);
		}
		
		// delete a comma after for
		if(!ctgItemDataList.isEmpty()) {
			sql.setLength(sql.length() - COMMA.length());
		}else {
			sql.append(" * ");
		}
		sql.append(FROM_COND);

		Optional<ExOutLinkTable> exOutLinkTable = settingResult.getExCndOutput();
		if (exOutLinkTable.isPresent()) {
			ExOutLinkTable item = exOutLinkTable.get();
			
			if (item.getForm1().isPresent()) {
				sql.append(item.getForm1().get().v());
			}
			
			sql.append(SPACE);
			
			if (item.getForm2().isPresent()) {
				sql.append(item.getForm2().get().v());
			}
			
			sql.append(WHERE_COND);

			boolean isDate = false;
			boolean isOutDate = false;
			String startDateItemName = "";
			String endDateItemName = "";
			Method getAssociation;
			Method getItemName;
			Optional<Association> asssociation;
			Optional<PhysicalProjectName> itemName;
			
			for (int i = 1; i < 11; i++) {
				getAssociation = ExOutLinkTable.class.getMethod(GET_ASSOCIATION + i);
				getItemName = ExOutLinkTable.class.getMethod(GET_ITEM_NAME + i);

				asssociation = (Optional<Association>) getAssociation.invoke(item);
				itemName = (Optional<PhysicalProjectName>) getItemName.invoke(item);

				if (!asssociation.isPresent() || !itemName.isPresent()) {
					continue;
				}

				if (asssociation.get() == Association.CID) {
					createWhereCondition(sql, itemName.get().v(), "=", CID_PARAM);
				} else if (asssociation.get() == Association.SID) {
					sidAlias = itemName.get().v();
					createWhereCondition(sql, itemName.get().v(), "=", SID_PARAM);
				} else if (asssociation.get() == Association.DATE) {
					if (!isDate) {
						// 出力条件関連付　＝　7（日付(年月日)）の場合
						isDate = true;
						startDateItemName = itemName.get().v();
					} else {
						//出力条件関連付　＝　7（日付(年月日)）が２つ存在する場合
						isOutDate = true;
						endDateItemName = itemName.get().v();
					}
				}
			}

			if (isOutDate) {
				StringBuilder subSqlStart = new StringBuilder();
				StringBuilder subSqlEtart = new StringBuilder();
				//4-1．「外部出力カテゴリ．外部出期間区分」=【年月】の場合
				//　「出力条件項目名3（開始）　≧　処理期間.開始年月（yyyyMM）
				//　　出力条件項目名4（終了）　≦　処理期間.終了年月（yyyyMM）」
				//　4-2．「外部出力カテゴリ．外部出期間区分」=【年月日】の場合
				//　「出力条件項目名3（開始）　≧　処理期間.開始日付（yyyy/MM/dd）
				//　　出力条件項目名4（終了）　≦　処理期間.終了日付（yyyy/MM/dd）」

				//　4-3．「外部出力カテゴリ．外部出期間区分」=【基準日】の場合
				//　「出力条件項目名3（開始）　≧　基準日（yyyy/MM/dd）
				//　　出力条件項目名4（終了）　≦　基準日（yyyy/MM/dd）」
				// Update to # 121029
				// TH 4-3．「外部出力カテゴリ．外部出期間区分」=【基準日】
				//「出力条件項目名3（開始）　≦　基準日（yyyy/MM/dd）
				//　出力条件項目名4（終了）　≧　基準日（yyyy/MM/dd）」


				if(outingPeriodClassific == OutingPeriodClassific.YEAR_MONTH){
					subSqlStart.append("CONCAT(SUBSTRING(CAST(");
					subSqlEtart.append("CONCAT(SUBSTRING(CAST(");
					subSqlStart.append(startDateItemName);
					subSqlEtart.append(endDateItemName);
					subSqlStart.append(" as varchar), 0, 5),'-',SUBSTRING(CAST(");
					subSqlStart.append(startDateItemName);
					subSqlStart.append(" as varchar), 5,7),'-','01')");
					subSqlEtart.append(" as varchar), 0, 5),'-',SUBSTRING(CAST(");
					subSqlEtart.append(endDateItemName);
					subSqlEtart.append(" as varchar), 5,7),'-','01')");
					createWhereCondition(sql, subSqlStart.toString(), " >= ", START_DATE_PARAM);
					createWhereCondition(sql, subSqlEtart.toString(), " <= ", END_DATE_PARAM);

				} else if(outingPeriodClassific == OutingPeriodClassific.DATE){
					createWhereCondition(sql, startDateItemName, " >= ", START_DATE_PARAM);
					createWhereCondition(sql, endDateItemName, " <= ", END_DATE_PARAM);
			    }else if(outingPeriodClassific == OutingPeriodClassific.REFERENCE_DATE){
					createWhereCondition(sql, startDateItemName, " <= ", BASE_DATE_PARAM);
					createWhereCondition(sql, endDateItemName, " >= ", BASE_DATE_PARAM);
				}
			} else if (isDate) {
				StringBuilder subSql = new StringBuilder();
				if(outingPeriodClassific == OutingPeriodClassific.YEAR_MONTH){
					//3-1．「外部出力カテゴリ．外部出期間区分」=【年月】の場合
					//　「出力条件項目名　≧　処理期間.開始年月（yyyyMM）
					//　　出力条件項目名　≦　処理期間.終了年月（yyyyMM）」
					//    Convert to YM
					//	  DATEADD(MONTH, DATEDIFF(MONTH, 0, <dateField>), 0)
					subSql.append("CONCAT(SUBSTRING(CAST(");
					subSql.append(startDateItemName);
					subSql.append(" as varchar), 0, 5),'-',SUBSTRING(CAST(");
					subSql.append(startDateItemName);
					subSql.append(" as varchar), 5,7),'-','01')");
					createWhereCondition(sql, subSql.toString(), " >= ", START_DATE_PARAM);
					createWhereCondition(sql, subSql.toString(), " <= ", END_DATE_PARAM);

				}else if(outingPeriodClassific == OutingPeriodClassific.DATE){
					createWhereCondition(sql, startDateItemName, " >= ", START_DATE_PARAM);
					createWhereCondition(sql, startDateItemName, " <= ", END_DATE_PARAM);
				}else if(outingPeriodClassific == OutingPeriodClassific.REFERENCE_DATE){
					createWhereCondition(sql, startDateItemName, " = ", BASE_DATE_PARAM);
				}
			}

			if (exOutLinkTable.get().getConditions().isPresent()
					&& (exOutLinkTable.get().getConditions().get().v().length() > 0)) {
				sql.append(AND_COND);
				sql.append(exOutLinkTable.get().getConditions().get().v());
			}

			String value = "";
			String value1 = "";
			String value2 = "";
			String operator = "";
			String searchCodeListCond;

			List<OutCndDetailItem> outCndDetailItemList = settingResult.getOutCndDetailItem();
			OutCndDetailItemCustom outCndDetailItemCustom;
			
			for (OutCndDetailItem outCndDetailItem : outCndDetailItemList) {
				outCndDetailItemCustom = (OutCndDetailItemCustom) outCndDetailItem;
				searchCodeListCond = (outCndDetailItemCustom.getJoinedSearchCodeList() != null)
						? outCndDetailItemCustom.getJoinedSearchCodeList() : "";

				operator = outCndDetailItem.getConditionSymbol().operator;

				if (outCndDetailItemCustom.getCtgItemData().isPresent()) {
					CtgItemData ctgItemData = outCndDetailItemCustom.getCtgItemData().get();
					
					switch (ctgItemData.getDataType()) {
					case NUMERIC:
						value = outCndDetailItem.getSearchNum().map(i -> i.v().toString()).orElse("");
						value1 = outCndDetailItem.getSearchNumStartVal().map(i -> i.v().toString()).orElse("");
						value2 = outCndDetailItem.getSearchNumEndVal().map(i -> i.v().toString()).orElse("");
						break;
					case CHARACTER:
						if (outCndDetailItem.getConditionSymbol() != ConditionSymbol.CONTAIN) {
							value = "'" + outCndDetailItem.getSearchChar().map(i -> i.v()).orElse("") + "'";
							value1 = "'" + outCndDetailItem.getSearchCharStartVal().map(i -> i.v()).orElse("") + "'";
							value2 = "'" + outCndDetailItem.getSearchCharEndVal().map(i -> i.v()).orElse("") + "'";
						} else {
							value = "'%" + outCndDetailItem.getSearchChar().map(i -> i.v()).orElse("") + "%'";
							value1 = "'%" + outCndDetailItem.getSearchCharStartVal().map(i -> i.v()).orElse("") + "%'";
							value2 = "'%" + outCndDetailItem.getSearchCharEndVal().map(i -> i.v()).orElse("") + "%'";
						}
						break;
					case DATE:
						value = "'" + outCndDetailItem.getSearchDate().map(i -> i.toString(yyyy_MM_dd)).orElse("") + "'";
						value1 = "'" + outCndDetailItem.getSearchDateStart().map(i -> i.toString(yyyy_MM_dd)).orElse("")
								+ "'";
						value2 = "'" + outCndDetailItem.getSearchDateEnd().map(i -> i.toString(yyyy_MM_dd)).orElse("")
								+ "'";
						break;
					case TIME:
						value = outCndDetailItem.getSearchTime().map(i -> i.v().toString()).orElse("");
						value1 = outCndDetailItem.getSearchTimeStartVal().map(i -> i.v().toString()).orElse("");
						value2 = outCndDetailItem.getSearchTimeEndVal().map(i -> i.v().toString()).orElse("");
						break;
					case INS_TIME:
						value = outCndDetailItem.getSearchClock().map(i -> i.v().toString()).orElse("");
						value1 = outCndDetailItem.getSearchClockStartVal().map(i -> i.v().toString()).orElse("");
						value2 = outCndDetailItem.getSearchClockEndVal().map(i -> i.v().toString()).orElse("");
						break;

					default:
						break;
					}

					if ((outCndDetailItem.getConditionSymbol() == ConditionSymbol.IN)
							|| (outCndDetailItem.getConditionSymbol() == ConditionSymbol.NOT_IN))
						value = "(" + searchCodeListCond + ")";

					if (outCndDetailItem.getConditionSymbol() == ConditionSymbol.BETWEEN) {
						createWhereCondition(sql, ctgItemData.getTblAlias(), ctgItemData.getFieldName(), " >= ",
								value1);
						createWhereCondition(sql, ctgItemData.getTblAlias(), ctgItemData.getFieldName(), " <= ",
								value2);
					} else {
						createWhereCondition(sql, ctgItemData.getTblAlias(), ctgItemData.getFieldName(), operator,
								value);
					}

					ctgItemData.getPrimarykeyClassfication().ifPresent(primaryKey -> {
						if (primaryKey == NotUseAtr.USE) {
							keyOrderList.add(ctgItemData.getTblAlias() + DOT + ctgItemData.getFieldName());
						}
					});
				}
			}
		}

		if (isdataType) {
			if (sidAlias != null) {
				sql.append(ORDER_BY_COND);
				sql.append(sidAlias);
				sql.append(ASC);
			}
		} else {
			if (!keyOrderList.isEmpty()) {
				sql.append(ORDER_BY_COND);
				sql.append(String.join(COMMA, keyOrderList));
				sql.append(ASC);
			}
		}

		sqlAndParams.put(SQL , sql.toString());
		
		return sqlAndParams;
	}

	// サーバ外部出力ファイル行データ作成
	private Map<String, Object> fileLineDataCreation(String processingId, List<String> lineData,
			List<OutputItemCustom> outputItemCustomList, String sid, StringFormat stringFormat,GeneralDate baseDate, CategorySetting type) {

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> lineDataCSV = new HashMap<String, Object>();
		String targetValue = "";
		NotUseAtr isfixedValue = NotUseAtr.NOT_USE;
		String fixedValue = "";
		boolean isSetNull = false;
		String nullValueReplace = "";
		DataFormatSetting dataFormatSetting;
		Map<String, String> fileItemDataCreationResult;
		Map<String, String> fileItemDataCheckedResult;

		String useNullValue;
		String resultState;
		String errorMess;
		int index = 0;

		for (OutputItemCustom outputItemCustom : outputItemCustomList) {
			dataFormatSetting = outputItemCustom.getDataFormatSetting();

			isfixedValue = dataFormatSetting.getFixedValue();
			fixedValue = dataFormatSetting.getValueOfFixedValue().map(item -> item.v()).orElse("");
			isSetNull = (dataFormatSetting.getNullValueReplace() == NotUseAtr.USE);
			nullValueReplace = dataFormatSetting.getValueOfNullValueReplace().map(item -> item.v()).orElse("");

			if (isfixedValue == NotUseAtr.USE) {
				if(stringFormat == StringFormat.SINGLE_QUOTATION) {
					targetValue = stringFormat.character + stringFormat.character +fixedValue + stringFormat.character;
				}else if(stringFormat == StringFormat.DOUBLE_QUOTATION) {
					targetValue = stringFormat.character+ fixedValue +stringFormat.character ;
				}else{
					targetValue = fixedValue;
				}
				lineDataCSV.put(outputItemCustom.getStandardOutputItem().getOutputItemName().v(), targetValue);
				index += outputItemCustom.getCtgItemDataList().size();
				continue;
			}

			fileItemDataCreationResult = fileItemDataCreation(lineData, outputItemCustom, isSetNull, nullValueReplace,
					index);
			targetValue = fileItemDataCreationResult.get(ITEM_VALUE);
			useNullValue = fileItemDataCreationResult.get(USE_NULL_VALUE);

			if (useNullValue == USE_NULL_VALUE_ON || StringUtils.isEmpty(targetValue)) {
				if(outputItemCustom.getStandardOutputItem().getItemType() != ItemType.NUMERIC) {
					if(stringFormat == StringFormat.SINGLE_QUOTATION) {
						targetValue = stringFormat.character +stringFormat.character + stringFormat.character;
					}else if(stringFormat == StringFormat.DOUBLE_QUOTATION) {
						targetValue = stringFormat.character +stringFormat.character ;
					}
				}
				lineDataCSV.put(outputItemCustom.getStandardOutputItem().getOutputItemName().v(), targetValue);
				index += outputItemCustom.getCtgItemDataList().size();
				continue;
			}

			fileItemDataCheckedResult = checkOutputFileType(targetValue,
					outputItemCustom.getStandardOutputItem().getItemType(), outputItemCustom.getDataFormatSetting(),
					sid,baseDate);
			resultState = fileItemDataCheckedResult.get(RESULT_STATE);
			errorMess = fileItemDataCheckedResult.get(ERROR_MESS);
			targetValue = fileItemDataCheckedResult.get(RESULT_VALUE);

			if (RESULT_NG.equals(resultState)) {
				// Do khong biet nghiep vu. nen fix the nay cho bug 105279
				if(type != CategorySetting.DATA_TYPE) sid = null;
				
				createOutputLogError(processingId, errorMess, targetValue, sid,
						outputItemCustom.getStandardOutputItem().getOutputItemName().v());
				result.put(RESULT_STATE, RESULT_NG);
				result.put(LINE_DATA_CSV, lineDataCSV);
				return lineDataCSV;
			}
			
			if(outputItemCustom.getStandardOutputItem().getItemType() != ItemType.NUMERIC) {
				targetValue = stringFormat.character + targetValue + stringFormat.character;
				
				if(stringFormat == StringFormat.SINGLE_QUOTATION) {
					targetValue = stringFormat.character + targetValue;
				}
			}
			
			lineDataCSV.put(outputItemCustom.getStandardOutputItem().getOutputItemCode().v(), targetValue);
			index += outputItemCustom.getCtgItemDataList().size();
		}

		result.put(RESULT_STATE, RESULT_OK);
		result.put(LINE_DATA_CSV, lineDataCSV);
		return result;
	}

	//サーバ外部出力ログエラー作成
	private void createOutputLogError(String processingId, String errorContent, String targetValue, String sid,
			String errorItem) {
		String employeeCode = null;
		if (sid != null) {
			employeeCode = personInfoAdapter.getPersonInfo(sid).getEmployeeCode();
		}
		
		Optional<ExOutOpMng> exOutOpMng = exOutOpMngRepo.getExOutOpMngById(processingId);

		if (!exOutOpMng.isPresent())
			return;
		exOutOpMng.get().setOpCond(ExIoOperationState.EXPORTING);
		exOutOpMng.get().setErrCnt(exOutOpMng.get().getErrCnt() + 1);
		exOutOpMngRepo.update(exOutOpMng.get());
		
		String companyId = AppContexts.user().companyId();
		String outputProcessId = processingId;
		String errorTargetValue = targetValue;
		// in the case of dateType, never error so it always empty
		GeneralDate errorDate = null;
		String errorEmployee = employeeCode;
		GeneralDateTime logRegisterDateTime = GeneralDateTime.now();
		int logSequenceNumber = exOutOpMng.get().getErrCnt();
		int processCount = exOutOpMng.get().getProCnt();
		int processContent = ProcessingClassification.ERROR.value;

		ExternalOutLog externalOutLog = new ExternalOutLog(companyId, outputProcessId, errorContent, errorTargetValue,
				errorDate, errorEmployee, errorItem, logRegisterDateTime, logSequenceNumber, processCount,
				processContent);

		externalOutLogRepo.add(externalOutLog);
	}

	// サーバ外部出力ファイル項目作成
	private Map<String, String> fileItemDataCreation(List<String> lineData, OutputItemCustom outputItemCustom,
			boolean isSetNull, String nullValueReplace, int index) {
		String itemValue = "";
		String value;
		Map<String, String> result = new HashMap<String, String>();

		if (outputItemCustom.getStandardOutputItem().getCategoryItems() == null) {
			result.put(ITEM_VALUE, nullValueReplace);
			result.put(USE_NULL_VALUE, USE_NULL_VALUE_ON);
			return result;
		}
		List<CategoryItem> categoryItems = outputItemCustom.getStandardOutputItem().getCategoryItems();
		List<ResultCharater> listResultCharater = new ArrayList<>();
		for (int i = 0; i < categoryItems.size(); i++) {
			value = lineData.get(index);
			index++;

			if (StringUtils.isEmpty(value)) {
				if (isSetNull) {
					value = nullValueReplace;
					result.put(ITEM_VALUE, value);
					result.put(USE_NULL_VALUE, USE_NULL_VALUE_ON);
					return result;
				}
			}

//			if ((i == 0) || StringUtils.isEmpty(itemValue)) {
//				itemValue = value;
//				continue;
//			}

			if (outputItemCustom.getStandardOutputItem().getItemType() == ItemType.CHARACTER) {
				listResultCharater.add(new ResultCharater(categoryItems.get(i).getDisplayOrder(),value));
				//itemValue += value;
				continue;
			}  
			if ((outputItemCustom.getStandardOutputItem().getItemType() != ItemType.NUMERIC)
					&& (outputItemCustom.getStandardOutputItem().getItemType() != ItemType.TIME)
					&& (outputItemCustom.getStandardOutputItem().getItemType() != ItemType.INS_TIME)) {
				itemValue = value;
				continue;
			}
			
			Optional<OperationSymbol> operationSymbol = categoryItems
					.get(i).getOperationSymbol();
			if ( StringUtils.isEmpty(value))
				continue;
			if ( !operationSymbol.isPresent() || operationSymbol.get() == OperationSymbol.PLUS) {
				itemValue = String.valueOf((Double.parseDouble(itemValue.equals("")?"0":itemValue)) + Double.parseDouble(value.equals("")?"0":value));
			} else if (operationSymbol.get() == OperationSymbol.MINUS) {
				itemValue = String.valueOf(Double.parseDouble(itemValue.equals("")?"0":itemValue) - Double.parseDouble(value.equals("")?"0":value));
			}
		}
		if(outputItemCustom.getStandardOutputItem().getItemType() == ItemType.CHARACTER) {
			if(!listResultCharater.isEmpty()) {
				List<ResultCharater> listResultCharaterSort = listResultCharater.stream().sorted((x,y)->x.getIndex()-y.getIndex()).collect(Collectors.toList());
				for(ResultCharater resultCharater : listResultCharaterSort ) {
					itemValue += resultCharater.getValue();
				}
			}
		}
		result.put(ITEM_VALUE,itemValue);
		result.put(USE_NULL_VALUE, USE_NULL_VALUE_OFF);

		return result;
	}

	private void createWhereCondition(StringBuilder temp, String table, String key, String operation, String value) {
		temp.append(AND_COND);
		if(ConditionSymbol.IS_NOT.operator.equals(operation)) {
			temp.append("(");
			temp.append(table);
			temp.append(DOT);
			temp.append(key);
			temp.append(operation);
			temp.append(value);
			
			temp.append(" OR ");
			temp.append(table);
			temp.append(DOT);
			temp.append(key);
			temp.append(" IS NULL ) ");
		}else {
			temp.append(table);
			temp.append(DOT);
			temp.append(key);
			temp.append(operation);
			temp.append(value);
		}
	}
	
	private void createWhereCondition(StringBuilder temp, String key, String operation, String value) {
		temp.append(AND_COND);
		temp.append(key);
		temp.append(operation);
		temp.append(value);
	}

	// 外部出力取得項目一覧 only for this file
	private List<OutputItemCustom> getExOutItemList(String condSetCd, String userID, String outItemCd,
			StandardAtr standardType, boolean isAcquisitionMode) {
		String cid = AppContexts.user().companyId();
		List<StandardOutputItem> stdOutItemList = new ArrayList<StandardOutputItem>();
		List<StandardOutputItemOrder> stdOutItemOrder = new ArrayList<StandardOutputItemOrder>();

		if (standardType == StandardAtr.STANDARD) {
			if (StringUtils.isEmpty(outItemCd)) {
				stdOutItemList.addAll(stdOutItemRepo.getStdOutItemByCidAndSetCd(cid, condSetCd));
				stdOutItemOrder.addAll(stdOutItemOrderRepo.getStandardOutputItemOrderByCidAndSetCd(cid, condSetCd));
			} else {
				stdOutItemRepo.getStdOutItemById(cid, outItemCd, condSetCd).ifPresent(item -> stdOutItemRepo.add(item));
				stdOutItemOrderRepo.getStandardOutputItemOrderById(cid, outItemCd, condSetCd)
						.ifPresent(item -> stdOutItemOrder.add(item));
			}
		} else {
			// type user pending
		}

		// 出力項目(リスト)を出力項目並び順(リスト)に従って並び替える
		stdOutItemList.sort(new Comparator<StandardOutputItem>() {
			@Override
			public int compare(StandardOutputItem outputItem1, StandardOutputItem outputItem2) {
				List<StandardOutputItemOrder> order1 = stdOutItemOrder.stream()
						.filter(order -> order.getOutputItemCode().v().equals(outputItem1.getOutputItemCode().v()))
						.collect(Collectors.toList());

				List<StandardOutputItemOrder> order2 = stdOutItemOrder.stream()
						.filter(order -> order.getOutputItemCode().v().equals(outputItem2.getOutputItemCode().v()))
						.collect(Collectors.toList());

				if((order1.size() > 0) && (order2.size() > 0)) {
					return order1.get(0).getDisplayOrder() > order2.get(0).getDisplayOrder() ? 1 : -1;
				}
					
				return order1.size() - order2.size();
			}
		});

		DataFormatSetting dataFormatSetting;
		OutputItemCustom outputItemCustom;
		List<OutputItemCustom> outputItemCustomList = new ArrayList<>();

		if (!isAcquisitionMode)
			return outputItemCustomList;

		NumberDataFmSet numberDataFmSetFixed = getNumberDataFmSetFixed();
		ChacDataFmSet chacDataFmSetFixed = getChacDataFmSetFixed();
		DateFormatSet dateFormatSetFixed = getDateFormatSetFixed();
		TimeDataFmSet timeDataFmSetFixed = getTimeDataFmSetFixed();
		InTimeDataFmSet inTimeDataFmSetFixed = getInTimeDataFmSetFixed();
		AwDataFormatSet awDataFormatSetFixed = getAwDataFormatSetFixed();
		for (StandardOutputItem stdOutItem : stdOutItemList) {
			switch (stdOutItem.getItemType()) {
			case NUMERIC:
				Optional<NumberDataFmSetting> numberDataFmSetting = stdOutItemRepo.getNumberDataFmSettingByID(
						stdOutItem.getCid(), stdOutItem.getConditionSettingCode().v(),
						stdOutItem.getOutputItemCode().v());
				if (numberDataFmSetting.isPresent()) {
					dataFormatSetting = numberDataFmSetting.get();
					break;
				}

				Optional<NumberDataFmSet> numberDataFmSet = dataFormatSettingRepo.getNumberDataFmSetById(cid);
				if (numberDataFmSet.isPresent()) {
					dataFormatSetting = numberDataFmSet.get();
					break;
				}
				dataFormatSetting = numberDataFmSetFixed;
				break;
			case CHARACTER:
				Optional<CharacterDataFmSetting> characterDataFmSetting = stdOutItemRepo.getCharacterDataFmSettingByID(
						stdOutItem.getCid(), stdOutItem.getConditionSettingCode().v(),
						stdOutItem.getOutputItemCode().v());
				if (characterDataFmSetting.isPresent()) {
					dataFormatSetting = characterDataFmSetting.get();
					break;
				}

				Optional<ChacDataFmSet> chacDataFmSet = dataFormatSettingRepo.getChacDataFmSetById(cid);
				if (chacDataFmSet.isPresent()) {
					dataFormatSetting = chacDataFmSet.get();
					break;
				}

				dataFormatSetting = chacDataFmSetFixed;
				break;
			case DATE:
				Optional<DateFormatSetting> dateFormatSetting = stdOutItemRepo.getDateFormatSettingByID(
						stdOutItem.getCid(), stdOutItem.getConditionSettingCode().v(),
						stdOutItem.getOutputItemCode().v());
				if (dateFormatSetting.isPresent()) {
					dataFormatSetting = dateFormatSetting.get();
					break;
				}

				Optional<DateFormatSet> dateFormatSet = dataFormatSettingRepo.getDateFormatSetById(cid);
				if (dateFormatSet.isPresent()) {
					dataFormatSetting = dateFormatSet.get();
					break;
				}

				dataFormatSetting = dateFormatSetFixed;
				break;
			case TIME:
				Optional<TimeDataFmSetting> timeDataFmSetting = stdOutItemRepo.getTimeDataFmSettingByID(
						stdOutItem.getCid(), stdOutItem.getConditionSettingCode().v(),
						stdOutItem.getOutputItemCode().v());
				if (timeDataFmSetting.isPresent()) {
					dataFormatSetting = timeDataFmSetting.get();
					break;
				}

				Optional<TimeDataFmSet> timeDataFmSet = dataFormatSettingRepo.getTimeDataFmSetByCid(cid);
				if (timeDataFmSet.isPresent()) {
					dataFormatSetting = timeDataFmSet.get();
					break;
				}

				dataFormatSetting = timeDataFmSetFixed;
				break;
			case INS_TIME:
				Optional<InstantTimeDataFmSetting> instantTimeDataFmSetting = stdOutItemRepo
						.getInstantTimeDataFmSettingByID(stdOutItem.getCid(), stdOutItem.getConditionSettingCode().v(),
								stdOutItem.getOutputItemCode().v());
				if (instantTimeDataFmSetting.isPresent()) {
					dataFormatSetting = instantTimeDataFmSetting.get();
					break;
				}

				Optional<InTimeDataFmSet> inTimeDataFmSet = dataFormatSettingRepo.getInTimeDataFmSetByCid(cid);
				if (inTimeDataFmSet.isPresent()) {
					dataFormatSetting = inTimeDataFmSet.get();
					break;
				}

				dataFormatSetting = inTimeDataFmSetFixed;
				break;
			case AT_WORK_CLS:
				Optional<AwDataFormatSetting> awDataFormatSetting = stdOutItemRepo.getAwDataFormatSettingByID(
						stdOutItem.getCid(), stdOutItem.getConditionSettingCode().v(),
						stdOutItem.getOutputItemCode().v());
				if (awDataFormatSetting.isPresent()) {
					dataFormatSetting = awDataFormatSetting.get();
					break;
				}

				Optional<AwDataFormatSet> awDataFormatSet = dataFormatSettingRepo.getAwDataFormatSetById(cid);
				if (awDataFormatSet.isPresent()) {
					dataFormatSetting = awDataFormatSet.get();
					break;
				}

				dataFormatSetting = awDataFormatSetFixed;
				break;
			default:
				dataFormatSetting = null;
				break;
			}

			List<CtgItemData> ctgItemDataList = new ArrayList<CtgItemData>();
			for (CategoryItem categoryItem : stdOutItem.getCategoryItems()) {
				ctgItemDataRepo.getCtgItemDataByIdAndDisplayClass(categoryItem.getCategoryId().v(),
						categoryItem.getItemNo().v(), 1).ifPresent(item -> ctgItemDataList.add(item));
			}

			outputItemCustom = new OutputItemCustom(stdOutItem, dataFormatSetting, ctgItemDataList);
			outputItemCustomList.add(outputItemCustom);
		}

		return outputItemCustomList;
	}

	private NumberDataFmSet getNumberDataFmSetFixed() {
		String cid = AppContexts.user().companyId();
		NotUseAtr nullValueReplace = NotUseAtr.NOT_USE;
		Optional<DataFormatNullReplacement> valueOfNullValueReplace = Optional.empty();
		NotUseAtr outputMinusAsZero = NotUseAtr.NOT_USE;
		NotUseAtr fixedValue = NotUseAtr.NOT_USE;
		Optional<DataTypeFixedValue> valueOfFixedValue = Optional.empty();
		NotUseAtr fixedValueOperation = NotUseAtr.NOT_USE;
		Optional<DataFormatFixedValueOperation> fixedCalculationValue = Optional.empty();
		FixedValueOperationSymbol fixedValueOperationSymbol = FixedValueOperationSymbol.PLUS;
		NotUseAtr fixedLengthOutput = NotUseAtr.NOT_USE;
		Optional<DataFormatIntegerDigit> fixedLengthIntegerDigit = Optional.empty();
		FixedLengthEditingMethod fixedLengthEditingMethod = FixedLengthEditingMethod.BEFORE_ZERO;
		Optional<DataFormatDecimalDigit> decimalDigit = Optional.empty();
		DecimalPointClassification decimalPointClassification = DecimalPointClassification.OUT_PUT;
		Rounding decimalFraction = Rounding.TRUNCATION;
		DecimalDivision formatSelection = DecimalDivision.DECIMAL;

		return new NumberDataFmSet(ItemType.NUMERIC, cid, nullValueReplace, valueOfNullValueReplace, outputMinusAsZero,
				fixedValue, valueOfFixedValue, fixedValueOperation, fixedCalculationValue, fixedValueOperationSymbol,
				fixedLengthOutput, fixedLengthIntegerDigit, fixedLengthEditingMethod, decimalDigit,
				decimalPointClassification, decimalFraction, formatSelection);

	}

	private ChacDataFmSet getChacDataFmSetFixed() {
		String cid = AppContexts.user().companyId();
		NotUseAtr nullValueReplace = NotUseAtr.NOT_USE;
		Optional<DataFormatNullReplacement> valueOfNullValueReplace = Optional.empty();
		NotUseAtr cdEditting = NotUseAtr.NOT_USE;
		NotUseAtr fixedValue = NotUseAtr.NOT_USE;
		FixedLengthEditingMethod cdEdittingMethod = FixedLengthEditingMethod.BEFORE_ZERO;
		Optional<DataFormatCharacterDigit> cdEditDigit = Optional.empty();
		Optional<ConvertCode> convertCode = Optional.empty();
		EditSpace spaceEditting = EditSpace.DO_NOT_DELETE;
		NotUseAtr effectDigitLength = NotUseAtr.NOT_USE;
		Optional<DataFormatCharacterDigit> startDigit = Optional.empty();
		Optional<DataFormatCharacterDigit> endDigit = Optional.empty();
		Optional<DataTypeFixedValue> valueOfFixedValue = Optional.empty();

		return new ChacDataFmSet(ItemType.CHARACTER, cid, nullValueReplace, valueOfNullValueReplace, cdEditting,
				fixedValue, cdEdittingMethod, cdEditDigit, convertCode, spaceEditting, effectDigitLength, startDigit,
				endDigit, valueOfFixedValue);
	}

	private DateFormatSet getDateFormatSetFixed() {
		String cid = AppContexts.user().companyId();
		NotUseAtr nullValueSubstitution = NotUseAtr.NOT_USE;
		NotUseAtr fixedValue = NotUseAtr.NOT_USE;
		Optional<DataTypeFixedValue> valueOfFixedValue = Optional.empty();
		Optional<DataFormatNullReplacement> valueOfNullValueSubs = Optional.empty();
		DateOutputFormat formatSelection = DateOutputFormat.YYYY_MM_DD;

		return new DateFormatSet(ItemType.DATE, cid, nullValueSubstitution, fixedValue, valueOfFixedValue,
				valueOfNullValueSubs, formatSelection);
	}

	private TimeDataFmSet getTimeDataFmSetFixed() {
		String cid = AppContexts.user().companyId();
		NotUseAtr nullValueSubs = NotUseAtr.NOT_USE;
		NotUseAtr outputMinusAsZero = NotUseAtr.NOT_USE;
		NotUseAtr fixedValue = NotUseAtr.NOT_USE;
		Optional<DataTypeFixedValue> valueOfFixedValue = Optional.empty();
		NotUseAtr fixedLengthOutput = NotUseAtr.NOT_USE;
		Optional<DataFormatIntegerDigit> fixedLongIntegerDigit = Optional.empty();
		FixedLengthEditingMethod fixedLengthEditingMethod = FixedLengthEditingMethod.BEFORE_ZERO;
		DelimiterSetting delimiterSetting = DelimiterSetting.SEPARATE_BY_COLON;
		HourMinuteClassification selectHourMinute = HourMinuteClassification.HOUR_AND_MINUTE;
		Optional<DataFormatDecimalDigit> minuteFractionDigit = Optional.empty();
		DecimalSelection decimalSelection = DecimalSelection.HEXA_DECIMAL;
		FixedValueOperationSymbol fixedValueOperationSymbol = FixedValueOperationSymbol.PLUS;
		NotUseAtr fixedValueOperation = NotUseAtr.NOT_USE;
		Optional<DataFormatFixedValueOperation> fixedCalculationValue = Optional.empty();
		Optional<DataFormatNullReplacement> valueOfNullValueSubs = Optional.empty();
		Rounding minuteFractionDigitProcessCls = Rounding.TRUNCATION;

		return new TimeDataFmSet(ItemType.TIME, cid, nullValueSubs, outputMinusAsZero, fixedValue, valueOfFixedValue,
				fixedLengthOutput, fixedLongIntegerDigit, fixedLengthEditingMethod, delimiterSetting, selectHourMinute,
				minuteFractionDigit, decimalSelection, fixedValueOperationSymbol, fixedValueOperation,
				fixedCalculationValue, valueOfNullValueSubs, minuteFractionDigitProcessCls);
	}

	private InTimeDataFmSet getInTimeDataFmSetFixed() {
		String cid = AppContexts.user().companyId();
		NotUseAtr nullValueSubs = NotUseAtr.NOT_USE;
		Optional<DataFormatNullReplacement> valueOfNullValueSubs = Optional.empty();
		NotUseAtr outputMinusAsZero = NotUseAtr.NOT_USE;
		NotUseAtr fixedValue = NotUseAtr.NOT_USE;
		Optional<DataTypeFixedValue> valueOfFixedValue = Optional.empty();
		HourMinuteClassification timeSeletion = HourMinuteClassification.HOUR_AND_MINUTE;
		NotUseAtr fixedLengthOutput = NotUseAtr.NOT_USE;
		Optional<DataFormatIntegerDigit> fixedLongIntegerDigit = Optional.empty();
		FixedLengthEditingMethod fixedLengthEditingMethod = FixedLengthEditingMethod.BEFORE_ZERO;
		DelimiterSetting delimiterSetting = DelimiterSetting.SEPARATE_BY_COLON;
		PreviousDayOutputMethod prevDayOutputMethod = PreviousDayOutputMethod.FORMAT24HOUR;
		NextDayOutputMethod nextDayOutputMethod = NextDayOutputMethod.OUT_PUT_24HOUR;
		Optional<DataFormatDecimalDigit> minuteFractionDigit = Optional.empty();
		DecimalSelection decimalSelection = DecimalSelection.HEXA_DECIMAL;
		Rounding minuteFractionDigitProcessCls = Rounding.TRUNCATION;

		return new InTimeDataFmSet(ItemType.INS_TIME, cid, nullValueSubs, valueOfNullValueSubs, outputMinusAsZero,
				fixedValue, valueOfFixedValue, timeSeletion, fixedLengthOutput, fixedLongIntegerDigit,
				fixedLengthEditingMethod, delimiterSetting, prevDayOutputMethod, nextDayOutputMethod,
				minuteFractionDigit, decimalSelection, minuteFractionDigitProcessCls);
	}

	private AwDataFormatSet getAwDataFormatSetFixed() {
		String cid = AppContexts.user().companyId();
		Optional<DataTypeFixedValue> closedOutput = Optional.empty();
		Optional<DataTypeFixedValue> absenceOutput = Optional.empty();
		NotUseAtr fixedValue = NotUseAtr.NOT_USE;
		Optional<DataTypeFixedValue> valueOfFixedValue = Optional.empty();
		Optional<DataTypeFixedValue> atWorkOutput = Optional.empty();
		Optional<DataTypeFixedValue> retirementOutput = Optional.empty();

		return new AwDataFormatSet(ItemType.AT_WORK_CLS, cid, closedOutput, absenceOutput, fixedValue,
				valueOfFixedValue, atWorkOutput, retirementOutput);
	}

	// サーバ外部出力ファイル型チェック
	private Map<String, String> checkOutputFileType(String itemValue, ItemType itemType,
			DataFormatSetting dataFormatSetting, String sid,GeneralDate baseDate) {
		Map<String, String> result;

		try {
			switch (itemType) {
			case NUMERIC:
				result = checkNumericType(itemValue, (NumberDataFmSet) dataFormatSetting);
				break;
			case TIME:
				result = checkTimeType(itemValue, (TimeDataFmSet) dataFormatSetting);
				break;
			case CHARACTER:
				result = checkCharType(itemValue, (ChacDataFmSet) dataFormatSetting);
				break;
			case INS_TIME:
				result = checkTimeOfDayType(itemValue, (InTimeDataFmSet) dataFormatSetting);
				break;
			case DATE:
				result = checkDateType(itemValue, (DateFormatSet) dataFormatSetting);
				break;
			case AT_WORK_CLS:
				result = checkOfficeType(itemValue, (AwDataFormatSet) dataFormatSetting, sid, baseDate);
				break;
			default:
				result = new HashMap<String, String>();
				result.put(RESULT_STATE, RESULT_NG);
				result.put(ERROR_MESS, "");
				result.put(RESULT_VALUE, itemValue);
				break;
			}
		} catch (Exception e) {
			result = new HashMap<String, String>();
			result.put(RESULT_STATE, RESULT_NG);
			result.put(ERROR_MESS, "Check output data exception");
			result.put(RESULT_VALUE, itemValue);
		}

		return result;
	}

	// サーバ外部出力ファイル型チェック数値型
	private Map<String, String> checkNumericType(String itemValue, NumberDataFmSet setting) {
		Map<String, String> result = new HashMap<String, String>();
		String state = RESULT_OK;
		String errorMess = "";
		String targetValue;
		BigDecimal decimaValue = new BigDecimal(itemValue);

		if ((setting.getFixedValueOperation() == NotUseAtr.USE) && setting.getFixedCalculationValue().isPresent()) {
			if (setting.getFixedValueOperationSymbol() == FixedValueOperationSymbol.MINUS) {
				decimaValue = decimaValue.subtract(setting.getFixedCalculationValue().get().v());
			} else if (setting.getFixedValueOperationSymbol() == FixedValueOperationSymbol.PLUS) {
				decimaValue = decimaValue.add(setting.getFixedCalculationValue().get().v());
			}
		}

		decimaValue = ((setting.getOutputMinusAsZero() == NotUseAtr.USE) && decimaValue.doubleValue() < 0)
				? BigDecimal.valueOf(0) : decimaValue;

		int precision = ((setting.getFormatSelection() == DecimalDivision.DECIMAL)
				&& setting.getDecimalDigit().isPresent()) ? setting.getDecimalDigit().get().v() : 0;
				decimaValue = roundDecimal(decimaValue, precision, setting.getDecimalFraction());

		targetValue = (setting.getDecimalPointClassification() == DecimalPointClassification.OUT_PUT)
				? decimaValue.toString() : decimaValue.toString().replace(DOT, "");

		if ((setting.getFixedLengthOutput() == NotUseAtr.USE) && setting.getFixedLengthIntegerDigit().isPresent()
				&& (targetValue.length() < setting.getFixedLengthIntegerDigit().get().v())) {
			targetValue = fixlengthData(targetValue, setting.getFixedLengthIntegerDigit().get().v(),
					setting.getFixedLengthEditingMethod());
		}

		result.put(RESULT_STATE, state);
		result.put(ERROR_MESS, errorMess);
		result.put(RESULT_VALUE, targetValue);
		return result;
	}

	// サーバ外部出力ファイル型チェック時間型
	private Map<String, String> checkTimeType(String itemValue, TimeDataFmSet setting) {
		Map<String, String> result = new HashMap<String, String>();
		String state = RESULT_OK;
		String errorMess = "";
		String targetValue;
		BigDecimal decimaValue = new BigDecimal(itemValue);
		
		if ((setting.getFixedValueOperation() == NotUseAtr.USE) && setting.getFixedCalculationValue().isPresent()) {
			if (setting.getFixedValueOperationSymbol() == FixedValueOperationSymbol.MINUS) {
				decimaValue = decimaValue.subtract(setting.getFixedCalculationValue().get().v());
			} else if (setting.getFixedValueOperationSymbol() == FixedValueOperationSymbol.PLUS) {
				decimaValue = decimaValue.add(setting.getFixedCalculationValue().get().v());
			}
		}

		if (setting.getSelectHourMinute() == HourMinuteClassification.HOUR_AND_MINUTE) {
			if (setting.getDecimalSelection() == DecimalSelection.DECIMAL) {
				decimaValue = decimaValue.divide(BigDecimal.valueOf(60.0), 2, RoundingMode.HALF_UP);
			} else if (setting.getDecimalSelection() == DecimalSelection.HEXA_DECIMAL) {
				BigDecimal intValue = decimaValue.divideToIntegralValue(BigDecimal.valueOf(60.00));
				BigDecimal remainValue = decimaValue.subtract(intValue.multiply(BigDecimal.valueOf(60.00)));
				decimaValue = intValue.add(remainValue.divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP));
			}
		}

		decimaValue = ((setting.getOutputMinusAsZero() == NotUseAtr.USE) && (decimaValue.doubleValue() < 0))
				? BigDecimal.valueOf(0) : decimaValue;
		if (setting.getDecimalSelection() == DecimalSelection.DECIMAL) {
			int precision = setting.getMinuteFractionDigit().map(item -> item.v()).orElse(0);
			decimaValue = roundDecimal(decimaValue, precision, setting.getMinuteFractionDigitProcessCls());
		}

		targetValue = decimaValue.toString();
		if (setting.getDelimiterSetting() == DelimiterSetting.NO_DELIMITER) {
			targetValue = targetValue.replace(DOT, "");
		} else if (setting.getDelimiterSetting() == DelimiterSetting.SEPARATE_BY_COLON) {
			targetValue = targetValue.replace(DOT, ":");
		}

		if ((setting.getFixedLengthOutput() == NotUseAtr.USE) && setting.getFixedLongIntegerDigit().isPresent()
				&& (targetValue.length() < setting.getFixedLongIntegerDigit().get().v())) {
			targetValue = fixlengthData(targetValue, setting.getFixedLongIntegerDigit().get().v(),
					setting.getFixedLengthEditingMethod());
		}
		result.put(RESULT_STATE, state);
		result.put(ERROR_MESS, errorMess);
		result.put(RESULT_VALUE, targetValue);

		return result;
	}

	// サーバ外部出力ファイル型チェック文字型
	private Map<String, String> checkCharType(String itemValue, ChacDataFmSet setting) {
		Map<String, String> result = new HashMap<String, String>();
		String state = RESULT_OK;
		String errorMess = "";
		String targetValue = itemValue;
		String cid = AppContexts.user().companyId();
		boolean inConvertCode = false;

		if ((setting.getEffectDigitLength() == NotUseAtr.USE) && setting.getStartDigit().isPresent()
				&& setting.getEndDigit().isPresent()) {
			targetValue = StringLength.cutOffAsLengthHalf(targetValue, setting.getStartDigit().get().v().intValue(),
					setting.getEndDigit().get().v().intValue() - setting.getStartDigit().get().v().intValue());
		}

		if (setting.getConvertCode().isPresent()) {
			Optional<OutputCodeConvert> codeConvert = outputCodeConvertRepo.getOutputCodeConvertById(cid, setting.getConvertCode().get().v());
			
			if(codeConvert.isPresent()) {
				for (CdConvertDetail convertDetail : codeConvert.map(OutputCodeConvert::getListCdConvertDetails).orElseGet(ArrayList::new)) {
					if (!targetValue.equals(convertDetail.getSystemCd().v())) {
						continue;
					}
					targetValue = convertDetail.getOutputItem().map(i->i.v()).orElse("");
					inConvertCode = true;
					break;
				}
	
				if (!inConvertCode && (codeConvert.map(i->i.getAcceptWithoutSetting()).orElse(null) == NotUseAtr.NOT_USE)) {
					state = RESULT_NG;
					errorMess = TextResource.localize("Msg_678");
	
					result.put(RESULT_STATE, state);
					result.put(ERROR_MESS, errorMess);
					result.put(RESULT_VALUE, targetValue);
	
					return result;
				}
			}
		}
		targetValue =  targetValue.replaceAll("\\n", "");
		if (setting.getSpaceEditting() == EditSpace.DELETE_SPACE_AFTER) {
			targetValue =  targetValue.replaceAll("\\s+$", "");
			int countSpaceJapan = 0;
			for(int i = targetValue.length()-1;i>=0;i--) {
				if(targetValue.charAt(i) == '　' || targetValue.charAt(i) == '　') {
					countSpaceJapan++;
				}else {
					break;
				}
			}
			targetValue = targetValue.substring(0,targetValue.length()-countSpaceJapan);
		} else if (setting.getSpaceEditting() == EditSpace.DELETE_SPACE_BEFORE) {
			targetValue = targetValue.replaceAll("^\\s+", "");
			int countSpaceJapan = 0;
			for(char x : targetValue.toCharArray()) {
				if(x == '　' || x == '　') {
					countSpaceJapan++;
				}else {
					break;
				}
			}
			targetValue = targetValue.substring(countSpaceJapan,targetValue.length());
		}

		if ((setting.getCdEditting() == NotUseAtr.USE)
				&& targetValue.length() < setting.getCdEditDigit().map(i->i.v()).orElse(-1)) {
			targetValue = fixlengthData(targetValue, setting.getCdEditDigit().get().v(), setting.getCdEdittingMethod());
		}

		result.put(RESULT_STATE, state);
		result.put(ERROR_MESS, errorMess);
		result.put(RESULT_VALUE, targetValue);

		return result;
	}

	// サーバ外部出力ファイル型チェック時刻型
	private Map<String, String> checkTimeOfDayType(String itemValue, InTimeDataFmSet setting) {
		Map<String, String> result = new HashMap<String, String>();
		String state = RESULT_OK;
		String errorMess = "";
		String targetValue;
		BigDecimal decimaValue = new BigDecimal(itemValue);

		if (setting.getTimeSeletion() == HourMinuteClassification.HOUR_AND_MINUTE) {
			if (setting.getDecimalSelection() == DecimalSelection.DECIMAL) {
				decimaValue = decimaValue.divide(BigDecimal.valueOf(60.00), 2, RoundingMode.HALF_UP);
			} else if (setting.getDecimalSelection() == DecimalSelection.HEXA_DECIMAL) {
				BigDecimal intValue = decimaValue.divideToIntegralValue(BigDecimal.valueOf(60.00));
				BigDecimal remainValue = decimaValue.subtract(intValue.multiply(BigDecimal.valueOf(60.00)));
				decimaValue = intValue.add(remainValue.divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP));
				targetValue = decimaValue.toString();
			}
		}
		
		decimaValue = ((setting.getOutputMinusAsZero() == NotUseAtr.USE) && (decimaValue.doubleValue() < 0))
				? BigDecimal.valueOf(0) : decimaValue;
				targetValue = decimaValue.toString();		
		if (setting.getDecimalSelection() == DecimalSelection.DECIMAL) {
			int precision = setting.getMinuteFractionDigit().map(item -> item.v()).orElse(0);
			decimaValue = roundDecimal(decimaValue, precision, setting.getMinuteFractionDigitProcessCls());
			targetValue = decimaValue.toString();
		}

		if ((Double.valueOf(itemValue) > 1440)
				&& (setting.getTimeSeletion() == HourMinuteClassification.HOUR_AND_MINUTE)
				&& (setting.getNextDayOutputMethod() == NextDayOutputMethod.OUT_PUT_24HOUR)) {
			//decimaValue.subtract(new BigDecimal(24.00));
			targetValue = decimaValue.subtract(new BigDecimal(24.00)).toString();
			
		}

		if ((decimaValue.doubleValue() < 0)
				&& (setting.getTimeSeletion() == HourMinuteClassification.HOUR_AND_MINUTE)) {
			if (setting.getPrevDayOutputMethod() == PreviousDayOutputMethod.FORMAT0H00) {
				decimaValue = new BigDecimal(0.00);
				targetValue = decimaValue.toString() + ":00";
			} else if (setting.getPrevDayOutputMethod() == PreviousDayOutputMethod.FORMAT24HOUR) {
				targetValue = decimaValue.add(new BigDecimal(24.00)).toString();
			}
		}
		
		
		if (setting.getDelimiterSetting() == DelimiterSetting.NO_DELIMITER) {
			targetValue = targetValue.replace(DOT, "");
		} else if (setting.getDelimiterSetting() == DelimiterSetting.SEPARATE_BY_COLON) {
			targetValue = targetValue.replace(DOT, ":");
		}

		if ((setting.getFixedLengthOutput() == NotUseAtr.USE) && setting.getFixedLongIntegerDigit().isPresent()
				&& (targetValue.length() < setting.getFixedLongIntegerDigit().get().v())) {
			targetValue = fixlengthData(targetValue, setting.getFixedLongIntegerDigit().get().v(),
					setting.getFixedLengthEditingMethod());
		}

		result.put(RESULT_STATE, state);
		result.put(ERROR_MESS, errorMess);
		result.put(RESULT_VALUE, targetValue);

 		return result;
	}

	// サーバ外部出力ファイル型チェック日付型
	private Map<String, String> checkDateType(String itemValue, DateFormatSet setting) {
		Map<String, String> result = new HashMap<String, String>();
		String state = RESULT_OK;
		String errorMess = "";
		String targetValue = "";
		itemValue = itemValue.substring(0, 10);
		GeneralDate date = GeneralDate.fromString(itemValue, yyyy_MM_dd);
		DateOutputFormat formatDate = setting.getFormatSelection();

		if (formatDate == DateOutputFormat.YY_MM_DD || formatDate == DateOutputFormat.YYMMDD
				|| formatDate == DateOutputFormat.YYYY_MM_DD || formatDate == DateOutputFormat.YYYYMMDD) {
			targetValue = date.toString(formatDate.format);
		} else if (formatDate == DateOutputFormat.DAY_OF_WEEK){
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E", Locale.JAPAN);			
			LocalDate dateXX = LocalDate.parse(itemValue, DateTimeFormatter.ofPattern("yyyy-MM-dd"));		
			targetValue = dateXX.format(formatter);
		}
		else if (formatDate == DateOutputFormat.JJYY_MM_DD || formatDate == DateOutputFormat.JJYYMMDD) {
			JapaneseEras erasList = japaneseErasAdapter.getAllEras();
			Optional<JapaneseEraName> japaneseEraNameOptional = erasList.eraOf(date);
			
			if(!japaneseEraNameOptional.isPresent()) {
				state = RESULT_NG;
				errorMess = "Could not get japanese era name";
				targetValue = date.toString(DateOutputFormat.YYYY_MM_DD.nameId);
				
			} else {
				JapaneseEraName japaneseEraName = japaneseEraNameOptional.get();
				
				StringBuilder japaneseDate = new StringBuilder(japaneseEraName.getName());
				if(formatDate == DateOutputFormat.JJYY_MM_DD){
				japaneseDate.append((date.year() - japaneseEraName.startDate().year() + 1) + SLASH);
				if (date.month() < 10) {
					japaneseDate.append("0" + date.month() + SLASH);
				}else{
					japaneseDate.append(date.month() + SLASH);
				}
				if (date.day() < 10) {
					japaneseDate.append("0" + date.day());
				}else{
					japaneseDate.append(date.day());
				}
				} else if(formatDate == DateOutputFormat.JJYYMMDD){
					japaneseDate.append((date.year() - japaneseEraName.startDate().year() + 1) );
					if (date.month() < 10) {
					japaneseDate.append("0" + date.month());
					} else {
						japaneseDate.append(date.month());
					}
					if (date.day() < 10) {
						japaneseDate.append("0" + date.day());
					}else {
					japaneseDate.append(date.day());
					}
				}
				
				targetValue = japaneseDate.toString();
			}
		}

		result.put(RESULT_STATE, state);
		result.put(ERROR_MESS, errorMess);
		result.put(RESULT_VALUE, targetValue);

		return result;
	}

	// サーバ外部出力ファイル型チェック在職区分型
	private Map<String, String> checkOfficeType(String itemValue, AwDataFormatSet setting, String sid,GeneralDate baseDate) {
		Map<String, String> result = new HashMap<String, String>();
		String state = RESULT_OK;
		String errorMess = "";
		String targetValue = "";
		StatusOfEmployment status;
		//GeneralDate date = GeneralDate.fromString(itemValue, yyyy_MM_dd);
		
		Optional<StatusOfEmploymentResult> statusOfEmployment = statusOfEmploymentAdapter.getStatusOfEmployment(sid,
				baseDate);
		if (statusOfEmployment.isPresent()) {
			status = EnumAdaptor.valueOf(statusOfEmployment.get().getStatusOfEmployment(), StatusOfEmployment.class);
			switch (status) {
			case INCUMBENT:
				targetValue = setting.getAtWorkOutput().map(item -> item.v()).orElse("");
				break;
			case LEAVE_OF_ABSENCE:
				targetValue = setting.getAbsenceOutput().map(item -> item.v()).orElse("");
				break;
			case RETIREMENT:
				targetValue = setting.getRetirementOutput().map(item -> item.v()).orElse("");
				break;
			case HOLIDAY:
				targetValue = setting.getClosedOutput().map(item -> item.v()).orElse("");
				break;

			default:
				break;
			}
		} else {
			state = RESULT_NG;
			errorMess = "";
		}

		result.put(RESULT_STATE, state);
		result.put(ERROR_MESS, errorMess);
		result.put(RESULT_VALUE, targetValue);

		return result;
	}

	private BigDecimal roundDecimal(BigDecimal decimaValue, int precision, Rounding rounding) {
		RoundingMode roundingMode;

		switch (rounding) {
		case DOWN_4_UP_5:
			roundingMode = RoundingMode.HALF_UP;
			break;
		case TRUNCATION:
			roundingMode = RoundingMode.DOWN;
			break;
		case ROUND_UP:
			roundingMode = RoundingMode.UP;
			break;

		default:
			roundingMode = RoundingMode.HALF_UP;
			break;
		}

		return decimaValue.setScale(precision, roundingMode);
	}

	private String fixlengthData(String data, int fixLength, FixedLengthEditingMethod method) {
		StringBuilder result = new StringBuilder(data);
		StringBuilder addString = new StringBuilder("");

		switch (method) {
		case AFTER_ZERO:
			for (int i = 0; i < (fixLength - data.length()); i++) {
				addString.append("0");
			}
			result.append(addString);
			break;
		case AFTER_SPACE:
			for (int i = 0; i < (fixLength - data.length()); i++) {
				addString.append(" ");
			}
			result.append(addString);
			break;
		case BEFORE_ZERO:
			for (int i = 0; i < (fixLength - data.length()); i++) {
				addString.append("0");
			}
			result = addString.append(result);
			break;
		case BEFORE_SPACE:
			for (int i = 0; i < (fixLength - data.length()); i++) {
				addString.append(" ");
			}
			result = addString.append(result);
			break;

		default:
			break;
		}

		return result.toString();
	}
	
	private GeneralDateTime toDateTime(GeneralDate date) {
		return GeneralDateTime.ymdhms(date.year(), date.month(), date.day(), 0, 0, 0);
	}
	
	private void saveAutoExecutionLog(ExOutSetting exOutSetting) {
		GeneralDateTime now = GeneralDateTime.now();
		Optional<ExOutOpMng> optExOutOpMng = this.exOutOpMngRepo
				.getExOutOpMngById(exOutSetting.getProcessingId());
		// ドメインモデル「外部出力実行結果ログ」へ追加する
		ExterOutExecLog exterOutExecLog = new ExterOutExecLog(exOutSetting.getCid(), 
				exOutSetting.getProcessingId(), exOutSetting.getUserId(), 1, 1, null, null, NotUseAtr.USE.value, null, null, null, 
				now, now, StandardClassification.STANDARD.value, ExecutionForm.AUTOMATIC_EXECUTION.value, null,
				exOutSetting.getReferenceDate(), exOutSetting.getEndDate(), exOutSetting.getStartDate(), 
				exOutSetting.getConditionSetCd(), ResultStatus.FAILURE.value, exOutSetting.getConditionSetName());
		this.exterOutExecLogRepo.add(exterOutExecLog);
		
		// ドメインモデル「外部出力結果ログ」へ追加する
		ExternalOutLog externalOutLog = new ExternalOutLog(exOutSetting.getCid(), 
				exOutSetting.getProcessingId(), TextResource.localize("CMF002_501"), null, null, null, null, 
				now, 1, optExOutOpMng.map(ExOutOpMng::getProCnt).orElse(null), ProcessingClassification.ERROR.value);
		this.externalOutLogRepo.add(externalOutLog);
	}
}
