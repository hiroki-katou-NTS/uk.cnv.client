package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.task.parallel.ManagedParallelWithContext.ControlOption;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeRecordAdapter;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeRecordImport;
import nts.uk.ctx.at.record.dom.adapter.specificdatesetting.RecSpecificDateSettingAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.WorkPlaceConfig;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.output.ExecutionAttr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.EmployeeGeneralInfoService;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.UpdateLogInfoWithNewTransaction;
import nts.uk.ctx.at.record.dom.functionalgorithm.errorforcreatedaily.ErrorHandlingCreateDailyResults;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.TargetPersonRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExeStateOfCalAndSum;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionStatus;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.EmployeeGeneralInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.ExJobTitleHistItemImport;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.ExJobTitleHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.ExWorkPlaceHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.ExWorkTypeHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.ExWorkplaceHistItemImport;
import nts.uk.ctx.at.shared.dom.adapter.specificdatesetting.RecSpecificDateSettingImport;
import nts.uk.ctx.at.shared.dom.bonuspay.enums.UseAtr;
import nts.uk.ctx.at.shared.dom.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.BPSettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.BPUnitUseSettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.CPBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.WPBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BPUnitUseSetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.CompanyBonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.WorkplaceBonusPaySetting;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.calculationsetting.repository.StampReflectionManagementRepository;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmpDto;
import nts.uk.ctx.at.shared.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmpHisAdaptor;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.output.MasterList;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.output.PeriodInMasterList;
import nts.uk.ctx.at.shared.dom.ot.autocalsetting.BaseAutoCalSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrule.overtime.AutoCalculationSetService;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.i18n.TextResource;

