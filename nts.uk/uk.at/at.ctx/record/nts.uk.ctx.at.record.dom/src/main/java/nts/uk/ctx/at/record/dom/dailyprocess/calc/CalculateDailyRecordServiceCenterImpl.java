package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.ProcessState;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.errorcheck.CalculationErrorCheckService;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLogRepository;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.attendance.MasterShareBus;
import nts.uk.ctx.at.shared.dom.attendance.MasterShareBus.MasterShareContainer;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemService;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.CommonCompanySettingForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.CalculateOption;
import nts.uk.ctx.at.shared.dom.scherec.dailyprocess.calc.FactoryManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculationRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpCondition;
import nts.uk.ctx.at.shared.dom.scherec.optitem.applicable.EmpConditionRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.Formula;
import nts.uk.ctx.at.shared.dom.scherec.optitem.calculation.FormulaRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.worktime.common.JustCorrectionAtr;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CalculateDailyRecordServiceCenterImpl implements CalculateDailyRecordServiceCenter{
	
	//リポジトリ：労働条件
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;
	
	//計算処理
	@Inject
	private CalculateDailyRecordService calculate;
	
	//エラーチェック処理
	@Inject
	private CalculationErrorCheckService calculationErrorCheckService;
	
	//リポジトリ：勤務情報
	@Inject
	private WorkInformationRepository workInformationRepository;
	
	//割増計算用に追加
	@Inject
	private PersonCostCalculationRepository personCostCalculationRepository;
	
	//計算を動かすための会社共通設定取得
	@Inject
	private CommonCompanySettingForCalc commonCompanySettingForCalc;
	
	/*回数・時間・任意項目の勤怠項目IDを取得するためのサービス*/
	@Inject
	private AttendanceItemService attendanceItemService;
	
	/*日別実績の編集状態*/
	@Inject
	private EditStateOfDailyPerformanceRepository dailyEditStateRepo;
	
//	@Inject
//	private DailyCalculationEmployeeService dailyCalculationEmployeeService;
	
	// 任意項目の計算の為に追加
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	//任意項目
	@Inject
	private OptionalItemRepository optionalItemRepository;
	
	//労働条件
	@Inject
	private EmpConditionRepository empConditionRepository;
	//計算式
	@Inject
	private FormulaRepository formulaRepository;
	
	/** リポジトリ：就業計算と集計実行ログ */
	@Inject
	private EmpCalAndSumExeLogRepository empCalAndSumExeLogRepository;
	
	/** 勤怠項目と勤怠項目の実際の値のマッピング */
	@Inject
	private AttendanceItemConvertFactory attendanceItemConvertFactory;
		
	/** 社員設定管理ファクトリー */
	@Inject
	private FactoryManagePerPersonDailySet factoryManagePerPersonDailySet;
	
	
	private Optional<BsEmploymentHistoryImport> c(String companyId, String employeeId, GeneralDate targetDate) {
		return this.shareEmploymentAdapter.findEmploymentHistory(companyId, employeeId, targetDate);
	}

	private List<OptionalItem> getOptionalItemsFromRepository() {
		return optionalItemRepository.findAll(AppContexts.user().companyCode());
	}
	
	private List<EmpCondition> getEmpConditionFromRepository(List<Integer> optionalIds) {
		return empConditionRepository.findAll(AppContexts.user().companyCode(), optionalIds);
	}
	
	
	@Override
	//old_process. Don't use!
	public List<IntegrationOfDaily> calculate(List<IntegrationOfDaily> integrationOfDaily){
//		return commonPerCompany(CalculateOption.asDefault(), integrationOfDaily,false,Optional.empty(),Optional.empty(),Optional.empty(),Collections.emptyList()).getIntegrationOfDailyList();
		List<IntegrationOfDaily> result = calculatePassCompanySetting(CalculateOption.asDefault(), integrationOfDaily, Optional.empty(), ExecutionType.NORMAL_EXECUTION);
		
		return result;
	}
	
	private List<OptionalItem> getOptionalItemsFromRepository(Optional<ManagePerCompanySet> companySet) {
		List<OptionalItem> opItems = new ArrayList<>();
		if(companySet.isPresent()) {
			opItems = companySet.get().getOptionalItems();
		}
		else {
			opItems = getOptionalItemsFromRepository();
		}
		return opItems;
	}
	
	private List<EmpCondition> getEmpConditions(Optional<ManagePerCompanySet> companySet, List<OptionalItem> opItems) {
		List<EmpCondition> empCond = new ArrayList<>();
		if(companySet.isPresent()) {
			empCond = companySet.get().getEmpCondition();
		}
		else {
			empCond = getEmpConditionFromRepository(opItems.stream().map(opItem -> opItem.getOptionalItemNo().v()).collect(Collectors.toList()));
		}
		return empCond;
	}

	private List<Formula> getFormula(Optional<ManagePerCompanySet> companySet){
		if(companySet.isPresent()) {
			return companySet.get().getFormulaList();
		}
		return formulaRepository.find(AppContexts.user().companyCode()); 
	}
	
	private List<Integer> getUseOpIds(List<OptionalItem> opItems, List<EmpCondition> empCond, String employeeId, GeneralDate targetDate, List<Formula> formulaList) {
		List<Integer> useOptionalIds = new ArrayList<>();
		val bse = c(AppContexts.user().companyCode(), employeeId, targetDate);
		for(OptionalItem opItem:opItems) {
			if(decisionUseSetting(opItem,empCond,bse) && formulaList.stream().filter(formula -> formula.getOptionalItemNo().equals(opItem.getOptionalItemNo())).collect(Collectors.toList()).size() > 0) {
			//if(decisionUseSetting(opItem,empCond,bse)) {
				useOptionalIds.add(opItem.getOptionalItemNo().v());
			}
			
		}
		return useOptionalIds;
	}
	

	
	private boolean decisionUseSetting(OptionalItem optionalItem, List<EmpCondition> empConditionList, Optional<BsEmploymentHistoryImport> bsEmploymentHistOpt) {
		return AnyItemValueOfDaily.decisionCondition(optionalItem, empConditionList, bsEmploymentHistOpt);
	}
	
	private boolean isIncludeId(ItemValue id,List<Integer> useOpIds) {
		if(AttendanceItemIdContainer.isOptionalItem(id)) {
			final Map<Integer, Integer> optionalItemMap = AttendanceItemIdContainer.mapOptionalItemIdsToNos();
			
			if(optionalItemMap.containsKey(id.getItemId())) {
				if(useOpIds.contains(optionalItemMap.get(id.getItemId()))) {
					//編集状態を消す対象
					return true;
				}
				else {
					//編集状態を残す対象
					return false;
				}
			}
			//編集状態を残す対象
			return false;
		}
		//編集状態を消す対象
		return true;
	}
	

	@Override
	//スケジュール・申請から呼び出す窓口
	public List<IntegrationOfDaily> calculateForSchedule(CalculateOption calcOption, List<IntegrationOfDaily> integrationOfDaily, Optional<ManagePerCompanySet> companySet) {
		
		return commonPerCompany(calcOption, integrationOfDaily, companySet, Collections.emptyList(), JustCorrectionAtr.NOT_USE, Optional.empty())
				.getLst().stream().map(tc -> tc.getIntegrationOfDaily()).collect(Collectors.toList());
	}
	
	@Override
	//会社共通の設定を他のコンテキストで取得できる場合に呼び出す窓口
	public List<IntegrationOfDaily> calculatePassCompanySetting(CalculateOption calcOption,
			List<IntegrationOfDaily> integrationOfDailys, Optional<ManagePerCompanySet> companySet, ExecutionType reCalcAtr){
		
		val result = calculateForManageStateInternal(companySet, calcOption, 
				integrationOfDailys, new ArrayList<>(), reCalcAtr, Optional.empty(), 
				//時間・回数の勤怠項目だけ　編集状態テーブルから削除
				(iod, clearedItems) -> dailyEditStateRepo.deleteByListItemId(iod.getEmployeeId(), iod.getYmd(), clearedItems));
		
		return result.getLst().stream().map(ts -> ts.getIntegrationOfDaily()).collect(Collectors.toList()); 
	}
	
	@Override
	//就業計算と集計から呼び出す時の窓口
	public ManageProcessAndCalcStateResult calculateForManageState(
			List<IntegrationOfDaily> integrationOfDailys,
			List<ClosureStatusManagement> closureList,
			ExecutionType reCalcAtr,
			String executeLogId){

		Map<Pair<String, GeneralDate>, AtomTask> atomTasks = new HashMap<>();
		
		val setting = Optional.of(commonCompanySettingForCalc.getCompanySetting());
		
		//時間・回数の勤怠項目だけ　編集状態テーブルから削除
		BiConsumer<IntegrationOfDaily, List<Integer>> actionOnClearedItemIds = (iod, clearedItems) -> {
			atomTasks.put(Pair.of(iod.getEmployeeId(), iod.getYmd()), AtomTask.of(() -> {
				dailyEditStateRepo.deleteByListItemId(iod.getEmployeeId(), iod.getYmd(), clearedItems);
			}));
		};
		
		val result = calculateForManageStateInternal(setting, CalculateOption.asDefault(), integrationOfDailys, 
				closureList, reCalcAtr, Optional.of(executeLogId), actionOnClearedItemIds);
		
		return result;
	}
	
	private ManageProcessAndCalcStateResult calculateForManageStateInternal(Optional<ManagePerCompanySet> companySet,
			CalculateOption calcOption, List<IntegrationOfDaily> integrationOfDailys, 
			List<ClosureStatusManagement> closureList, ExecutionType reCalcAtr, Optional<String> executeLogId, 
			BiConsumer<IntegrationOfDaily, List<Integer>> actionOnClearedItemIds) {
		
		if(reCalcAtr.isRerun()) {

			val itemId = attendanceItemService.getTimeAndCountItem(AttendanceItemType.DAILY_ITEM);
//			val beforeChangeItemId = new ArrayList<ItemValue>(itemId);
			
			val optionalItems = getOptionalItemsFromRepository(companySet);
			val empConds = getEmpConditions(companySet, optionalItems);
			val formula = getFormula(companySet);
			
			integrationOfDailys.forEach(iod -> {
				List<EditStateOfDailyAttd> notReCalcItems = new ArrayList<>();
//				itemId.clear();
//				itemId.addAll(beforeChangeItemId);
				
				List<Integer> useOpIds = getUseOpIds(optionalItems, empConds, iod.getEmployeeId(), iod.getYmd(),formula);
				
				val itemIdsDeletedEdit = itemId.stream().filter(id ->{
					return isIncludeId(id, useOpIds);
				}).map(item -> item.getItemId()).collect(Collectors.toList());
				
				iod.getEditState().forEach(edit ->{
					//任意＋時間or回数以外の項目の編集状態残る
					if(!itemIdsDeletedEdit.contains(edit.getAttendanceItemId()))
						notReCalcItems.add(edit);
					
				});
				iod.setEditState(notReCalcItems);
				
				actionOnClearedItemIds.accept(iod, itemIdsDeletedEdit);
			});

		}
		return commonPerCompany(calcOption, integrationOfDailys, companySet, closureList, JustCorrectionAtr.USE, executeLogId);
	}



	
	@Override
	//更新処理自動実行から呼び出す窓口
	public ManageProcessAndCalcStateResult calculateForclosure(List<IntegrationOfDaily> integrationOfDaily,
			ManagePerCompanySet companySet, List<ClosureStatusManagement> closureList, String logId) {
		
		return commonPerCompany(CalculateOption.asDefault(), integrationOfDaily, Optional.empty(), closureList, JustCorrectionAtr.USE, Optional.of(logId));
	}
	
	
	/**
	 * 会社共通の処理達
	 * @param integrationOfDaily 実績データたち
	 * @param companySet 
	 * @param closureList 
	 * @param justCorrectionAtr ジャスト補正区分
	 * @return 計算後実績データ
	 */
	private ManageProcessAndCalcStateResult commonPerCompany(CalculateOption calcOption, List<IntegrationOfDaily> integrationOfDailys,
			Optional<ManagePerCompanySet> companySet, List<ClosureStatusManagement> closureList,
			JustCorrectionAtr justCorrectionAtr, Optional<String> logId ) {
		
		/***会社共通処理***/
		if(integrationOfDailys.isEmpty()) 
			return new ManageProcessAndCalcStateResult(ProcessState.SUCCESS, integrationOfDailys.stream().map(tc -> ManageCalcStateAndResult.failCalc(tc, attendanceItemConvertFactory)).collect(Collectors.toList()));
		//社員毎の実績に纏める
		Map<String,List<IntegrationOfDaily>> recordPerEmpId = getPerEmpIdRecord(integrationOfDailys);
		String comanyId = AppContexts.user().companyId();
		//会社共通の設定を
		ManagePerCompanySet companyCommonSetting = companySet.orElseGet(() -> {
			return commonCompanySettingForCalc.getCompanySetting();
		});
		
		boolean mustCleanShareContainer = false;
		if (companyCommonSetting.getShareContainer() == null) {
			mustCleanShareContainer = true;
			MasterShareContainer<String> shareContainer = MasterShareBus.open();
			companyCommonSetting.setShareContainer(shareContainer);
		}
		
		companyCommonSetting.setPersonnelCostSetting(personCostCalculationRepository.getHistAnPerCost(comanyId));
		
		/***会社共通処理***/
		List<ManageCalcStateAndResult> returnList = new ArrayList<>();
		
		
		
		//社員ごとの処理
		for(Entry<String, List<IntegrationOfDaily>> record: recordPerEmpId.entrySet()) {
			if(logId.isPresent()) {
				Optional<EmpCalAndSumExeLog> log = empCalAndSumExeLogRepository.getByEmpCalAndSumExecLogID(logId.get());
				if(!log.isPresent()) {
					
					return new ManageProcessAndCalcStateResult(ProcessState.INTERRUPTION,returnList);
				}
				else {
					val executionStatus = log.get().getExecutionStatus();
					if(executionStatus.isPresent() && executionStatus.get().isStartInterruption()) {
						return new ManageProcessAndCalcStateResult(ProcessState.INTERRUPTION,returnList);
					}
				}				
			}
			//対象社員の締め取得
			List<ClosureStatusManagement> closureByEmpId = getclosure(record.getKey(), closureList);
			//日毎の処理
			val returnValue = calcOnePerson(calcOption, comanyId,record.getValue(),companyCommonSetting,closureByEmpId, justCorrectionAtr);
			returnList.addAll(returnValue);
			//人数カウントアップ
//			if(counter.isPresent()) {
//				counter.get().accept(ProcessState.SUCCESS);
//			}
		}

		if (mustCleanShareContainer) {
			companyCommonSetting.getShareContainer().clearAll();
			companyCommonSetting.setShareContainer(null);
		}
		
		return new ManageProcessAndCalcStateResult(ProcessState.SUCCESS,returnList);
		
	}
	

	/**
	 * 対象者の締め一覧を取得する
	 * @param empId 対象者
	 * @param closureList 締め一覧
	 * @return 対象者の締め一覧
	 */
	private List<ClosureStatusManagement> getclosure(String empId,List<ClosureStatusManagement> closureList){
		return closureList.stream().filter(closureStatus -> closureStatus.getEmployeeId().equals(empId)).collect(Collectors.toList());
	}
	
	/**
	 * 日毎のメイン処理
	 * @param comanyId 会社 ID
	 * @param recordList 実績データのリスト
	 * @param companyCommonSetting 会社共通の設定
	 * @param closureByEmpId 
	 * @param justCorrectionAtr ジャスト補正区分
	 * @return　実績データ
	 */
	private List<ManageCalcStateAndResult> calcOnePerson(CalculateOption calcOption, String companyId, List<IntegrationOfDaily> recordList, 
			ManagePerCompanySet companyCommonSetting, List<ClosureStatusManagement> closureByEmpId,JustCorrectionAtr justCorrectionAtr){
		
		//社員の期間取得
		val integraListByRecordAndEmpId = getIntegrationOfDailyByEmpId(recordList);
		val map = convertMap(recordList);
		List<GeneralDate> sortedymd = recordList.stream()
								  			.sorted((first,second) -> first.getYmd().compareTo(second.getYmd()))
								  			.map(IntegrationOfDaily -> IntegrationOfDaily.getYmd())
								  			.collect(Collectors.toList());
		//開始日
		val minGeneralDate = sortedymd.get(0);
		//終了日
		val maxGeneralDate = sortedymd.get(sortedymd.size() - 1);
		
		
		//労働制マスタ取得
		val masterData = workingConditionItemRepository.getBySidAndPeriodOrderByStrDWithDatePeriod(integraListByRecordAndEmpId,maxGeneralDate,minGeneralDate);

		//日ごとループ(1人社員の)
		List<ManageCalcStateAndResult> returnList = new ArrayList<>();
		for(IntegrationOfDaily record:recordList) {
			
			//締め一覧から、ymdが計算可能な日かを判定する
			if(!isCalc(closureByEmpId, record.getYmd())) {
				returnList.add(ManageCalcStateAndResult.successCalc(record));
				continue;
			}
			
			//nowIntegrationの労働制取得
			Optional<Entry<DateHistoryItem, WorkingConditionItem>> nowWorkingItem = masterData.getItemAtDateAndEmpId(record.getYmd(),record.getEmployeeId());
			if(nowWorkingItem.isPresent()) {
				
				Optional<ManagePerPersonDailySet> personSetting = factoryManagePerPersonDailySet.create(companyId, companyCommonSetting, record, nowWorkingItem.get().getValue());
				
				ManageCalcStateAndResult result;
				if(personSetting.isPresent()) {
					//実績計算
					result = calculate.calculate(calcOption, record, 
														companyCommonSetting,
														personSetting.get(),
														justCorrectionAtr,
														findAndGetWorkInfo(record.getEmployeeId(),map,record.getYmd().addDays(-1)),
														findAndGetWorkInfo(record.getEmployeeId(),map,record.getYmd().addDays(1)));
				} else {
					result = ManageCalcStateAndResult.failCalc(record, attendanceItemConvertFactory);
				}
				result.getIntegrationOfDaily().getWorkInformation().changeCalcState(CalculationState.Calculated);
				returnList.add(result);
			}
			else {
				returnList.add(ManageCalcStateAndResult.successCalc(record));
			}
		}
		return returnList;
	}
	
	/**
	 * 計算可能な日かを判定する
	 * @param closureList　締め一覧
	 * @param ymd　対象年月日
	 * @return　計算してもよい
	 */
	public boolean isCalc(List<ClosureStatusManagement> closureList, GeneralDate ymd) {
		for(ClosureStatusManagement closure : closureList) {
			if(closure.getPeriod().contains(ymd))
				return false;
		}
		return true;
	}
	

	//社員毎の実績にまとめる
	private Map<String, List<IntegrationOfDaily>> getPerEmpIdRecord(List<IntegrationOfDaily> integrationOfDailys) {
		List<String> empIdList = integrationOfDailys.stream().map(integrationOfDaily -> integrationOfDaily.getEmployeeId()).distinct().collect(Collectors.toList());
		Map<String, List<IntegrationOfDaily>> returnMap = new HashMap<>();
		for(String empId : empIdList) {
			val integrations = integrationOfDailys.stream().filter(integrationOfDaily -> integrationOfDaily.getEmployeeId().equals(empId)).collect(Collectors.toList());
			returnMap.put(empId, integrations);
		}
		return returnMap;
	}
	
	
	/**
	 * List→Mapへの変換クラス
	 * @param integrationOfDaily 日別実績(Work)
	 * @return integrationOfDailyのMap
	 */
	private Map<GeneralDate, IntegrationOfDaily> convertMap(List<IntegrationOfDaily> integrationOfDailys) {
		Map<GeneralDate, IntegrationOfDaily> map = new HashMap<>();
		integrationOfDailys.forEach(integrationOfDaily ->{
			map.put(integrationOfDaily.getYmd(), integrationOfDaily);
		});
		return map;
	}
	
	/**
	 * 前日翌日(parameterによってどっちにするか決める)の勤務情報を取得する
	 * @param empId
	 * @param mapIntegration
	 * @param addDays　取得したい日(-1or+1した日を渡す)
	 * @return addDaysの勤務情報
	 */
	private Optional<WorkInfoOfDailyPerformance> findAndGetWorkInfo(String empId, Map<GeneralDate, IntegrationOfDaily> mapIntegration, GeneralDate addDays) {
		if(mapIntegration.containsKey(addDays)) {
			WorkInfoOfDailyPerformance data = new WorkInfoOfDailyPerformance(empId, addDays, mapIntegration.get(addDays).getWorkInformation());
			return Optional.of(data);
		}
		else {
			return workInformationRepository.find(empId, addDays);
		}
	}
	
	/**
	 * 日別実績(WORK)Listから社員毎の計算したい期間を取得する 
	 * @param integrationOfDaily
	 * @return　<社員、計算したい期間
	 */
	private Map<String,DatePeriod> getIntegrationOfDailyByEmpId(List<IntegrationOfDaily> integrationOfDaily) {
		Map<String,DatePeriod> returnMap = new HashMap<>();
		//しゃいんID 一覧取得
		List<String> idList = getAllEmpId(integrationOfDaily);
		idList.forEach(id -> {
			//特定の社員IDに一致しているintegrationに絞る
			val integrationOfDailys = integrationOfDaily.stream().filter(tc -> tc.getEmployeeId().equals(id)).collect(Collectors.toList());
			//特定社員の開始～終了を取得する
			val createdDatePriod = getDateSpan(integrationOfDailys);
			//Map<特定の社員ID、開始～終了>に追加する
			returnMap.put(id, createdDatePriod);
		});
		return returnMap;
	}
	
	/*
	 * 社員の一覧取得
	 */
	private List<String> getAllEmpId(List<IntegrationOfDaily> integrationOfDailys){
		return integrationOfDailys.stream().distinct().map(integrationOfDaily->integrationOfDaily.getEmployeeId()).collect(Collectors.toList());
	}
	
	/**
	 * 開始～終了の作成
	 * @param integrationOfDaily 日別実績(WORK)LIST
	 * @return 開始～終了
	 */
	private DatePeriod getDateSpan(List<IntegrationOfDaily> integrationOfDailys) {
		val sortedIntegration = integrationOfDailys.stream().sorted((first,second) -> first.getYmd().compareTo(second.getYmd())).collect(Collectors.toList());
		return new DatePeriod(sortedIntegration.get(0).getYmd(), sortedIntegration.get(sortedIntegration.size() - 1).getYmd());
	}
	
	/**
	 * エラーチェック(外部から呼ぶ用)
	 * @param integrationList
	 * @return
	 */
	@Override
	public List<IntegrationOfDaily> errorCheck(CompanyId companyId, List<IntegrationOfDaily> integrationList){
		if(integrationList.isEmpty()) return integrationList;
		//会社共通の設定を
		val companyCommonSetting = commonCompanySettingForCalc.getCompanySetting();

		//社員毎の期間取得
		val integraListByRecordAndEmpId = getIntegrationOfDailyByEmpId(integrationList);
		
		List<GeneralDate> sortedymd = integrationList.stream()
								  					 	.sorted((first,second) -> first.getYmd().compareTo(second.getYmd()))
								  					 	.map(integrationOfDaily -> integrationOfDaily.getYmd())
								  					 	.collect(Collectors.toList());
		
		val minGeneralDate = sortedymd.get(0);
		val maxGeneralDate = sortedymd.get(sortedymd.size() - 1);
		
		//労働制マスタ取得
		val masterData = workingConditionItemRepository.getBySidAndPeriodOrderByStrDWithDatePeriod(integraListByRecordAndEmpId,maxGeneralDate,minGeneralDate);
		List<IntegrationOfDaily> returnList = new ArrayList<>();
		for(IntegrationOfDaily record : integrationList) {

			//nowIntegrationの労働制取得
			Optional<Entry<DateHistoryItem, WorkingConditionItem>> nowWorkingItem = masterData.getItemAtDateAndEmpId(record.getYmd(),record.getEmployeeId());
			if(nowWorkingItem.isPresent()) {
				Optional<ManagePerPersonDailySet> personSetting = factoryManagePerPersonDailySet.create(companyId.v(), companyCommonSetting, record, nowWorkingItem.get().getValue());
				if(!personSetting.isPresent())
					continue;
				
				returnList.add(calculationErrorCheckService.errorCheck(record, 
																	personSetting.get(),
																	companyCommonSetting));
			}
			else {
				returnList.add(record);
			}
		}
		return returnList;
	}
	
//	/**
//	 * 共有コンテナを使った勤務種類の取得
//	 * 
//	 * @param shareContainer
//	 * @param WorkTypeCode
//	 * @param companyId
//	 * @return
//	 */
//	private Optional<WorkType> getWorkTypeFromShareContainer(MasterShareContainer<String> shareContainer,
//			String companyId, String WorkTypeCode) {
//		// val x = shareContainer.getShared("WorkType" + WorkTypeCode);
//		val workType = shareContainer.getShared("WorkType" + WorkTypeCode,
//				() -> workTypeRepository.findNoAbolishByPK(companyId, WorkTypeCode));
//		if (workType.isPresent()) {
//			return Optional.of(workType.get().clone());
//		}
//		return Optional.empty();
//	}
//	
//	/**
//	 * 就業時間帯コードの取得 勤務情報 > 労働条件 > 就業時間帯無と判定
//	 * 
//	 * @param workInfo
//	 * @param personCommonSetting
//	 * @param workType
//	 * @return
//	 */
//	private Optional<WorkTimeCode> decisionWorkTimeCode(WorkInfoOfDailyPerformance workInfo,
//			ManagePerPersonDailySet personCommonSetting, Optional<WorkType> workType) {
//
//		if (workInfo == null || workInfo.getRecordInfo() == null
//				|| workInfo.getRecordInfo().getWorkTimeCode() == null) {
//			if (personCommonSetting.getPersonInfo().isPresent()) {
//				return personCommonSetting.getPersonInfo().get().getWorkCategory().getWeekdayTime().getWorkTimeCode();
//			}
//			return Optional.empty();
//		}
//		return Optional.of(workInfo.getRecordInfo().getWorkTimeCode());
//	}
//
//	private Optional<PredetermineTimeSetForCalc> getPredByPersonInfo(Optional<WorkTimeCode> workTimeCode,
//			MasterShareContainer<String> shareContainer) {
//		if (!workTimeCode.isPresent())
//			return Optional.empty();
//		// val predSetting =
//		// predetemineTimeSetRepository.findByWorkTimeCode(AppContexts.user().companyId(),
//		// workTimeCode.get().toString());
//		val predSetting = getPredetermineTimeSetFromShareContainer(shareContainer, AppContexts.user().companyId(),
//				workTimeCode.get().toString());
//		if (!predSetting.isPresent())
//			return Optional.empty();
//		return Optional.of(PredetermineTimeSetForCalc.convertFromAggregatePremiumTime(predSetting.get()));
//
//	}
//	
//	/**
//	 * 共有コンテナを使った所定時間帯設定の取得
//	 * 
//	 * @param shareContainer
//	 * @param companyId
//	 * @param workTimeCode
//	 * @return
//	 */
//	private Optional<PredetemineTimeSetting> getPredetermineTimeSetFromShareContainer(
//			MasterShareContainer<String> shareContainer, String companyId, String workTimeCode) {
//		val predSet = shareContainer.getShared("PredetemineSet" + workTimeCode,
//				() -> predetemineTimeSetRepository.findByWorkTimeCode(companyId, workTimeCode));
//		if (predSet.isPresent()) {
//			return Optional.of(predSet.get().clone());
//		}
//		return Optional.empty();
//	}
//	
//	/**
//	 * @param map 各加算設定
//	 * @param workingSystem 労働制
//	 * @return 加算設定
//	 */
//	private AddSetting getAddSetting(String companyID, Map<String, AggregateRoot> map, WorkingSystem workingSystem) {
//		
//		switch(workingSystem) {
//		case REGULAR_WORK:
//			AggregateRoot workRegularAdditionSet = map.get("regularWork");
//			return workRegularAdditionSet != null
//					?(WorkRegularAdditionSet) workRegularAdditionSet
//					: new WorkRegularAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
//		
//		case FLEX_TIME_WORK:
//			AggregateRoot workFlexAdditionSet = map.get("flexWork");
//			return workFlexAdditionSet != null
//					?(WorkFlexAdditionSet) workFlexAdditionSet
//					: new WorkFlexAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
//			
//		case VARIABLE_WORKING_TIME_WORK:
//			AggregateRoot workDeformedLaborAdditionSet = map.get("irregularWork");
//			return workDeformedLaborAdditionSet != null
//					? (WorkDeformedLaborAdditionSet) workDeformedLaborAdditionSet
//					: new WorkDeformedLaborAdditionSet(companyID, HolidayCalcMethodSet.emptyHolidayCalcMethodSet());
//		
//		default:
//			throw new RuntimeException("unknown WorkingSystem");
//		}
//	}
}
