package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.algorithm;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.*;
import org.apache.logging.log4j.util.Strings;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.adapter.workrule.closure.ClosureAdapter;
import nts.uk.ctx.at.record.dom.adapter.workrule.closure.PresentClosingPeriodImport;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforAdapter;
import nts.uk.ctx.at.record.dom.adapter.workschedule.WorkScheduleWorkInforImport;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.GetAnnAndRsvRemNumWithinPeriod;
import nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param.AggrResultOfAnnAndRsvLeave;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CheckedCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareRange;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareSingleValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.CompareOperatorText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConvertCompareTypeToText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.StatusOfEmployeeAdapterAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceIdAndPeriodImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.WorkplaceDailyCheckConditionTime;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.DayCheckCond;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.ExtractionCondScheduleYear;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.ExtractionCondScheduleYearRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.ScheYearCheckService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.TimeCheckCond;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.annual.YearCheckItemType;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.TypeOfDays;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.algorithm.CalculateVacationDayService;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.monthly.algorithm.CheckScheTimeAndTotalWorkingService;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm.CompareValueRangeChecking;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.AttendanceItemNameAdapter;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckType;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ExtractionAlarmPeriodDate;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
public class ScheYearCheckServiceImpl implements ScheYearCheckService {
	@Inject
	private AttendanceItemNameAdapter attendanceItemNameAdapter;
	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;
	@Inject
	private WorkScheduleWorkInforAdapter workScheduleWorkInforAdapter;
	@Inject
	private ExtractionCondScheduleYearRepository condScheduleYearRepository;
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;
	@Inject
	private WorkingConditionRepository workingConditionRepository;
	@Inject
    private AttendanceTimeOfMonthlyRepository attendanceTimeOfMonthlyRepo;
	@Inject
	private ManagedParallelWithContext parallelManager;
	@Inject
	private RecordDomRequireService requireService;
	@Inject
	private ClosureAdapter closureAdapter;
	@Inject
	private CalculateVacationDayService calculateVacationDayService;
	@Inject
	private WorkTypeRepository workTypeRep;
	@Inject
	private CheckScheTimeAndTotalWorkingService scheTimeAndTotalWorkingService;
	@Inject
	private ConvertCompareTypeToText convertComparaToText;
	@SuppressWarnings("rawtypes")
	@Inject
	private CompareValueRangeChecking compareValueRangeChecking;
	
	@Override
	public void extractScheYearCheck(String cid, List<String> lstSid, DatePeriod dPeriod, String errorCheckId,
			String listOptionalItem, List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<StatusOfEmployeeAdapterAl> lstStatusEmp, List<ResultOfEachCondition> lstResultCondition,
			List<AlarmListCheckInfor> lstCheckType, Consumer<Integer> counter, Supplier<Boolean> shouldStop,
			List<AlarmEmployeeList> alarmEmployeeList, List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		String contractCode = AppContexts.user().contractCode();
		ScheYearPrepareData prepareData = prepareDataBeforeChecking(contractCode, cid, lstSid, dPeriod, errorCheckId, listOptionalItem);
		
		parallelManager.forEach(CollectionUtil.partitionBySize(lstSid, 100), emps -> {
			synchronized (this) {
				if (shouldStop.get()) {
					return;
				}
			}
									
			// 特定属性の項目の予定を作成する
			this.extractCondition(
					cid, lstSid, dPeriod, prepareData, getWplByListSidAndPeriod,
					alarmEmployeeList, alarmExtractConditions, alarmCheckConditionCode);
					
			synchronized (this) {
				counter.accept(emps.size());
			}
		});
	}
	