/**
 * ③日別実績の作成処理
 * @author tutk
 *
 */
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class CreateDailyResultDomainServiceNew {

	@Inject
	private TargetPersonRepository targetPersonRepository;

	@Inject
	private EmployeeGeneralInfoService employeeGeneralInfoService;

	@Inject
	private UpdateLogInfoWithNewTransaction updateLogInfoWithNewTransaction;

	@Inject
	private StampReflectionManagementRepository stampReflectionManagementRepository;

	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;

	@Inject
	private WorkingConditionRepository workingConditionRepo;

	@Inject
	private AffWorkplaceAdapter affWorkplaceAdapter;

	@Inject
	private BPUnitUseSettingRepository bPUnitUseSettingRepository;

//	@Inject
//	private WorkingConditionService workingConditionService;

	@Inject
	private WPBonusPaySettingRepository wPBonusPaySettingRepository;

	@Inject
	private CPBonusPaySettingRepository cPBonusPaySettingRepository;

	@Inject
	private AutoCalculationSetService autoCalculationSetService;

	@Inject
	private BPSettingRepository bPSettingRepository;

	@Inject
	private RecSpecificDateSettingAdapter recSpecificDateSettingAdapter;

	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;

	@Inject
	private EmployeeRecordAdapter employeeRecordAdapter;

	@Inject
	private ManagedParallelWithContext managedParallelWithContext;

	public static int MAX_DELAY_PARALLEL = 0;
	
	@Inject
	private ErrorHandlingCreateDailyResults errorHandlingCreateDailyResults;
	
	@Inject
	private CreateDailyResultEmployeeDomainServiceNew createDailyResultEmployeeDomainServiceNew;
	
	@Inject
	private BusinessTypeOfEmpHisAdaptor businessTypeOfEmpHisAdaptor;
	
	@Inject
	private RecordDomRequireService requireService;
	
	/**
	 * ③日別実績の作成処理
	 * 
	 * @param companyId
	 *            会社ID
	 * @param listEmployeeId
	 *            社員ID<List>
	 * @param period
	 *            期間
	 * @param empCalAndSumExeLogId
	 *            就業計算と集計実行ログ
	 * @param executionAttr
	 *            再作成区分 : 手動 - 自動
	 * @param executionType
	 *            実行タイプ （作成する、打刻反映する、実績削除する）
	 * @param checkLock
	 *            ロック中の計算/集計できるか(true,false)
	 */
	public ProcessState createDailyResult(AsyncCommandHandlerContext asyncContext, List<String> emloyeeIds,
			DatePeriod periodTime, ExecutionAttr executionAttr, String companyId,
			ExecutionTypeDaily executionType,Optional<EmpCalAndSumExeLog> empCalAndSumExeLog, Optional<Boolean> checkLock) {
		val dataSetter = asyncContext.getDataSetter();

		// 空のエラー一覧を作る
		List<ErrorMessageInfo> listErrorMessageInfo = new ArrayList<>();

		ProcessState status = ProcessState.SUCCESS;

		// AsyncCommandHandlerContext<SampleCancellableAsyncCommand> ABC;

		// ③日別実績の作成処理
//		ExecutionContent executionContent = executionLog.get().getExecutionContent();

//		if (executionContent == ExecutionContent.DAILY_CREATION) {
		
		if(empCalAndSumExeLog.isPresent()) {
			// ④ログ情報（実行ログ）を更新する
			updateLogInfoWithNewTransaction.updateLogInfo(empCalAndSumExeLog.get().getEmpCalAndSumExecLogID(), 0,
					ExecutionStatus.PROCESSING.value);
		}

		Optional<StampReflectionManagement> stampReflectionManagement = this.stampReflectionManagementRepository
				.findByCid(companyId);

		// マスタ情報を取得する
		// Imported(就業)「社員の履歴情報」 を取得する
		// reqList401
		EmployeeGeneralInfoImport employeeGeneralInfoImport = this.employeeGeneralInfoService
				.getEmployeeGeneralInfo(emloyeeIds, periodTime);
		List<ExWorkTypeHistoryImport> exWorkTypeHistoryImports = this.businessTypeOfEmpHisAdaptor
				.findByCidSidBaseDate(companyId, emloyeeIds, periodTime).stream()
				.map(c -> convertToBusinessTypeOfEmpDto(c)).collect(Collectors.toList());
		employeeGeneralInfoImport.setExWorkTypeHistoryImports(exWorkTypeHistoryImports);
		// Imported(勤務実績)「期間分の勤務予定」を取得する
		// RequestList444 - TODO

		// ...................................
		// 社員ID（List）と期間から労働条件を取得する
		List<WorkingConditionItem> workingConditionItems = this.workingConditionItemRepository
				.getBySidsAndDatePeriod(emloyeeIds, periodTime);

		Map<String, List<WorkingConditionItem>> mapWOrking = workingConditionItems.parallelStream()
				.collect(Collectors.groupingBy(WorkingConditionItem::getEmployeeId));

		// Map<Sid, Map<HistoryID, List<WorkingConditionItem>>>
		Map<String, Map<String, WorkingConditionItem>> mapWorkingConditionItem = mapWOrking.entrySet()
				.parallelStream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream()
						.collect(Collectors.toMap(WorkingConditionItem::getHistoryId, Function.identity()))));

		List<WorkingCondition> workingConditions = workingConditionRepo.getBySidsAndDatePeriod(emloyeeIds,
				periodTime);

		// Map<Sid, List<DateHistoryItem>>
		Map<String, List<DateHistoryItem>> mapLstDateHistoryItem = workingConditions.parallelStream().collect(
				Collectors.toMap(WorkingCondition::getEmployeeId, WorkingCondition::getDateHistoryItem));

		// Map<Sid, Map<HistoryID, DateHistoryItem>>
		Map<String, Map<String, DateHistoryItem>> mapDateHistoryItem = mapLstDateHistoryItem.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream()
						.collect(Collectors.toMap(DateHistoryItem::identifier, Function.identity()))));

		// 会社IDと期間から期間内の職場構成期間を取得する
		// ReqList485
		// List<DatePeriod> workPlaceHistory =
		// this.affWorkplaceAdapter.getLstPeriod(companyId, periodTime);

		// [No.647]期間に対応する職場構成を取得する
		List<WorkPlaceConfig> workPlaceConfigLst = this.affWorkplaceAdapter.findByCompanyIdAndPeriod(companyId,
				periodTime);
		List<DatePeriod> workPlaceHistory = new ArrayList<>();

		List<List<DatePeriod>> listWorkplaceHistory = workPlaceConfigLst.stream()
				.map(c -> c.getWkpConfigHistory().stream().map(m -> m.getPeriod()).collect(Collectors.toList()))
				.collect(Collectors.toList());

		workPlaceHistory.addAll(listWorkplaceHistory.get(0));
		StateHolder stateHolder = new StateHolder(emloyeeIds.size());

		/** 並列処理、AsyncTask */
		// Create thread pool.
		// ExecutorService executorService =
		// Executors.newFixedThreadPool(20);
		// CountDownLatch countDownLatch = new
		// CountDownLatch(emloyeeIds.size());

		this.managedParallelWithContext.forEach(ControlOption.custom().millisRandomDelay(MAX_DELAY_PARALLEL),
				emloyeeIds, employeeId -> {
					if (asyncContext.hasBeenRequestedToCancel()) {
						// asyncContext.finishedAsCancelled();
						stateHolder.add(ProcessState.INTERRUPTION);
						dataSetter.updateData("dailyCreateStatus", ExeStateOfCalAndSum.STOPPING.nameId);
						return;
						// return ProcessState.INTERRUPTION;
					}
					// AsyncTask task =
					// AsyncTask.builder().withContexts().keepsTrack(false).setDataSetter(dataSetter)
					// .threadName(this.getClass().getName()).build(() -> {
					// 社員の日別実績を計算
					if (stateHolder.isInterrupt()) {
						// Count down latch.
						// countDownLatch.countDown();
						return;
					}
					// 日別実績の作成入社前、退職後を期間から除く
					DatePeriod newPeriod = this.checkPeriod(companyId, employeeId, periodTime);

					// Outputのエラーを確認する
					if (newPeriod == null) {
						listErrorMessageInfo.add(new ErrorMessageInfo(companyId, employeeId, periodTime.start(),
								ExecutionContent.DAILY_CREATION, new ErrMessageResource("020"),
								new ErrMessageContent(TextResource.localize("Msg_1156"))));
					} else {

						// 対象期間 = periodTime
						// 職場構成期間 = workPlaceHistory
						// 社員の履歴情報 = employeeGeneralInfoImport
						// 労働条件 = workingConditionItems ( Map<String,
						// List<DateHistoryItem>> mapLstDateHistoryItem )
						// 特定日、加給、計算区分情報を取得する
						// 履歴が区切られている年月日を判断する
						List<GeneralDate> historySeparatedList = this.historyIsSeparated(newPeriod,
								workPlaceHistory, employeeGeneralInfoImport, mapLstDateHistoryItem, employeeId);

						PeriodInMasterList periodInMasterList = getPeriodInMasterList(companyId, employeeId,
								newPeriod, historySeparatedList, employeeGeneralInfoImport);

						OutputCreateDailyResult cStatus = createDataNew(asyncContext, newPeriod, executionAttr, companyId,
								empCalAndSumExeLog, dataSetter, employeeGeneralInfoImport,
								stateHolder, employeeId, stampReflectionManagement, mapWorkingConditionItem,
								mapDateHistoryItem, periodInMasterList,executionType, checkLock);
						listErrorMessageInfo.addAll(cStatus.getListErrorMessageInfo());
						if (cStatus.getProcessState() == ProcessState.INTERRUPTION) {
							stateHolder.add(cStatus.getProcessState());
							dataSetter.updateData("dailyCreateStatus", ExeStateOfCalAndSum.STOPPING.nameId);
							return;
						}

						stateHolder.add(cStatus.getProcessState());
						if (stateHolder.status.stream().filter(c -> c == ProcessState.INTERRUPTION)
								.count() > 0) {
							dataSetter.updateData("dailyCreateStatus", ExeStateOfCalAndSum.STOPPING.nameId);
							// Count down latch.
							// countDownLatch.countDown();
							stateHolder.add(ProcessState.INTERRUPTION);
							return;
							// return ProcessState.INTERRUPTION;
						}
					}
					// executorService.submit(task);
				});
		status = stateHolder.status.stream().filter(c -> c == ProcessState.INTERRUPTION).findFirst()
				.orElse(ProcessState.SUCCESS);
		if (status == ProcessState.SUCCESS) {
			dataSetter.updateData("dailyCreateCount", emloyeeIds.size());
			if (executionAttr.value == 0) {
				for (ErrorMessageInfo errorMessageInfo : listErrorMessageInfo) {
					String empCalAndSumExeLogId = empCalAndSumExeLog.isPresent()?empCalAndSumExeLog.get().getEmpCalAndSumExecLogID():null;
					// 日別実績の作成エラー処理
					errorHandlingCreateDailyResults.executeCreateError(companyId, errorMessageInfo.getEmployeeID(),
							errorMessageInfo.getProcessDate(), empCalAndSumExeLogId, errorMessageInfo.getExecutionContent(),
							errorMessageInfo.getResourceID(), errorMessageInfo.getMessageError());
				}
				if(empCalAndSumExeLog.isPresent()) {
					updateLogInfoWithNewTransaction.updateLogInfo(empCalAndSumExeLog.get().getEmpCalAndSumExecLogID(), 0,
						ExecutionStatus.DONE.value);
				}
			}
		}