	/**
	 * チェックする前にデータ準備
	 */
	private ScheYearPrepareData prepareDataBeforeChecking(String contractCode, String cid, List<String> lstSid, DatePeriod dPeriod, 
			String errorDailyCheckId, String listOptionalItem) {
		YearMonthPeriod ym = new YearMonthPeriod(dPeriod.start().yearMonth(), dPeriod.end().yearMonth());
		
		// <<Public>> 勤務種類をすべて取得する
		List<WorkType> listWorkType = workTypeRep.findByCompanyId(cid);
				
		// 1, スケジュール年間の勤怠項目を取得する
		Map<Integer, String> attendanceItems = getScheYearAttendanceItems(cid, 0);
		
		// 2, 社員ID(List)、期間を設定して勤務予定を取得する
		List<WorkScheduleWorkInforImport> workScheduleWorkInfos = workScheduleWorkInforAdapter.getBy(lstSid, dPeriod);
		
		// 3, 社員と期間を条件に日別実績を取得する
		List<IntegrationOfDaily> empDailyPerformance = getEmpDailyPerformance(lstSid, dPeriod);
		
		// 4, 社員ID（List）、期間を設定して月別実績を取得する
		List<AttendanceTimeOfMonthly> attendanceTimeOfMonthlies = attendanceTimeOfMonthlyRepo.findBySidsAndYearMonths(lstSid, ym.yearMonthsBetween());
		
		// 5, スケジュール年間の任意抽出条件のデータを取得する
		List<ExtractionCondScheduleYear> condScheYears = getCondScheYear(contractCode, cid, listOptionalItem);
		
		List<TypeOfDays> typeOfDaysSelected = new ArrayList<>();
		typeOfDaysSelected.add(TypeOfDays.WORKING_DAY_NUMBER);
		typeOfDaysSelected.add(TypeOfDays.HOLIDAY_NUMBER);
		typeOfDaysSelected.add(TypeOfDays.DAYOFF_NUMBER);
		typeOfDaysSelected.add(TypeOfDays.SPECIAL_HOLIDAY_NUMBER);
		typeOfDaysSelected.add(TypeOfDays.ABSENTEEDAY_NUMBER);
		typeOfDaysSelected.add(TypeOfDays.SCHE_WORKINGD_AND_WORKINGD);
		
		Map<String, Map<DatePeriod, WorkingConditionItem>> empWorkingCondItem = new HashMap<>();
		
		boolean existItemDay = condScheYears.stream().anyMatch(x -> x.getCheckItemType() == YearCheckItemType.DAY_NUMBER
				&& typeOfDaysSelected.contains(((DayCheckCond)x.getScheCheckConditions()).getTypeOfDays()));
		
		// 6, データをチェック
		if (existItemDay) {
			// 7, 社員ID（List）と期間から労働条件を取得する
			List<WorkingCondition> listWorkingCondition = workingConditionRepository
					.getBySidsAndDatePeriod(lstSid, dPeriod);
			
			// 8, ドメインモデル「労働条件項目」を取得
			List<WorkingConditionItem> workingConditionItems = workingConditionItemRepository
					.getBySidsAndDatePeriodNew(lstSid, dPeriod);
			
			Map<String, WorkingConditionItem> mapWorkingCondtionItem = workingConditionItems.stream()
					.collect(Collectors.toMap(WorkingConditionItem::getHistoryId, x -> x));
			
			// 9, 労働条件を作成
			listWorkingCondition.forEach(x -> {
				Map<DatePeriod, WorkingConditionItem> mapWorkingCondItems = new HashMap<>();
				x.getDateHistoryItem().forEach(y -> {
					WorkingConditionItem workingConditionItem = mapWorkingCondtionItem.get(y.identifier());
					mapWorkingCondItems.put(y.span(), workingConditionItem);
				});
				empWorkingCondItem.put(x.getEmployeeId(), mapWorkingCondItems);
			});
		}
		
		return ScheYearPrepareData.builder()
				.workScheduleWorkInfos(workScheduleWorkInfos)
				.attendanceItems(attendanceItems)
				.attendanceTimeOfMonthlies(attendanceTimeOfMonthlies)
				.listIntegrationDai(empDailyPerformance)
				.scheCondItems(condScheYears)
				.empWorkingCondItem(empWorkingCondItem)
				.listWorkType(listWorkType)
				.build();
	}
	
	/**
	 * 1
	 * @param cid 会社ID　＝　Input．会社ID
	 * @param typeOfAttendanceItem 勤務項目の種類　＝　スケジュール
	 * @return List<勤怠項目コード、勤怠項目名称＞
	 */
	private Map<Integer, String> getScheYearAttendanceItems(String cid, int typeOfAttendanceItem) {
		return attendanceItemNameAdapter.getAttendanceItemNameAsMapName(cid, typeOfAttendanceItem);
	}
	
	/**
	 * 3
	 * @param lstSid List<社員ID> 
	 * @param dPeriod 期間．開始日　＝　Input．期間．開始月の開始日 & 期間．終了日　＝　Input．期間．終了月の終了日
	 * @return 日別勤怠：日別勤怠(Work)
	 */
	private List<IntegrationOfDaily> getEmpDailyPerformance(List<String> lstSid, DatePeriod dPeriod) {
		return dailyRecordShareFinder.findByListEmployeeId(lstSid, dPeriod);
	}
	
	/**
	 * 5
	 * @param cid Input．会社ID
	 * @param checkOptionalId Input．List＜スケジュール年間のエラーアラームチェックID＞
	 * @return List＜スケジュール年間のエラーアラーム＞
	 */
	private List<ExtractionCondScheduleYear> getCondScheYear(String contractCode, String cid, String checkOptionalId) {
		return condScheduleYearRepository.getScheAnyCond(contractCode, cid, checkOptionalId).stream()
				.filter(x -> x.isUse()).collect(Collectors.toList());
	}
	
	/**
	 * Extract condition Tab2
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void extractCondition(
			String cid, List<String> listSid, DatePeriod dPeriod, ScheYearPrepareData prepareData,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		
		for (ExtractionCondScheduleYear condScheYear: prepareData.getScheCondItems()) {
			val lstExtractCon = alarmExtractConditions.stream()
					.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FreeCheck && x.getAlarmCheckConditionNo().equals(String.valueOf(condScheYear.getSortOrder())))
					.findAny();
			if (!lstExtractCon.isPresent()) {
				alarmExtractConditions.add(new AlarmExtractionCondition(
						String.valueOf(condScheYear.getSortOrder()),
						new AlarmCheckConditionCode(alarmCheckConditionCode),
						AlarmCategory.SCHEDULE_YEAR,
						AlarmListCheckType.FreeCheck
				));
			}

			for (String sid: listSid) {
				// 総取得結果＝0
				Double totalTime = 0.0;
				
				Closure cloure = getClosure(cid, sid);
				PresentClosingPeriodImport presentClosingPeriod = getPresentClosingPeriod(cid, sid, cloure);
				
				//スケジュール年間の任意抽出条件．チェック項目の種類　＝　日数
				//AND
				//スケジュール年間の任意抽出条件．スケジュール年間チェック条件　！＝　3，6，7
				if (YearCheckItemType.DAY_NUMBER == condScheYear.getCheckItemType()) {
					DayCheckCond dayCheckCond = (DayCheckCond)condScheYear.getScheCheckConditions();
					if (dayCheckCond.getTypeOfDays() == TypeOfDays.PUBLIC_HOLIDAY_NUMBER
							|| dayCheckCond.getTypeOfDays() == TypeOfDays.ANNUAL_LEAVE_NUMBER
							|| dayCheckCond.getTypeOfDays() == TypeOfDays.ACC_ANNUAL_LEAVE_NUMBER) {
						cloure = null;
						presentClosingPeriod = null;
					}
				}
				
				String checkCondTypeName = Strings.EMPTY;
				List<YearMonth> listDate = dPeriod.yearMonthsBetween();
				for(YearMonth ym: listDate) {
					
					// 日別勤怠を探す
					//条件：
					//　・社員ID　＝　ループ中の社員ID
					//　・ループ中の年月の開始日＜＝年月日＜＝ループ中の年月の終了日
					//【Output】
					//　・List＜日別勤怠＞
					List<IntegrationOfDaily> lstDaily = prepareData.getListIntegrationDai().stream()
							.filter(x -> x.getEmployeeId().equals(sid) 
									&& x.getYmd().afterOrEquals(ym.firstGeneralDate()) && x.getYmd().beforeOrEquals(ym.lastGeneralDate()))
							.collect(Collectors.toList());
					
					// 月別実績を探す
					//条件：
					//　・社員ID　＝　ループ中の社員ID
					//　・年月　＝　ループ中の年月
					//【Output】
					//　・月別実績
					Optional<AttendanceTimeOfMonthly> lstMonthly = prepareData.getAttendanceTimeOfMonthlies().stream()
							.filter(x -> x.getEmployeeId().equals(sid) && x.getYearMonth().equals(ym))
							.findFirst();
					AttendanceTimeOfMonthly attendanceTimeOfMonthly = null;
					if(lstMonthly.isPresent()) {
						attendanceTimeOfMonthly = lstMonthly.get();
					}
					
					// 勤務予定を探す
					//条件：
					//　・社員ID　＝　ループ中の社員ID
					//　・ループ中の年月の開始日＜＝年月日＜＝ループ中の年月の終了日
					//【Output】
					//　・List＜勤務予定＞
					List<WorkScheduleWorkInforImport> workScheduleWorkInfosOpt = prepareData.getWorkScheduleWorkInfos().stream()
							.filter(x -> x.getEmployeeId().equals(sid) 
									&& x.getYmd().afterOrEquals(ym.firstGeneralDate()) && x.getYmd().beforeOrEquals(ym.lastGeneralDate()))
							.collect(Collectors.toList());
					
					// チェック項目をチェック
					switch (condScheYear.getCheckItemType()) {
					case TIME:
						TimeCheckCond dayCheckCond = (TimeCheckCond) condScheYear.getScheCheckConditions();
						checkCondTypeName = dayCheckCond.getTypeOfTime().nameId;
						// 総取得結果　+＝　取得結果
						totalTime += checkItemTime(
								cid, sid, ym, condScheYear, attendanceTimeOfMonthly, 
								prepareData, presentClosingPeriod, lstDaily, workScheduleWorkInfosOpt);
						break;
					case DAY_NUMBER:
						DayCheckCond timeCheckCond = (DayCheckCond) condScheYear.getScheCheckConditions();
						checkCondTypeName = timeCheckCond.getTypeOfDays().nameId;
						// 総取得結果　+＝　取得結果
						totalTime += checkItemDay(
								cid, sid, ym, condScheYear, attendanceTimeOfMonthly, 
								prepareData, presentClosingPeriod, lstDaily, workScheduleWorkInfosOpt);
						break;
					default:
						break;
					}
				}
				
				// 条件をチェックする
				boolean checkValue;
				if (condScheYear.getCheckConditions() instanceof CompareRange) {
					checkValue = compareValueRangeChecking.checkCompareRange((CompareRange)condScheYear.getCheckConditions(), totalTime);
				} else {
					checkValue = compareValueRangeChecking.checkCompareSingleRange((CompareSingleValue)condScheYear.getCheckConditions(), totalTime);
				}
				
				if (!checkValue) {
					continue;
				}
				
				// ・職場ID　＝　Input．List＜職場ID＞をループ中の年月日から探す
				String wplId = "";
				Optional<WorkPlaceHistImportAl> optWorkPlaceHistImportAl = getWplByListSidAndPeriod.stream().filter(x -> x.getEmployeeId().equals(sid)).findFirst();
				if(optWorkPlaceHistImportAl.isPresent()) {
					Optional<WorkPlaceIdAndPeriodImportAl> optWorkPlaceIdAndPeriodImportAl = optWorkPlaceHistImportAl.get().getLstWkpIdAndPeriod().stream()
							.filter(x -> x.getDatePeriod().start().beforeOrEquals(dPeriod.start()) 
									&& x.getDatePeriod().end().afterOrEquals(dPeriod.end())).findFirst();
					if(optWorkPlaceIdAndPeriodImportAl.isPresent()) {
						wplId = optWorkPlaceIdAndPeriodImportAl.get().getWorkplaceId();
					}
				}
				
				// 抽出結果詳細を作成
				String alarmCode = String.valueOf(condScheYear.getSortOrder());
				String alarmContent = getAlarmContent(
						getCompareOperatorText(condScheYear.getCheckItemType(), condScheYear.getCheckConditions(), checkCondTypeName), 
						totalTime, condScheYear.getCheckItemType());
				Optional<String> comment = condScheYear.getErrorAlarmMessage().isPresent()
						? Optional.of(condScheYear.getErrorAlarmMessage().get().v())
						: Optional.empty();
				// QA#116824
				ExtractionAlarmPeriodDate extractionAlarmPeriodDate = new ExtractionAlarmPeriodDate(Optional.of(dPeriod.start()), Optional.of(dPeriod.end()));
				
				ExtractResultDetail detail = new ExtractResultDetail(
						extractionAlarmPeriodDate, 
						condScheYear.getName().v(), 
						alarmContent, 
						GeneralDateTime.now(), 
						Optional.ofNullable(wplId), 
						comment, 
						Optional.ofNullable(getCheckValue(totalTime, condScheYear.getCheckItemType())));

				List<AlarmExtractInfoResult> lstExtractInfoResult = new ArrayList<>(Arrays.asList(
						new AlarmExtractInfoResult(
								alarmCode,
								new AlarmCheckConditionCode(alarmCheckConditionCode),
								AlarmCategory.SCHEDULE_YEAR,
								AlarmListCheckType.FreeCheck,
								new ArrayList<>(Arrays.asList(detail))
						)
				));

				if (alarmEmployeeList.stream().anyMatch(i -> i.getEmployeeID().equals(sid))) {
					for (AlarmEmployeeList i : alarmEmployeeList) {
						if (i.getEmployeeID().equals(sid)) {
							List<AlarmExtractInfoResult> temp = new ArrayList<>(i.getAlarmExtractInfoResults());
							temp.addAll(lstExtractInfoResult);
							i.setAlarmExtractInfoResults(temp);
							break;
						}
					}
				} else {
					alarmEmployeeList.add(new AlarmEmployeeList(lstExtractInfoResult, sid));
				}
			}
		}
	}
	
	/**
	 * アラーム内容
	 * @return アラーム内容
	 */
	private String getAlarmContent(String checkCondTypeName, Double totalTime, YearCheckItemType yearCheckItemType) {
		String param0 = checkCondTypeName;
		String param1 = YearCheckItemType.TIME == yearCheckItemType ? formatTime(totalTime.intValue()) : String.valueOf(totalTime);
		return TextResource.localize("KAL010_1203", param0, param1);
	}
	