//		} else {
//			dataSetter.updateData("dailyCreateStatus", ExeStateOfCalAndSum.STOPPING.nameId);
//			status = ProcessState.INTERRUPTION;
//		}
		return status;
	}
	
	private ExWorkTypeHistoryImport convertToBusinessTypeOfEmpDto(BusinessTypeOfEmpDto businessTypeOfEmpDto) {
		return new ExWorkTypeHistoryImport(businessTypeOfEmpDto.getCompanyId(), businessTypeOfEmpDto.getEmployeeId(),
				businessTypeOfEmpDto.getHistoryId(),
				new DatePeriod(businessTypeOfEmpDto.getStartDate(), businessTypeOfEmpDto.getEndDate()),
				businessTypeOfEmpDto.getBusinessTypeCd());
	}

	private DatePeriod checkPeriod(String companyId, String employeeId, DatePeriod periodTime) {

		DatePeriod datePeriodOutput = periodTime;

		// RequestList1
		EmployeeRecordImport empInfo = employeeRecordAdapter.getPersonInfor(employeeId);

		if (empInfo == null) {
			return null;
		}

		if (datePeriodOutput.start().before(empInfo.getEntryDate())
				&& datePeriodOutput.end().afterOrEquals(empInfo.getEntryDate())
				&& datePeriodOutput.end().beforeOrEquals(empInfo.getRetiredDate())) {
			datePeriodOutput = new DatePeriod(empInfo.getEntryDate(), datePeriodOutput.end());
		} else if (datePeriodOutput.start().afterOrEquals(empInfo.getEntryDate())
				&& datePeriodOutput.start().beforeOrEquals(empInfo.getRetiredDate())
				&& datePeriodOutput.end().after(empInfo.getRetiredDate())) {
			datePeriodOutput = new DatePeriod(datePeriodOutput.start(), empInfo.getRetiredDate());
		} else if (datePeriodOutput.start().afterOrEquals(empInfo.getEntryDate())
				&& datePeriodOutput.end().beforeOrEquals(empInfo.getRetiredDate())) {
			datePeriodOutput = new DatePeriod(datePeriodOutput.start(), datePeriodOutput.end());
		} else if (datePeriodOutput.start().beforeOrEquals(empInfo.getEntryDate())
				&& datePeriodOutput.end().afterOrEquals(empInfo.getRetiredDate())) {
			datePeriodOutput = new DatePeriod(empInfo.getEntryDate(), empInfo.getRetiredDate());
		} else
			datePeriodOutput = null;

		return datePeriodOutput;
	}

	// 会社職場個人の加給設定を取得する
	private Optional<BonusPaySetting> reflectBonusSetting(String companyId, String employeeId, GeneralDate date,
			List<String> workPlaceIdList) {
		Optional<BonusPaySetting> bonusPaySetting = Optional.empty();

		// ドメインモデル「加給利用単位」を取得する
		Optional<BPUnitUseSetting> bPUnitUseSetting = this.bPUnitUseSettingRepository.getSetting(companyId);

		// 加給利用単位．個人使用区分
		if (bPUnitUseSetting.isPresent() && bPUnitUseSetting.get().getPersonalUseAtr() == UseAtr.USE) {
			// 社員の労働条件を取得する
			Optional<WorkingConditionItem> workingConditionItem = WorkingConditionService
					.findWorkConditionByEmployee(requireService.createRequire(), employeeId, date);

			if (workingConditionItem.isPresent() && workingConditionItem.get().getTimeApply().isPresent()) {
				// ドメインモデル「加給設定」を取得する
				bonusPaySetting = this.bPSettingRepository.getBonusPaySetting(companyId,
						new BonusPaySettingCode(workingConditionItem.get().getTimeApply().get().v()));
				return bonusPaySetting;
			}
		}

		// 加給利用単位．職場使用区分
		if (bPUnitUseSetting.isPresent() && bPUnitUseSetting.get().getWorkplaceUseAtr() == UseAtr.USE) {
			Optional<WorkplaceBonusPaySetting> workplaceBonusPaySetting = Optional.empty();
			// 職場の加給設定を取得する
			for (String wPId : workPlaceIdList) {
				workplaceBonusPaySetting = this.wPBonusPaySettingRepository.getWPBPSetting(companyId,
						new WorkplaceId(wPId));
				if (workplaceBonusPaySetting.isPresent()) {
					break;
				}
			}
			if (workplaceBonusPaySetting.isPresent()) {
				bonusPaySetting = this.bPSettingRepository.getBonusPaySetting(companyId,
						workplaceBonusPaySetting.get().getBonusPaySettingCode());
				return bonusPaySetting;
			}
		}

		// ドメインモデル「会社加給設定」を取得する
		Optional<CompanyBonusPaySetting> companyBonusPaySetting = this.cPBonusPaySettingRepository
				.getSetting(companyId);

		if (companyBonusPaySetting.isPresent()) {
			bonusPaySetting = this.bPSettingRepository.getBonusPaySetting(companyId,
					companyBonusPaySetting.get().getBonusPaySettingCode());
			return bonusPaySetting;
		}

		return bonusPaySetting;
	}

	// 履歴が区切られている年月日を判断する
	private List<GeneralDate> historyIsSeparated(DatePeriod periodTime, List<DatePeriod> workPlaceHistory,
			EmployeeGeneralInfoImport employeeGeneralInfoImport,
			Map<String, List<DateHistoryItem>> mapLstDateHistoryItem, String employeeID) {
		// 履歴開始日一覧
		List<GeneralDate> historyStartDateList = new ArrayList<>();
		historyStartDateList.add(periodTime.start());
		// add all startDate
		if (workPlaceHistory.size() > 1) {
			for (DatePeriod workPlaceDate : workPlaceHistory) {
				historyStartDateList.add(workPlaceDate.start());
			}
		}

		// get 所属職場の履歴
		List<ExWorkPlaceHistoryImport> exWorkPlaceHistoryImports = employeeGeneralInfoImport
				.getExWorkPlaceHistoryImports();
		// filter 所属職場の履歴 follow employeeID
		List<ExWorkPlaceHistoryImport> newExWorkPlaceHistoryImports = exWorkPlaceHistoryImports.stream()
				.filter(item -> item.getEmployeeId().equals(employeeID)).collect(Collectors.toList());
		// get all workPlaceHistory
		List<ExWorkplaceHistItemImport> workplaceItems = new ArrayList<>();
		newExWorkPlaceHistoryImports.stream().forEach(item -> {
			workplaceItems.addAll(item.getWorkplaceItems());
		});
		// add all startDate
		if (workplaceItems.size() > 1) {
			for (ExWorkplaceHistItemImport itemImport : workplaceItems) {
				if (!historyStartDateList.stream().anyMatch(item -> item.equals(itemImport.getPeriod().start()))) {
					historyStartDateList.add(itemImport.getPeriod().start());
				}
			}
		}

		// get 所属職位の履歴
		List<ExJobTitleHistoryImport> exJobTitleHistoryImports = employeeGeneralInfoImport
				.getExJobTitleHistoryImports();
		// filter 所属職位の履歴 follow employeeID
		List<ExJobTitleHistoryImport> newExJobTitleHistoryImports = exJobTitleHistoryImports.stream()
				.filter(item -> item.getEmployeeId().equals(employeeID)).collect(Collectors.toList());
		// get all jobTitleHistory
		List<ExJobTitleHistItemImport> jobTitleItems = new ArrayList<>();
		newExJobTitleHistoryImports.stream().forEach(item -> {
			jobTitleItems.addAll(item.getJobTitleItems());
		});
		// add all startDate
		if (jobTitleItems.size() > 1) {
			for (ExJobTitleHistItemImport jobTitleHistItemImport : jobTitleItems) {
				if (!historyStartDateList.stream()
						.anyMatch(item -> item.equals(jobTitleHistItemImport.getPeriod().start()))) {
					historyStartDateList.add(jobTitleHistItemImport.getPeriod().start());
				}
			}
		}

		// 労働条件の履歴が区切られている年月日を判断する
		// filter 労働条件の履歴 follow employeeID
		if (mapLstDateHistoryItem.containsKey(employeeID)) {
			List<DateHistoryItem> dateHistoryItems = mapLstDateHistoryItem.get(employeeID);
			if (dateHistoryItems.size() > 1) {
				for (DateHistoryItem dateHistoryItem : dateHistoryItems) {
					if (!historyStartDateList.stream().anyMatch(item -> item.equals(dateHistoryItem.start()))) {
						historyStartDateList.add(dateHistoryItem.start());
					}
				}
			}
		}

		historyStartDateList.sort((item1, item2) -> item1.compareTo(item2));

		return historyStartDateList;
	}

	/**
	 * 日別実績の作成 (Use KIF(KDW001))
	 * @param asyncContext
	 * @param periodTime
	 * @param executionAttr
	 * @param companyId
	 * @param empCalAndSumExecLogID
	 * @param executionLog
	 * @param dataSetter
	 * @param employeeGeneralInfoImport
	 * @param stateHolder
	 * @param employeeId
	 * @param stampReflectionManagement
	 * @param mapWorkingConditionItem
	 * @param mapDateHistoryItem
	 * @param periodInMasterList
	 * @param executionType
	 * @param checkLock
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public OutputCreateDailyResult createDataNew(AsyncCommandHandlerContext asyncContext, DatePeriod periodTime,
			ExecutionAttr executionAttr, String companyId,
			Optional<EmpCalAndSumExeLog> empCalAndSumExeLog, TaskDataSetter dataSetter,
			EmployeeGeneralInfoImport employeeGeneralInfoImport, StateHolder stateHolder, String employeeId,
			Optional<StampReflectionManagement> stampReflectionManagement,
			Map<String, Map<String, WorkingConditionItem>> mapWorkingConditionItem,
			Map<String, Map<String, DateHistoryItem>> mapDateHistoryItem, PeriodInMasterList periodInMasterList,
			ExecutionTypeDaily executionType, Optional<Boolean> checkLock) {

		/**
		 * 勤務種別変更時に再作成 = false reCreateWorkType 異動時に再作成 = false reCreateWorkPlace
		 * 休職・休業者再作成 = false reCreateRestTime
		 */
		OutputCreateDailyResult cStatus = createDailyResultEmployeeDomainServiceNew.createDailyResultEmployee(
				asyncContext, employeeId, periodTime, companyId, empCalAndSumExeLog, false, false,
				false, employeeGeneralInfoImport, stampReflectionManagement, mapWorkingConditionItem,
				mapDateHistoryItem, periodInMasterList, executionType, checkLock);

		// 暫定データの登録
		this.interimRemainDataMngRegisterDateChange.registerDateChange(companyId, employeeId,
				periodTime.datesBetween());
		if(empCalAndSumExeLog.isPresent()) {
			// ログ情報（実行内容の完了状態）を更新する
			updateExecutionStatusOfDailyCreation(employeeId, executionAttr.value, empCalAndSumExeLog.get().getEmpCalAndSumExecLogID());
		}

		if(dataSetter !=null) {
			// 状態確認
			if (cStatus.getProcessState() == ProcessState.SUCCESS) {
				dataSetter.updateData("dailyCreateCount", stateHolder.count() + 1);
			} else {
				dataSetter.updateData("dailyCreateStatus", ExeStateOfCalAndSum.STOPPING.nameId);
				return new OutputCreateDailyResult(ProcessState.INTERRUPTION, cStatus.getListErrorMessageInfo());
			}
		}
		return cStatus;
	}

	/**
	 * 日別実績の作成 (use KBT)
	 * @param asyncContext
	 * @param employeeId
	 * @param periodTime
	 * @param executionAttr
	 * @param companyId
	 * @param empCalAndSumExecLogID
	 * @param executionLog
	 * @param executionType
	 * @param checkLock
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public OutputCreateDailyResult createDataNewWithNoImport(AsyncCommandHandlerContext asyncContext, String employeeId,
			DatePeriod periodTime, ExecutionAttr executionAttr, String companyId,
			ExecutionTypeDaily executionType,Optional<EmpCalAndSumExeLog> empCalAndSumExeLog, Optional<Boolean> checkLock) {
		val dataSetter = asyncContext.getDataSetter(); //TODO
		
		Optional<StampReflectionManagement> stampReflectionManagement = this.stampReflectionManagementRepository
				.findByCid(companyId);

		EmployeeGeneralInfoImport employeeGeneralInfoImport = this.employeeGeneralInfoService
				.getEmployeeGeneralInfo(Arrays.asList(employeeId), periodTime);
		List<ExWorkTypeHistoryImport> exWorkTypeHistoryImports = this.businessTypeOfEmpHisAdaptor
				.findByCidSidBaseDate(companyId, Arrays.asList(employeeId), periodTime).stream()
				.map(c -> convertToBusinessTypeOfEmpDto(c)).collect(Collectors.toList());
		employeeGeneralInfoImport.setExWorkTypeHistoryImports(exWorkTypeHistoryImports);
		// 社員ID（List）と期間から労働条件を取得する
		List<WorkingConditionItem> workingConditionItems = this.workingConditionItemRepository
				.getBySidsAndDatePeriod(Arrays.asList(employeeId), periodTime);

		Map<String, List<WorkingConditionItem>> mapWOrking = workingConditionItems.parallelStream()
				.collect(Collectors.groupingBy(WorkingConditionItem::getEmployeeId));

		// Map<Sid, Map<HistoryID, List<WorkingConditionItem>>>
		Map<String, Map<String, WorkingConditionItem>> mapWorkingConditionItem = mapWOrking.entrySet()
				.parallelStream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream()
						.collect(Collectors.toMap(WorkingConditionItem::getHistoryId, Function.identity()))));

		List<WorkingCondition> workingConditions = workingConditionRepo.getBySidsAndDatePeriod(Arrays.asList(employeeId),
				periodTime);

		// Map<Sid, List<DateHistoryItem>>
		Map<String, List<DateHistoryItem>> mapLstDateHistoryItem = workingConditions.parallelStream().collect(
				Collectors.toMap(WorkingCondition::getEmployeeId, WorkingCondition::getDateHistoryItem));

		// Map<Sid, Map<HistoryID, DateHistoryItem>>
		Map<String, Map<String, DateHistoryItem>> mapDateHistoryItem = mapLstDateHistoryItem.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream()
						.collect(Collectors.toMap(DateHistoryItem::identifier, Function.identity()))));

		// 会社IDと期間から期間内の職場構成期間を取得する
		// ReqList485
		// List<DatePeriod> workPlaceHistory =
		// this.affWorkplaceAdapter.getLstPeriod(companyId, periodTime);

		// [No.647]期間に対応する職場構成を取得する
		List<WorkPlaceConfig> workPlaceConfigLst = this.affWorkplaceAdapter.findByCompanyIdAndPeriod(companyId,
				periodTime);
		List<DatePeriod> workPlaceHistory = new ArrayList<>();

		List<List<DatePeriod>> listWorkplaceHistory = workPlaceConfigLst.stream()
				.map(c -> c.getWkpConfigHistory().stream().map(m -> m.getPeriod()).collect(Collectors.toList()))
				.collect(Collectors.toList());

		workPlaceHistory.addAll(listWorkplaceHistory.get(0));
		StateHolder stateHolder = new StateHolder(1);
		
		DatePeriod newPeriod = this.checkPeriod(companyId, employeeId, periodTime);
		List<GeneralDate> historySeparatedList = this.historyIsSeparated(newPeriod,
				workPlaceHistory, employeeGeneralInfoImport, mapLstDateHistoryItem, employeeId);
		
		PeriodInMasterList periodInMasterList = getPeriodInMasterList(companyId, employeeId,
				newPeriod, historySeparatedList, employeeGeneralInfoImport);

		/**
		 * 勤務種別変更時に再作成 = false reCreateWorkType 異動時に再作成 = false reCreateWorkPlace
		 * 休職・休業者再作成 = false reCreateRestTime
		 */
		OutputCreateDailyResult cStatus = createDataNew(asyncContext, periodTime, executionAttr, companyId,
				empCalAndSumExeLog, dataSetter, employeeGeneralInfoImport, stateHolder, employeeId,
				stampReflectionManagement, mapWorkingConditionItem, mapDateHistoryItem, periodInMasterList,
				executionType, checkLock);
		if (cStatus.getProcessState() == ProcessState.SUCCESS) {
				for (ErrorMessageInfo errorMessageInfo : cStatus.getListErrorMessageInfo()) {
					String empCalAndSumExeLogId = empCalAndSumExeLog.isPresent()?empCalAndSumExeLog.get().getEmpCalAndSumExecLogID():null;
					// 日別実績の作成エラー処理
					errorHandlingCreateDailyResults.executeCreateError(companyId, errorMessageInfo.getEmployeeID(),
							errorMessageInfo.getProcessDate(), empCalAndSumExeLogId, errorMessageInfo.getExecutionContent(),
							errorMessageInfo.getResourceID(), errorMessageInfo.getMessageError());
				}
				if(empCalAndSumExeLog.isPresent()) {
					updateLogInfoWithNewTransaction.updateLogInfo(empCalAndSumExeLog.get().getEmpCalAndSumExecLogID(), 0,
						ExecutionStatus.DONE.value);
				}
		}
		return cStatus;
	}
	
	/**
	 * 日別実績の作成 (not use async)
	 * @param asyncContext
	 * @param employeeId
	 * @param periodTime
	 * @param executionAttr
	 * @param companyId
	 * @param empCalAndSumExecLogID
	 * @param executionLog
	 * @param executionType
	 * @param checkLock
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public OutputCreateDailyResult createDataNewNotAsync(String employeeId,
			DatePeriod periodTime, ExecutionAttr executionAttr, String companyId,
			ExecutionTypeDaily executionType,Optional<EmpCalAndSumExeLog> empCalAndSumExeLog, Optional<Boolean> checkLock) {
		
		Optional<StampReflectionManagement> stampReflectionManagement = this.stampReflectionManagementRepository
				.findByCid(companyId);

		EmployeeGeneralInfoImport employeeGeneralInfoImport = this.employeeGeneralInfoService
				.getEmployeeGeneralInfo(Arrays.asList(employeeId), periodTime);
		List<ExWorkTypeHistoryImport> exWorkTypeHistoryImports = this.businessTypeOfEmpHisAdaptor
				.findByCidSidBaseDate(companyId, Arrays.asList(employeeId), periodTime).stream()
				.map(c -> convertToBusinessTypeOfEmpDto(c)).collect(Collectors.toList());
		employeeGeneralInfoImport.setExWorkTypeHistoryImports(exWorkTypeHistoryImports);
		
		// 社員ID（List）と期間から労働条件を取得する
		List<WorkingConditionItem> workingConditionItems = this.workingConditionItemRepository
				.getBySidsAndDatePeriod(Arrays.asList(employeeId), periodTime);

		Map<String, List<WorkingConditionItem>> mapWOrking = workingConditionItems.parallelStream()
				.collect(Collectors.groupingBy(WorkingConditionItem::getEmployeeId));

		// Map<Sid, Map<HistoryID, List<WorkingConditionItem>>>
		Map<String, Map<String, WorkingConditionItem>> mapWorkingConditionItem = mapWOrking.entrySet()
				.parallelStream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream()
						.collect(Collectors.toMap(WorkingConditionItem::getHistoryId, Function.identity()))));

		List<WorkingCondition> workingConditions = workingConditionRepo.getBySidsAndDatePeriod(Arrays.asList(employeeId),
				periodTime);

		// Map<Sid, List<DateHistoryItem>>
		Map<String, List<DateHistoryItem>> mapLstDateHistoryItem = workingConditions.parallelStream().collect(
				Collectors.toMap(WorkingCondition::getEmployeeId, WorkingCondition::getDateHistoryItem));

		// Map<Sid, Map<HistoryID, DateHistoryItem>>
		Map<String, Map<String, DateHistoryItem>> mapDateHistoryItem = mapLstDateHistoryItem.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream()
						.collect(Collectors.toMap(DateHistoryItem::identifier, Function.identity()))));

		// 会社IDと期間から期間内の職場構成期間を取得する
		// ReqList485
		// List<DatePeriod> workPlaceHistory =
		// this.affWorkplaceAdapter.getLstPeriod(companyId, periodTime);

		// [No.647]期間に対応する職場構成を取得する
		List<WorkPlaceConfig> workPlaceConfigLst = this.affWorkplaceAdapter.findByCompanyIdAndPeriod(companyId,
				periodTime);
		List<DatePeriod> workPlaceHistory = new ArrayList<>();

		List<List<DatePeriod>> listWorkplaceHistory = workPlaceConfigLst.stream()
				.map(c -> c.getWkpConfigHistory().stream().map(m -> m.getPeriod()).collect(Collectors.toList()))
				.collect(Collectors.toList());

		workPlaceHistory.addAll(listWorkplaceHistory.get(0));
		StateHolder stateHolder = new StateHolder(1);
		
		DatePeriod newPeriod = this.checkPeriod(companyId, employeeId, periodTime);
		List<GeneralDate> historySeparatedList = this.historyIsSeparated(newPeriod,
				workPlaceHistory, employeeGeneralInfoImport, mapLstDateHistoryItem, employeeId);
		
		PeriodInMasterList periodInMasterList = getPeriodInMasterList(companyId, employeeId,
				newPeriod, historySeparatedList, employeeGeneralInfoImport);

		/**
		 * 勤務種別変更時に再作成 = false reCreateWorkType 異動時に再作成 = false reCreateWorkPlace
		 * 休職・休業者再作成 = false reCreateRestTime
		 */
		OutputCreateDailyResult cStatus = createDataNew(null, periodTime, executionAttr, companyId,
				empCalAndSumExeLog, null, employeeGeneralInfoImport, stateHolder, employeeId,
				stampReflectionManagement, mapWorkingConditionItem, mapDateHistoryItem, periodInMasterList,
				executionType, checkLock);
		return cStatus;
	}
	
	private void updateExecutionStatusOfDailyCreation(String employeeID, int executionAttr,
			String empCalAndSumExecLogID) {

		if (executionAttr == 0) {
			targetPersonRepository.update(employeeID, empCalAndSumExecLogID, ExecutionStatus.DONE.value);
		}
	}

	// 特定日、加給、計算区分情報を取得する
	private PeriodInMasterList getPeriodInMasterList(String companyId, String employeeId, DatePeriod period,
			List<GeneralDate> historySeparatedList, EmployeeGeneralInfoImport employeeGeneralInfoImport) {
		PeriodInMasterList periodInMasterList = new PeriodInMasterList();
		List<MasterList> masterLists = new ArrayList<>();

		if (historySeparatedList.size() > 0) {
			for (int i = 0; i < historySeparatedList.size(); i++) {
				GeneralDate strDate = historySeparatedList.get(i);
				GeneralDate endDate = (i == historySeparatedList.size() - 1) ? period.end()
						: historySeparatedList.get(i + 1).addDays(-1);

				// get 職場履歴一覧
				List<ExWorkPlaceHistoryImport> exWorkPlaceHistoryImports = employeeGeneralInfoImport
						.getExWorkPlaceHistoryImports();
				// get 職位履歴一覧
				List<ExJobTitleHistoryImport> exJobTitleHistoryImports = employeeGeneralInfoImport
						.getExJobTitleHistoryImports();
				// filter follow employeeId
				Optional<ExWorkPlaceHistoryImport> optional = exWorkPlaceHistoryImports.stream()
						.filter(item -> item.getEmployeeId().equals(employeeId)).findFirst();
				Optional<ExJobTitleHistoryImport> jobTitleOptional = exJobTitleHistoryImports.stream()
						.filter(item -> item.getEmployeeId().equals(employeeId)).findFirst();
				// get workPlaceItem
				List<ExWorkplaceHistItemImport> workplaceItems = optional.isPresent()
						? optional.get().getWorkplaceItems()
						: new ArrayList<>();
				// get jobTitleHistItem
				List<ExJobTitleHistItemImport> jobTitleItems = jobTitleOptional.isPresent()
						? jobTitleOptional.get().getJobTitleItems()
						: new ArrayList<>();
				// filter : DatePeriod of workplaceItems has
				// contains start date
				Optional<ExWorkplaceHistItemImport> itemImport = workplaceItems.stream()
						.filter(item -> item.getPeriod().contains(strDate)).findFirst();
				// filter : DatePeriod of jobTitleItems has
				// contains start date
				Optional<ExJobTitleHistItemImport> jobTitleItemImport = jobTitleItems.stream()
						.filter(item -> item.getPeriod().contains(strDate)).findFirst();
				// get workPlaceId
				String workPlaceId = itemImport.isPresent() ? itemImport.get().getWorkplaceId() : null;
				// get jobTitleId
				String jobTitleId = jobTitleItemImport.isPresent() ? jobTitleItemImport.get().getJobTitleId() : null;

				if (workPlaceId != null && jobTitleId != null) {
					// 職場IDと基準日から上位職場を取得する
					// reqList 496
					// List<String> workPlaceIdList = this.affWorkplaceAdapter
					// .findParentWpkIdsByWkpId(companyId, workPlaceId, strDate);
					// [No.569]職場の上位職場を取得する
					List<String> workPlaceIdList = this.affWorkplaceAdapter.getUpperWorkplace(companyId, workPlaceId,
							strDate);

					// 特定日設定を取得する
					// Reqlist 490
					List<RecSpecificDateSettingImport> specificDateSettingImport = this.recSpecificDateSettingAdapter
							.getList(companyId, workPlaceIdList, period);;

					// 会社職場個人の加給設定を取得する
					Optional<BonusPaySetting> bonusPaySettingOpt = this.reflectBonusSetting(companyId, employeeId,
							strDate, workPlaceIdList);

					// 自動計算設定の取得
					BaseAutoCalSetting baseAutoCalSetting = this.autoCalculationSetService.getAutoCalculationSetting(
							companyId, employeeId, strDate, workPlaceId, jobTitleId, Optional.of(workPlaceIdList));

					// set data in PeriodInMasterList
					DatePeriod datePeriod = new DatePeriod(strDate, endDate);

					MasterList masterList = new MasterList();
					masterList.setBaseAutoCalSetting(baseAutoCalSetting);
					masterList.setBonusPaySettingOpt(bonusPaySettingOpt);
					masterList.setDatePeriod(datePeriod);
					masterList.setSpecificDateSettingImport(specificDateSettingImport);

					masterLists.add(masterList);
				}
			}
			periodInMasterList.setEmployeeId(employeeId);
			periodInMasterList.setMasterLists(masterLists);
		}
		return periodInMasterList;
	}

	class StateHolder {
		private BlockingQueue<ProcessState> status;

		StateHolder(int max) {
			status = new ArrayBlockingQueue<ProcessState>(max);
		}

		void add(ProcessState status) {
			this.status.add(status);
		}

		int count() {
			return this.status.size();
		}

		boolean isInterrupt() {
			return this.status.stream().filter(s -> s == ProcessState.INTERRUPTION).findFirst().isPresent();
		}
	}
	
	
}