	/**
	 * チェック対象値
	 * @return チェック対象値
	 */
	private String getCheckValue(Double totalTime, YearCheckItemType yearCheckItemType) {
		String param = YearCheckItemType.TIME == yearCheckItemType ? formatTime(totalTime.intValue()) : String.valueOf(totalTime);
		return TextResource.localize("KAL010_1204", param);
	}
	
	/**
	 * Get parameter 0 for alarm content 
	 */
	@SuppressWarnings("rawtypes")
	public String getCompareOperatorText(YearCheckItemType checkItemType, CheckedCondition checkCondition, String checkCondTypeName) {
		if (checkCondition == null) {
			return checkCondTypeName;		
		}
		
		int compare = checkCondition instanceof CompareSingleValue 
				? ((CompareSingleValue) checkCondition).getCompareOpertor().value
				: ((CompareRange) checkCondition).getCompareOperator().value;
				
		CompareOperatorText compareOperatorText = convertComparaToText.convertCompareType(compare);
		
		String startValue = checkCondition instanceof CompareSingleValue 
						? ((CompareSingleValue) checkCondition).getValue().toString()
						: ((CompareRange) checkCondition).getStartValue().toString();
		String endValue = checkCondition instanceof CompareRange  
				? ((CompareRange) checkCondition).getEndValue().toString() : null;
		
		if (YearCheckItemType.TIME == checkItemType) {
			WorkplaceDailyCheckConditionTime formatStartValue = new WorkplaceDailyCheckConditionTime(Double.valueOf(startValue).intValue());
			startValue = formatStartValue.getTimeWithFormat();
			
			if (endValue != null) {
				WorkplaceDailyCheckConditionTime formatEndValue = new WorkplaceDailyCheckConditionTime(Double.valueOf(endValue).intValue());
				endValue = formatEndValue.getTimeWithFormat();
			}
		}
				
		String variable0 = "";
		if(compare <= 5) {
			variable0 = checkCondTypeName + compareOperatorText.getCompareLeft() + startValue;
		} else {
			if (compare == 6 || compare == 7) {
				variable0 = startValue + compareOperatorText.getCompareLeft() + checkCondTypeName
						+ compareOperatorText.getCompareright() + endValue;
			} else {
				variable0 = checkCondTypeName + compareOperatorText.getCompareLeft() + startValue
						+ ", " + checkCondTypeName +  compareOperatorText.getCompareright() + endValue;
			}
		}
		
		return variable0;
	}
	
	/**
	 * 日数チェック条件をチェック
	 */
	private Double checkItemDay(
			String cid, String sid, YearMonth ym, 
			ExtractionCondScheduleYear condScheYear,
			AttendanceTimeOfMonthly attendanceTimeOfMonthly,
			ScheYearPrepareData prepareData,
			PresentClosingPeriodImport presentClosingPeriod,
			List<IntegrationOfDaily> lstDaily,
			List<WorkScheduleWorkInforImport> lstWorkSchedule) {
		Double totalTime = 0.0;
		
		DayCheckCond dayCheckCond = (DayCheckCond)condScheYear.getScheCheckConditions();
		switch (dayCheckCond.getTypeOfDays()) {
		case PUBLIC_HOLIDAY_NUMBER:
			// 期間内の公休残数を集計する
			//TODO RQ718 not implement QA#113101
			
			// 休暇日数を計算
			// 休暇日数　＝　合計（取得した公休の集計結果．公休情報．公休消化情報．取得数）
			totalTime = 0.0;
			
			break;
		case ANNUAL_LEAVE_NUMBER:
		case ACC_ANNUAL_LEAVE_NUMBER:
			// 期間中の年休積休残数を取得
			val require = requireService.createRequire();
			val cacheCarrier = new CacheCarrier();
			GeneralDate criteriaDate = GeneralDate.today();
			DatePeriod period = new DatePeriod(ym.firstGeneralDate(), ym.lastGeneralDate());
			AggrResultOfAnnAndRsvLeave aggResult = GetAnnAndRsvRemNumWithinPeriod.algorithm(require, cacheCarrier,
                    cid, sid, period, InterimRemainMngMode.OTHER, criteriaDate,
                    false, false, Optional.of(false),
                    Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
			
			// 休暇日数を計算
			if (dayCheckCond.getTypeOfDays() == TypeOfDays.ANNUAL_LEAVE_NUMBER) {
				if (aggResult.getAnnualLeave().isPresent()) {
					// 休暇日数　＝　取得した年休積休の集計結果．年休．年休情報(期間終了日時点)．使用日数
					totalTime = aggResult.getAnnualLeave().get().getAsOfPeriodEnd().getUsedDays().v().doubleValue();
				}
			} else {
				if (aggResult.getReserveLeave().isPresent()) {
					// 休暇日数　＝　取得した年休積休の集計結果．積立年休．積立年休情報(期間終了日時点)．使用日数
					totalTime = aggResult.getReserveLeave().get().getAsOfPeriodEnd().getUsedDays().v().doubleValue();
				}
			}
			break;
		default:
			// 出勤日数を計算
			TypeOfDays typeOfDay = ((DayCheckCond)condScheYear.getScheCheckConditions()).getTypeOfDays();
			totalTime = calculateVacationDayService.calVacationDay(
					cid, sid, ym, 
					presentClosingPeriod, 
					attendanceTimeOfMonthly, 
					lstDaily, lstWorkSchedule, 
					typeOfDay,
					prepareData.getListWorkType(),
					prepareData.getEmpWorkingCondItem().get(sid));
			break;
		}
		
		return totalTime;
	}
	
	/**
	 * 時間チェック条件をチェック
	 */
	private Double checkItemTime(
			String cid, String sid, YearMonth ym, 
			ExtractionCondScheduleYear condScheYear,
			AttendanceTimeOfMonthly attendanceTimeOfMonthly,
			ScheYearPrepareData prepareData,
			PresentClosingPeriodImport presentClosingPeriod,
			List<IntegrationOfDaily> lstDaily, List<WorkScheduleWorkInforImport> lstWorkSchedule) {
		// 予定時間＋総労働時間をチェック
		int totalTime = scheTimeAndTotalWorkingService.getScheTimeAndTotalWorkingTime(
				ym, attendanceTimeOfMonthly, presentClosingPeriod, lstDaily, lstWorkSchedule);
		return Double.valueOf(String.valueOf(totalTime));
	}
	
	private Closure getClosure(String cid, String sid) {
		// 社員に対応する処理締めを取得する
		val require = requireService.createRequire();
        val cacheCarrier = new CacheCarrier();
        GeneralDate criteriaDate = GeneralDate.today();
		return ClosureService.getClosureDataByEmployee(require, cacheCarrier, sid, criteriaDate);
	}
	
	private PresentClosingPeriodImport getPresentClosingPeriod(String cid, String sid, Closure cloure) {	
		// 処理年月と締め期間を取得する
		Optional<PresentClosingPeriodImport> presentClosingPeriodOpt = closureAdapter.findByClosureId(cid, cloure.getClosureId().value);
		if (presentClosingPeriodOpt.isPresent()) {
			return presentClosingPeriodOpt.get();
		}
		return null;
	}
	
	/**
	 * Format time
	 * because not defined primitive value => function created!
	 * @param value integer value time
	 * @return format time HH:MM
	 */
	private String formatTime(int value) {
		int hours = Math.abs(value) / 60;
		int minute = Math.abs(value) % 60;
		
		return hours + ":" + (minute < 10 ? "0" + minute : minute); 
	}
}
