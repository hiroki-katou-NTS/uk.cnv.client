package nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.adapter.specificdatesetting.RecSpecificDateSettingAdapter;
import nts.uk.ctx.at.record.dom.adapter.workschedule.*;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmConstant;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CheckedCondition;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareRange;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.attendanceitem.CompareSingleValue;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.daily.algorithm.OutputCheckResult;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.CompareOperatorText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.enums.ConvertCompareTypeToText;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.StatusOfEmployeeAdapterAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceHistImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.mastercheck.algorithm.WorkPlaceIdAndPeriodImportAl;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue.CheckedTimeDuration;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.schedule.daily.*;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.ContinuousCount;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm.CalCountForConsecutivePeriodChecking;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm.CalCountForConsecutivePeriodOutput;
import nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm.CompareValueRangeChecking;
import nts.uk.ctx.at.shared.dom.adapter.attendanceitemname.AttendanceItemNameAdapter;
import nts.uk.ctx.at.shared.dom.adapter.specificdatesetting.RecSpecificDateSettingImport;
import nts.uk.ctx.at.shared.dom.alarmList.AlarmCategory;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckInfor;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.AlarmListCheckType;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ExtractionAlarmPeriodDate;
import nts.uk.ctx.at.shared.dom.alarmList.extractionResult.ResultOfEachCondition;
import nts.uk.ctx.at.shared.dom.alarmList.persistenceextractresult.*;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PrescribedTimezoneSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;
import org.apache.logging.log4j.util.Strings;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Stateless
public class ScheDailyCheckServiceImpl implements ScheDailyCheckService {
	@Inject
	private ManagedParallelWithContext parallelManager;
	@Inject
	private AttendanceItemNameAdapter attendanceItemNameAdapter;
	@Inject
	private WorkTypeRepository workTypeRep;
	@Inject
	private WorkTimeSettingRepository workTimeRep;
	@Inject
	private WorkScheduleWorkInforAdapter workScheduleWorkInforAdapter;
	@Inject 
	private ExtraCondScheDayRepository extraCondScheDayRepository;
	@Inject
	private FixedExtractSDailyConRepository fixedCondScheDayReporisoty;
	@Inject
	private FixedExtracSDailyItemsRepository fixedExtracSDailyItemsRepository;
	@Inject
	private RecSpecificDateSettingAdapter specificDateSettingAdapter;
	@Inject 
	private CalCountForConsecutivePeriodChecking calcCountForConsecutivePeriodChecking;
	@SuppressWarnings("rawtypes")
	@Inject
	private CompareValueRangeChecking compareValueRangeChecking;
	@Inject
	private ConvertCompareTypeToText convertComparaToText;
	@Inject
	private PredetemineTimeSettingRepository predTimeSetRepo;
	
	@Override
	public void extractScheDailyCheck(String cid, List<String> lstSid, DatePeriod dPeriod, String errorDailyCheckId,
			String listOptionalItem, String listFixedItem,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod, List<StatusOfEmployeeAdapterAl> lstStatusEmp,
			List<ResultOfEachCondition> lstResultCondition, List<AlarmListCheckInfor> lstCheckType,
			Consumer<Integer> counter, Supplier<Boolean> shouldStop, List<AlarmEmployeeList> alarmEmployeeList,
			List<AlarmExtractionCondition> alarmExtractConditions, String alarmCheckConditionCode) {
		String contractCode = AppContexts.user().contractCode();
		
		// チェックする前にデータ準備
		ScheDailyPrepareData prepareData = prepareDataBeforeChecking(contractCode, cid, lstSid, dPeriod, errorDailyCheckId, listOptionalItem, listFixedItem);
		
		parallelManager.forEach(CollectionUtil.partitionBySize(lstSid, 100), emps -> {

			synchronized (this) {
				if (shouldStop.get()) {
					return;
				}
			}
			
			// Input．List＜社員ID＞をループ
			for(String sid : lstSid) {
				List<AlarmExtractInfoResult> lstExtractInfoResult = new ArrayList<>();
				// 任意抽出条件のアラーム値を作成する
				OutputCheckResult checkTab2 = extractAlarmConditionTab2(
						sid, 
						prepareData.getWorkScheduleWorkInfos(), 
						prepareData.getScheCondItems(), 
						lstStatusEmp, 
						dPeriod, 
						getWplByListSidAndPeriod,
						prepareData.getListWorkType(),
						prepareData.getListIntegrationDai(),
						alarmCheckConditionCode,
						lstExtractInfoResult,
						alarmExtractConditions);

				// 固定抽出条件のアラーム値を作成する
				OutputCheckResult checkTab3 = extractAlarmConditionTab3(
						cid,
						sid,
						dPeriod,
						prepareData.getFixedScheCondItems(),
						prepareData.getFixedItems(),
						prepareData.getListWorkType(),
						prepareData.getListWorktime(),
						getWplByListSidAndPeriod,
						prepareData.getWorkScheduleWorkInfos(),
						lstStatusEmp,
						prepareData.getListIntegrationDai(),
						alarmCheckConditionCode,
						lstExtractInfoResult,
						alarmExtractConditions);

				if (!lstExtractInfoResult.isEmpty()) {
					if (alarmEmployeeList.stream().anyMatch(i -> i.getEmployeeID().equals(sid))) {
						alarmEmployeeList.forEach(i -> {
							if (i.getEmployeeID().equals(sid)) {
								List<AlarmExtractInfoResult> temp = new ArrayList<>(i.getAlarmExtractInfoResults());
								temp.addAll(lstExtractInfoResult);
								i.setAlarmExtractInfoResults(temp);
							}
						});
					} else {
						alarmEmployeeList.add(new AlarmEmployeeList(lstExtractInfoResult, sid));
					}
				}
			}
			
			synchronized (this) {
				counter.accept(emps.size());
			}
		});
	}
	
	/**
	 * チェックする前にデータ準備
	 * @param cid
	 * @param lstSid
	 * @param dPeriod
	 * @param errorDailyCheckId
	 * @param extractConditionWorkRecord
	 * @param errorDailyCheckCd
	 * @return
	 */
	private ScheDailyPrepareData prepareDataBeforeChecking(String contractCode, String cid, List<String> lstSid, DatePeriod dPeriod, 
			String errorDailyCheckId, String listOptionalItem, String listFixedItem) {
		// スケジュール日次の勤怠項目を取得する
		int typeOfItem = 0; // スケジュール
		Map<Integer, String> attendanceItems = attendanceItemNameAdapter.getAttendanceItemNameAsMapName(typeOfItem);
		
		// <<Public>> 勤務種類をすべて取得する
		List<WorkType> listWorkType = workTypeRep.findByCompanyId(cid);
		
		// 社員ID(List)、期間を設定して勤務予定を取得する
		List<WorkScheduleWorkInforImport> workScheduleWorkInfos = workScheduleWorkInforAdapter.getBy(lstSid, dPeriod);
		
		// 会社で使用できる就業時間帯を全件を取得する
		List<WorkTimeSetting> listWorktime = workTimeRep.findByCompanyId(cid);
		
		// スケジュール日次の任意抽出条件のデータを取得する
		List<ExtractionCondScheduleDay> scheCondItems = extraCondScheDayRepository.getScheAnyCondDay(contractCode, cid, listOptionalItem, true);
		
		// スケジュール日次の固定抽出条件のデータを取得する
		List<FixedExtractionSDailyCon> fixedScheCondItems = fixedCondScheDayReporisoty.getScheFixCondDay(contractCode, cid, listFixedItem, true);
		
		// ドメインモデル「スケジュール日次の固有抽出項目」を取得
		List<FixedExtractionSDailyItems> fixedItems = fixedExtracSDailyItemsRepository.getAll();
		
		//社員と期間を条件に日別実績を取得する
		//List<IntegrationOfDaily> listIntegrationDai = dailyRecordShareFinder.findByListEmployeeId(lstSid, dPeriod);
				
		return ScheDailyPrepareData.builder()
				.attendanceItems(attendanceItems)
				.listWorkType(listWorkType)
				.listWorktime(listWorktime)
				.workScheduleWorkInfos(workScheduleWorkInfos)
				.scheCondItems(scheCondItems)
				.fixedScheCondItems(fixedScheCondItems)
				.fixedItems(fixedItems)
				//.listIntegrationDai(listIntegrationDai)
				.build();
	}
	
	/**
	 * Tab2: チェック条件
	 */
	private OutputCheckResult extractAlarmConditionTab2(
			String sid, 
			List<WorkScheduleWorkInforImport> lstWorkScheduleWorkInfos, 
			List<ExtractionCondScheduleDay> scheCondItems, 
			List<StatusOfEmployeeAdapterAl> lstStatusEmp, 
			DatePeriod dPeriod,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			List<WorkType> listWorkType,
			List<IntegrationOfDaily> listIntegrationDai, String alarmCheckConditionCode,
			List<AlarmExtractInfoResult> lstExtractInfoResult, List<AlarmExtractionCondition> alarmExtractConditions) {
		OutputCheckResult result = new OutputCheckResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		
		// Input．List＜スケジュール日次の任意抽出条件＞をループ
		for (ExtractionCondScheduleDay scheCondItem: scheCondItems) {
			val extractionConditions = alarmExtractConditions.stream()
					.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FreeCheck && x.getAlarmCheckConditionNo().equals(String.valueOf(scheCondItem.getSortOrder())))
					.findAny();
			if (!extractionConditions.isPresent()) {
				alarmExtractConditions.add(new AlarmExtractionCondition(
						String.valueOf(scheCondItem.getSortOrder()),
						new AlarmCheckConditionCode(alarmCheckConditionCode),
						AlarmCategory.SCHEDULE_DAILY,
						AlarmListCheckType.FreeCheck
				));
			}

			// カウント＝0
			int count = 0;
			
			// Input．期間．開始日からループする
			List<GeneralDate> listDate = dPeriod.datesBetween();
			for(int day = 0; day < listDate.size(); day++) {
				GeneralDate exDate = listDate.get(day);
				// 社員の会社所属状況をチェック
				List<StatusOfEmployeeAdapterAl> statusOfEmp = lstStatusEmp.stream()
						.filter(x -> x.getEmployeeId().equals(sid) 
								&& !x.getListPeriod().stream()
									.filter(y -> y.start().beforeOrEquals(exDate) && y.end().afterOrEquals(exDate)).collect(Collectors.toList()).isEmpty())
						.collect(Collectors.toList());
				if (statusOfEmp.isEmpty()) {
					count = 0;
					continue;
				}
				
				// 勤務予定を探す
				Optional<WorkScheduleWorkInforImport> workScheduleWorks = lstWorkScheduleWorkInfos.stream()
						.filter(x -> x.getEmployeeId().equals(sid) && x.getYmd().equals(exDate)).findFirst();
				if (!workScheduleWorks.isPresent()) {
					count = 0;
					continue;
				}
				
				// 勤務予定
				WorkScheduleWorkInforImport workSched = workScheduleWorks.get();
				int continuousPeriod = 0;
				boolean applicableAtr = false;
				List<String> lstWorkTypeCode = new ArrayList<>();
				CheckedCondition checkedCondition = null;
				
				// 勤務種類の条件
				RangeToCheck targetWorkType = scheCondItem.getTargetWrkType();
				// 勤務種類コード
				WorkTypeCode workTypeCode = new WorkTypeCode(workSched.getWorkType());
				
				// ループ中のスケジュール日次の任意抽出条件．時間のチェック条件をチェック
				if (DaiCheckItemType.TIME == scheCondItem.getCheckItemType()) {
					CondTime condTime = (CondTime) scheCondItem.getScheduleCheckCond();
					checkedCondition = condTime.getCheckedCondition();
					lstWorkTypeCode = condTime.getWrkTypeCds();
					
					// 勤務種類でフィルタする
					applicableAtr = checkWorkType(lstWorkTypeCode, targetWorkType, workTypeCode);
					
					// 予定時間をチェック
					applicableAtr = checkTime(condTime.getCheckedCondition(), workSched);
				}
				
				if (DaiCheckItemType.CONTINUOUS_TIME == scheCondItem.getCheckItemType()) {
					CondContinuousTime condContinuousTime = (CondContinuousTime) scheCondItem.getScheduleCheckCond();
					checkedCondition = condContinuousTime.getCheckedCondition();
					lstWorkTypeCode = condContinuousTime.getWrkTypeCds();
					continuousPeriod = condContinuousTime.getPeriod().v();
					
					// 勤務種類でフィルタする
					applicableAtr = checkWorkType(lstWorkTypeCode, targetWorkType, workTypeCode);
					
					// 予定時間をチェック
					applicableAtr = checkTime(condContinuousTime.getCheckedCondition(), workSched);
				}
				
				// ループ中のスケジュール日次の任意抽出条件．連続時間帯の抽出条件をチェック
				if (DaiCheckItemType.CONTINUOUS_TIMEZONE == scheCondItem.getCheckItemType()) {
					// ・探した勤務予定．勤務予報．勤務情報．就業時間帯コード
					WorkTimeCode workTimeCode = new WorkTimeCode(workSched.getWorkTime());
					
					CondContinuousTimeZone condContinuousTimeZone = (CondContinuousTimeZone)scheCondItem.getScheduleCheckCond();
					lstWorkTypeCode = condContinuousTimeZone.getWrkTypeCds();
					
					// 勤務種類でフィルタする
					applicableAtr = checkWorkType(lstWorkTypeCode, targetWorkType, workTypeCode);
					
					continuousPeriod = condContinuousTimeZone.getPeriod().v();
					if (workTimeCode != null || !applicableAtr) {
						// 勤務予定の就業時間帯があるかチェック
						applicableAtr = checkContinuousTimeZone(workTimeCode, condContinuousTimeZone, scheCondItem.getTimeZoneTargetRange());
					}
				}
				
				if (DaiCheckItemType.CONTINUOUS_WORK == scheCondItem.getCheckItemType()) {
					CondContinuousWrkType condContinuousWrkType = (CondContinuousWrkType)scheCondItem.getScheduleCheckCond();
					lstWorkTypeCode = condContinuousWrkType.getWrkTypeCds();
					continuousPeriod = condContinuousWrkType.getPeriod().v();
					// 勤務種類でフィルタする
					applicableAtr = checkWorkType(condContinuousWrkType.getWrkTypeCds(), targetWorkType, workTypeCode);
				}
				
				CalCountForConsecutivePeriodOutput calCountForConsecutivePeriodOutput = new CalCountForConsecutivePeriodOutput(Optional.empty(), count);
				if (DaiCheckItemType.TIME != scheCondItem.getCheckItemType()) {
					// 連続期間のカウントを計算
					//【Input】
					//　・カウント　＝　取得したカウント　（最初は↑でセットしたカウント）
					//　・連続期間　＝　ループ中のスケジュール日次の任意抽出条件．連続期間
					//　・エラー発生区分　＝　取得した該当区分
					//【Output】
					//　・カウント
					//　・Optional<連続カウント＞
					boolean errorAtr = applicableAtr;
					calCountForConsecutivePeriodOutput = calcCountForConsecutivePeriodChecking.getContinuousCount(count, continuousPeriod, errorAtr, exDate);
					count = calCountForConsecutivePeriodOutput.getCount();
				}
				
				Optional<ContinuousCount> optContinuousCount = calCountForConsecutivePeriodOutput.getOptContinuousCount();
				
				//・該当区分　＝＝　True　AND　ループ中のスケジュール日次の任意抽出条件．チェック項目種類　＝＝　「時間」
				//OR
				//・ループ中のスケジュール日次の任意抽出条件．チェック項目種類　！＝　「時間」　AND　Optional<連続カウント>　！＝　Empty
				if ((applicableAtr && DaiCheckItemType.TIME == scheCondItem.getCheckItemType())
						|| (DaiCheckItemType.TIME != scheCondItem.getCheckItemType() && optContinuousCount.isPresent())) {
					String alarmContent = createAlarmContent(
							scheCondItem.getCheckItemType(), 
							targetWorkType,
							listWorkType, lstWorkTypeCode, 
							checkedCondition, 
							continuousPeriod, 
							optContinuousCount.isPresent() ? optContinuousCount.get().getConsecutiveYears() : 0,
							workSched);
					String checkValue = createCheckValue(
							scheCondItem.getCheckItemType(), 
							lstWorkTypeCode, 
							listWorkType, 
							optContinuousCount.isPresent() ? optContinuousCount.get().getConsecutiveYears() : 0,
							workSched);
					
					Optional<AlarmListCheckInfor> optCheckInfor = result.getLstCheckType().stream()
							.filter(x -> x.getChekType() == AlarmListCheckType.FreeCheck && x.getNo().equals(String.valueOf(scheCondItem.getSortOrder())))
							.findFirst();
					if(optCheckInfor.isPresent()) {
						result.getLstCheckType().add(new AlarmListCheckInfor(String.valueOf(scheCondItem.getSortOrder()), AlarmListCheckType.FreeCheck));
					}

					// スケジュール日次のアラーム抽出値を作成
					this.createExtractAlarm(sid,
							exDate,
							scheCondItem.getName().v(),
							alarmContent,
							Optional.ofNullable(scheCondItem.getErrorAlarmMessage() != null && scheCondItem.getErrorAlarmMessage().isPresent() ? scheCondItem.getErrorAlarmMessage().get().v() : ""),
							checkValue,
							String.valueOf(scheCondItem.getSortOrder()),
							AlarmListCheckType.FreeCheck,
							getWplByListSidAndPeriod,
							scheCondItem.getCheckItemType(),
							optContinuousCount.isPresent() ? optContinuousCount.get().getConsecutiveYears() : 0,
							alarmCheckConditionCode,
							lstExtractInfoResult);
				}
			}
			
			// 各チェック条件の結果を作成
			// - use value object: 各チェック条件の結果
		}
		
		// 作成したList＜各チェック条件の結果＞を返す
		return result;
	}
	
	/**
	 * create extract alarm
	 */
	private void createExtractAlarm(String sid,
			GeneralDate day,
			String alarmName,
			String alarmContent,
			Optional<String> alarmMess,
			String checkValue,
			String alarmCode, AlarmListCheckType checkType,
			List<WorkPlaceHistImportAl> getWplByListSidAndPeriod,
			DaiCheckItemType dailyCheckType,
			int consecutiveYears, String alarmCheckConditionCode,
			List<AlarmExtractInfoResult> alarmExtractInfoResults) {
		// ・職場ID　＝　Input．List＜職場ID＞をループ中の年月日から探す
		String wplId = "";
		Optional<WorkPlaceHistImportAl> optWorkPlaceHistImportAl = getWplByListSidAndPeriod.stream().filter(x -> x.getEmployeeId().equals(sid)).findFirst();
		if(optWorkPlaceHistImportAl.isPresent()) {
			Optional<WorkPlaceIdAndPeriodImportAl> optWorkPlaceIdAndPeriodImportAl = optWorkPlaceHistImportAl.get().getLstWkpIdAndPeriod().stream()
					.filter(x -> x.getDatePeriod().start()
							.beforeOrEquals(day) 
							&& x.getDatePeriod().end()
							.afterOrEquals(day)).findFirst();
			if(optWorkPlaceIdAndPeriodImportAl.isPresent()) {
				wplId = optWorkPlaceIdAndPeriodImportAl.get().getWorkplaceId();
			}
		}
		
		// アラーム値日付 =
		// チェック項目種類　＝＝　「時間」　－＞ループ中の年月日
		ExtractionAlarmPeriodDate extractionAlarmPeriodDate = new ExtractionAlarmPeriodDate(Optional.of(day), Optional.empty());
		if (dailyCheckType != null && DaiCheckItemType.TIME != dailyCheckType) {
			// チェック項目種類　！＝　「時間」　－＞ループ中の年月日.ADD(-取得した連続カウント）
			extractionAlarmPeriodDate = new ExtractionAlarmPeriodDate(Optional.of(day.addDays(-consecutiveYears)), Optional.empty());
		}
		
		// 抽出結果詳細
		ExtractResultDetail detail = new ExtractResultDetail(
				extractionAlarmPeriodDate, 
				alarmName, 
				alarmContent, 
				GeneralDateTime.now(), 
				Optional.ofNullable(wplId), 
				alarmMess, 
				Optional.ofNullable(checkValue));

		if (alarmExtractInfoResults.stream()
				.anyMatch(x -> x.getAlarmListCheckType().value == checkType.value
						&& x.getAlarmCheckConditionNo().equals(alarmCode)
						&& x.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode)
						&& x.getAlarmCategory().value == AlarmCategory.SCHEDULE_DAILY.value)) {
			alarmExtractInfoResults.forEach(x -> {
				if (x.getAlarmListCheckType().value == checkType.value
						&& x.getAlarmCheckConditionNo().equals(alarmCode)
						&& x.getAlarmCheckConditionCode().v().equals(alarmCheckConditionCode)
						&& x.getAlarmCategory().value == AlarmCategory.SCHEDULE_DAILY.value) {
					List<ExtractResultDetail> tmp = new ArrayList<>(x.getExtractionResultDetails());
					tmp.add(detail);
					x.setExtractionResultDetails(tmp);
				}
			});
		} else {
			List<ExtractResultDetail> listDetail = new ArrayList<>(Arrays.asList(detail));
			alarmExtractInfoResults.add(new AlarmExtractInfoResult(
					alarmCode,
					new AlarmCheckConditionCode(alarmCheckConditionCode),
					AlarmCategory.SCHEDULE_DAILY,
					checkType,
					listDetail
			));
		}
	}
	
	/**
	 * create alarm content (アラーム内容)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String createAlarmContent(
			DaiCheckItemType dailyCheckType,
			RangeToCheck targetWorkType,
			List<WorkType> listWorkType,
			List<String> lstWorkTypeCode,
			CheckedCondition checkedCondition,
			int conPeriod,
			int consecutiveYears,
			WorkScheduleWorkInforImport workSched) {
		String content = "";
		CompareOperatorText compareOperatorText = null;
		int compare = 0;
		String startValue = "";
		String endValue = "";
		String consecutiveYearStr = String.valueOf(consecutiveYears);
		String conPeriodStr = String.valueOf(conPeriod);
		
		if (checkedCondition != null) {
			compare = checkedCondition instanceof CompareSingleValue 
					? ((CompareSingleValue) checkedCondition).getCompareOpertor().value
					: ((CompareRange) checkedCondition).getCompareOperator().value;
					
			compareOperatorText = convertComparaToText.convertCompareType(compare);
			
			Double startVal = checkedCondition instanceof CompareSingleValue 
							? (Double)((CompareSingleValue) checkedCondition).getValue()
							: (Double)((CompareRange) checkedCondition).getStartValue();
			Double endVal = checkedCondition instanceof CompareRange  ? (Double)((CompareRange) checkedCondition).getEndValue() : null;
			
			// format value to HH:MM
			if (DaiCheckItemType.TIME == dailyCheckType || DaiCheckItemType.CONTINUOUS_TIME == dailyCheckType) {
				CheckedTimeDuration checkedTimeDurationStartValue = new CheckedTimeDuration(startVal.intValue());
				startValue = checkedTimeDurationStartValue.getTimeWithFormat();
				
				if (endVal != null) {
					CheckedTimeDuration checkedTimeDurationEndValue = new CheckedTimeDuration(endVal.intValue());
					endValue = checkedTimeDurationEndValue.getTimeWithFormat();
				}
			}
		}
		
		// チェック項目種類　＝＝　「時間」　OR　　「連続時間」 #KAL010_1013　※1
		if (DaiCheckItemType.TIME == dailyCheckType || DaiCheckItemType.CONTINUOUS_TIMEZONE == dailyCheckType) {
			// ※1内容:  対象勤務：{0}条件：予約時間{1}{2}　実績：{3}
			// {0}: ループ中のスケジュール日次の任意抽出条件．勤務種類の条件．予実比較による絞り込み方法
			String variable0 = targetWorkType.nameId;			
			
			// {1}: チェック条件　（例：　＜＞8：00）
			String checkCondTypeName = dailyCheckType.nameId;
			String variable1 = "";
			
			if (compareOperatorText != null) {
				if(compare <= 5) {
					variable1 = checkCondTypeName + compareOperatorText.getCompareLeft() + startValue;
				} else {
					if (compare == 6 || compare == 7) {
						variable1 = startValue + compareOperatorText.getCompareLeft() + checkCondTypeName 
								+ compareOperatorText.getCompareright() + endValue;
					} else {
						variable1 = checkCondTypeName + compareOperatorText.getCompareLeft() + startValue
								+ ", " + checkCondTypeName + compareOperatorText.getCompareright() + endValue;
					}
				}
			}
			
			// {2}: 
				// チェック項目種類　＝＝　「時間」　－＞""
				// チェック項目種類　！＝　「時間」  －＞ #KAL010_1015　（{0}: ループ中のスケジュール日次の任意抽出条件．連続期間）
			String variable2 = DaiCheckItemType.TIME != dailyCheckType ? TextResource.localize("KAL010_1015", conPeriodStr) : Strings.EMPTY;
			// {3}:
				// チェック項目種類　＝＝　「時間」　－＞探した勤務予定．勤怠時間．勤務時間．総労働時間
			String variable3 = "";
			if (DaiCheckItemType.TIME == dailyCheckType) {
				if (workSched != null && workSched.getOptAttendanceTime().isPresent()) {
					int actualTime = workSched.getOptAttendanceTime().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime();
					variable3 = formatTime(actualTime);
				}
			} else {
				// チェック項目種類　！＝　「時間」　－＞　取得した連続カウント　+　#KAL010_1017 
				variable3 = consecutiveYears + TextResource.localize("KAL010_1017");
			}
			// 例1：対象勤務：選択　条件：予約時間＜＞8：00　実績：10：00
			// 例２：対象勤務：選択　条件：予約時間＜＞8：00/5日連続　実績：10日連続
			content = TextResource.localize("KAL010_1013", variable0, variable1, variable2, variable3);
		} else { 
			// チェック項目種類　！＝　「時間」　OR　　「連続時間」－＞#KAL010_1016　※2
			
			// ※2内容：対象勤務：{0}/{1}日連続　実績：{2}日連続
			// {0}: ループ中のスケジュール日次の任意抽出条件．勤務種類の条件．予実比較による絞り込み方法
			String param0 = targetWorkType.nameId;
			
			// {1}: ループ中のスケジュール日次の任意抽出条件．連続期間
			String param1 = conPeriodStr;
			// {2}:　取得した連続カウント
			String param2 = consecutiveYearStr;
			// 例：対象勤務：選択/5日連続　実績：6日連続
			content = TextResource.localize("KAL010_1016", param0, param1, param2);
		}
		
		return content;		
	}
	
	/**
	 * create check value (チェック対象値)
	 * @return
	 */
	private String createCheckValue(DaiCheckItemType dailyCheckType, List<String> lstWorkTypeCode, List<WorkType> listWorkType, int consecutiveYear, WorkScheduleWorkInforImport workSched) {
		String consecutiveYearStr = String.valueOf(consecutiveYear);
		// チェック対象値 = 
			// チェック項目種類　==　時間　－＞#KAL010_1025
		if (DaiCheckItemType.TIME == dailyCheckType) {
			// {0} = 探した勤務予定．勤怠時間．勤務時間．総労働時間
			String param = "";
			if (workSched != null && workSched.getOptAttendanceTime().isPresent()) {
				int actualTime = workSched.getOptAttendanceTime().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime();
				param = formatTime(actualTime);
			}
			
			return TextResource.localize("KAL010_1025", param);
		}
		
		// チェック項目種類　！=　時間　－＞#KAL010_1026
		//　{0} = 取得した連続カウント
		String param = consecutiveYearStr;
		return TextResource.localize("KAL010_1026", param);
	}
	
	/**
	 * 勤務種類でフィルタする
	 * @return
	 */
	private boolean checkWorkType(List<String> workTypeSelected, RangeToCheck targetWrkType, WorkTypeCode workTypeCode) {
		if (workTypeSelected.isEmpty()) {
			return false;
		}
		
		// Input．勤務種類の条件．予実比較による絞り込み方法をチェック
		switch (targetWrkType) {
		case ALL:
			return true;
		case CHOICE:
			// 選択した勤務種類コードに存在するかないかをチェック
			return workTypeSelected.contains(workTypeCode.v());
		case OTHER:
			// 選択した勤務種類コードに存在するかないかをチェック
			return !workTypeSelected.contains(workTypeCode.v());
		default:
			break;
		}
		
		return false;
	}
	
	/**
	 * 予定時間をチェック
	 * @param -scheCondDay
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean checkTime(CheckedCondition checkedCondition, WorkScheduleWorkInforImport workSchedule) {
		if (checkedCondition == null) {
			return true;
		}
		
		// Input．勤務予定．勤怠時間をチェック
		Optional<AttendanceTimeOfDailyAttendanceImport> attendanceTimeOfDailyPerformance = workSchedule.getOptAttendanceTime();
		if (!attendanceTimeOfDailyPerformance.isPresent()) {
			return false;
		}
		
		AttendanceTimeOfDailyAttendanceImport attendanceTimeOfDaily = attendanceTimeOfDailyPerformance.get();
		Double targetValue = getTargetValue(attendanceTimeOfDaily.getActualWorkingTimeOfDaily().getTotalWorkingTime().getActualTime());
		
				
		// 集計値が条件に当てはまるかチェックする
		// 値を範囲と比較する
		if (checkedCondition instanceof CompareRange) {
			CompareRange compareRange = (CompareRange) checkedCondition;
			return compareValueRangeChecking.checkCompareRange(compareRange, targetValue);
		}
		
		// 値を単一値と比較する
		if (checkedCondition instanceof CompareSingleValue) {
			CompareSingleValue compareSingleValue = (CompareSingleValue) checkedCondition;
			
			return compareValueRangeChecking.checkCompareSingleRange(compareSingleValue, targetValue);
		}
		
		// 値を集計した値と比較する Remove this process beacause QA#115708 
		
		return false;
	}
	
	private Double getTargetValue(Integer target) {
		return Double.valueOf(target);
	}
	
	/**
	 * Case checktype = Continuous time zone
	 */
	private boolean checkContinuousTimeZone(WorkTimeCode workTimeCode, CondContinuousTimeZone condContinuousTimeZone, TimeZoneTargetRange timeZoneTargetRange) {
		// 勤務予定の就業時間帯があるかチェック
		if (condContinuousTimeZone.getWrkTimeCds().isEmpty()) {
			return false;
		}
		
		// 就業時間帯でフィルタする
		//【Input】
		//　・就業時間帯の条件　＝　ループ中のスケジュール日次の任意抽出条件．就業時間帯の条件
		//　・就業時間帯コード　＝　探した勤務予定．勤務予報．勤務情報．就業時間帯コード
		//【Output】
		//　・該当区分　（Default：False）
		return checkByWorkingTime(workTimeCode, condContinuousTimeZone, timeZoneTargetRange);
	}
	
	/**
	 * 就業時間帯でフィルタする
	 * @param condContinuousTimeZone
	 */
	private boolean checkByWorkingTime(WorkTimeCode workTimeCode, CondContinuousTimeZone condContinuousTimeZone, TimeZoneTargetRange timeZoneTargetRange) {
		if (condContinuousTimeZone.getWrkTimeCds().isEmpty()) {
			return false;
		}
		
		switch (timeZoneTargetRange) {
		case CHOICE:
			// Input．就業時間帯コードは対象の就業時間帯に存在するかチェック
			// 存在する
			return condContinuousTimeZone.getWrkTimeCds().contains(workTimeCode.v());
		case OTHER:
			// Input．就業時間帯コードは対象の就業時間帯に存在するかチェック
			// 存在しない
			return !condContinuousTimeZone.getWrkTimeCds().contains(workTimeCode.v());
		default:
			break;
		}
		
		return false;
	}
	
	/**
	 * Tab3: スケジュール日次の固有抽出条件
	 */
	private OutputCheckResult extractAlarmConditionTab3(
			String companyId,
			String sid,
			DatePeriod dPeriod,
			List<FixedExtractionSDailyCon> fixedScheCondItems,
			List<FixedExtractionSDailyItems> fixedItems,
			List<WorkType> listWorkType,
			List<WorkTimeSetting> listWorktime,
			List<WorkPlaceHistImportAl> wplByListSidAndPeriod,
			List<WorkScheduleWorkInforImport> workScheduleWorkInfos,
			List<StatusOfEmployeeAdapterAl> lstStatusEmp,
			List<IntegrationOfDaily> listIntegrationDai,
			String alarmCheckConditionCode,
			List<AlarmExtractInfoResult> alarmExtractInfoResults,
			List<AlarmExtractionCondition> alarmExtractConditions) {
		OutputCheckResult result = new OutputCheckResult(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		List<GeneralDate> listDate = dPeriod.datesBetween();
		for(int day = 0; day < listDate.size(); day++) {
			GeneralDate exDate = listDate.get(day);
			
			// ・職場ID　＝　Input．List＜職場ID＞をループ中の年月日から探す
			String wplId = getWorkplaceId(sid, exDate, wplByListSidAndPeriod);			
			
			// 社員の会社所属状況をチェック
			List<StatusOfEmployeeAdapterAl> statusOfEmp = lstStatusEmp.stream()
					.filter(x -> x.getEmployeeId().equals(sid) 
							&& !x.getListPeriod().stream()
								.filter(y -> y.start().beforeOrEquals(exDate) && y.end().afterOrEquals(exDate)).collect(Collectors.toList()).isEmpty())
					.collect(Collectors.toList());
			if(statusOfEmp.isEmpty()) continue;
			
			// 勤務予定(Work)を探す
			Optional<WorkScheduleWorkInforImport> workScheduleWorks = workScheduleWorkInfos.stream()
					.filter(x -> x.getEmployeeId().equals(sid) && x.getYmd().equals(exDate)).findFirst();			
			
			// Input．List＜スケジュール日次の固有抽出条件＞をループする
			for (FixedExtractionSDailyCon fixScheCondItem: fixedScheCondItems) {
				AlarmMsgOutput alarmMsgOutput = null;
				
				// ループ中のスケジュール日次の固有抽出条件をチェック
				FixedCheckSDailyItems fixedAtr = fixScheCondItem.getFixedCheckDayItems();

				val extractionCon = alarmExtractConditions.stream()
						.filter(x -> x.getAlarmListCheckType() == AlarmListCheckType.FixCheck && x.getAlarmCheckConditionNo().equals(String.valueOf(fixedAtr.value)))
						.findAny();
				if (!extractionCon.isPresent()) {
					alarmExtractConditions.add(new AlarmExtractionCondition(
							String.valueOf(fixedAtr.value),
							new AlarmCheckConditionCode(alarmCheckConditionCode),
							AlarmCategory.SCHEDULE_DAILY,
							AlarmListCheckType.FixCheck
					));
				}
				
				switch (fixedAtr) {
				case SCHEDULE_CREATE_NOTCREATE:
					// NO1 = ：スケジュール未作成
					// Input．勤務予定が存在するかチェックする
					if (!workScheduleWorks.isPresent()) {
						// 取得できない場合
						//・アラーム表示値　＝　#KAL010_1004
						//・チェック対象値　＝　#KAL010_1018
						String alarmMessage = TextResource.localize("KAL010_1004");
						String alarmTarget = TextResource.localize("KAL010_1018");
						alarmMsgOutput = new AlarmMsgOutput(alarmMessage, alarmTarget);
					}
					break;
				case WORKTYPE_NOTREGISTED:
					if (!workScheduleWorks.isPresent()) {
						break;
					}
					
					WorkScheduleWorkInforImport workSchedule = workScheduleWorks.get();
					
					// NO2 = ：勤務種類未登録
					// Input．日別勤怠の勤務情報が存在するかチェックする					
					String wkType = workSchedule.getWorkType();
					Optional<WorkType> listWk = listWorkType.stream()
							.filter(x -> x.getWorkTypeCode().v().equals(wkType)).findFirst();
					if(!listWk.isPresent()) {
						String alarmMessage = TextResource.localize("KAL010_7", wkType);
						String alarmTarget = TextResource.localize("KAL010_1020", wkType);
						alarmMsgOutput = new AlarmMsgOutput(alarmMessage, alarmTarget);
					}
					break;
				case WORKTIME_NOTREGISTED:
					if (!workScheduleWorks.isPresent()) {
						break;
					}
					WorkScheduleWorkInforImport workScheduleWorkTime = workScheduleWorks.get();
					
					// NO3 = ：就業時間帯未登録
					String wkTime = workScheduleWorkTime.getWorkTime();
					if(wkTime == null) break;
					Optional<WorkTimeSetting> optWtime = listWorktime.stream()
							.filter(x -> x.getWorktimeCode().v().equals(wkTime)).findFirst();
					if(!optWtime.isPresent()) {
						String alarmMessage = TextResource.localize("KAL010_9", wkTime);
						String alarmTarget = TextResource.localize("KAL010_77", wkTime);
						alarmMsgOutput = new AlarmMsgOutput(alarmMessage, alarmTarget);
					}
					break;
				case OVERLAP_TIMEZONE:
					// NO4 = ：時間帯の重複
					alarmMsgOutput = getNo4(companyId, sid, exDate, listIntegrationDai, workScheduleWorkInfos, listWorkType, listWorktime);
					break;
				case WORK_MULTI_TIME:
					if (!workScheduleWorks.isPresent()) {
						break;
					}
					WorkScheduleWorkInforImport workScheWorkMultiTime = workScheduleWorks.get();
					
					// NO5 = ：複数回勤務
					alarmMsgOutput = getNo5(
							workScheWorkMultiTime.getWorkType(), 
							workScheWorkMultiTime.getWorkTime(), 
							workScheWorkMultiTime.getTimeLeaving(),
							workScheWorkMultiTime.getOptAttendanceTime(),
							listWorkType, listWorktime);
					break;
				case WORK_ON_SCHEDULEDAY:
					if (!workScheduleWorks.isPresent()) {
						break;
					}
					WorkScheduleWorkInforImport workScheWorkOn = workScheduleWorks.get();
					
					// NO6 = ：特定日出勤
					String wtCode = workScheWorkOn.getWorkType();
					WorkType workType = listWorkType.stream().filter(x -> x.getWorkTypeCode().v().equals(wtCode)).findFirst().get();
					alarmMsgOutput = getNo6(companyId, wplId, exDate, workType);
					break;
				case SCHEDULE_UNDECIDED:
					if (!workScheduleWorks.isPresent()) {
						break;
					}
					WorkScheduleWorkInforImport workScheUndecided = workScheduleWorks.get();
					
					// NO7 = ：スケジュール未確定
					// Input　・確定区分　＝　探した勤務予定．確定区分
					int confirmedATR = workScheUndecided.getConfirmedATR();
					alarmMsgOutput = getNo7(confirmedATR);
					break;
				default:
					break;
				}
				
				String alarmMessage = null;
				String alarmTarget = null;
				if (alarmMsgOutput != null) {
					alarmMessage = alarmMsgOutput.getAlarmMessage();
					alarmTarget = alarmMsgOutput.getAlarmTarget();
				}
				
				// 取得した「アラーム表示値」をチェック
				if (alarmMessage == null) {
					continue;
				}
				
				Optional<AlarmListCheckInfor> optCheckInfor = result.getLstCheckType().stream()
						.filter(x -> x.getChekType() == AlarmListCheckType.FreeCheck && x.getNo().equals(String.valueOf(fixedAtr.value)))
						.findFirst();
				if(!optCheckInfor.isPresent()) {
					result.getLstCheckType().add(new AlarmListCheckInfor(String.valueOf(fixedAtr.value), AlarmListCheckType.FreeCheck));
				}
				
				// 「抽出結果詳細」を作成する
				// 各チェック条件の結果を作成
				String checkValue = alarmTarget;
				String alarmContent = alarmMessage;
				String comment = fixScheCondItem.getMessageDisp() != null && fixScheCondItem.getMessageDisp().isPresent() 
						? fixScheCondItem.getMessageDisp().get().v() : Strings.EMPTY;
				this.createExtractAlarm(sid,
						exDate,
						fixedAtr.nameId,
						alarmContent,
						Optional.ofNullable(comment),
						checkValue,
						String.valueOf(fixedAtr.value),
						AlarmListCheckType.FixCheck,
						wplByListSidAndPeriod,
						null,
						0,
						alarmCheckConditionCode,
						alarmExtractInfoResults);
				
			}
		}
		
		return result;
	}
	
	/**
	 * Get fix condition NO4
	 * @param workInformation 日別勤怠の勤務情報
	 * @param attendanceLeave 日別勤怠の出退勤
	 * @param listWorkType List<勤務種類＞
	 * @param listWorktime List＜就業時間帯＞
	 * @param alarmMessage アラーム表示値
	 * @param alarmTarget チェック対象値
	 */
	private AlarmMsgOutput getNo4(
			String cid,
			String sid,
			GeneralDate exDate,
			List<IntegrationOfDaily> listIntegrationDai,
			List<WorkScheduleWorkInforImport> workScheduleWorkInfos,
			List<WorkType> listWorkType,
			List<WorkTimeSetting> listWorktime) {
		// Input．List＜勤務予定＞から前日の勤務予定を探す
		// 条件： 年月日　＝　Input．年月日．Add（－１）
		// Output: 前日の勤務予定
		Optional<WorkScheduleWorkInforImport> workScheInDayBeforeOpt = workScheduleWorkInfos.stream()
				.filter(x -> x.getYmd().equals(exDate.addDays(-1))).findFirst();
		// 探した前日の勤務予定をチェック
		if (!workScheInDayBeforeOpt.isPresent()) {
			// 社員ID(List)、期間を設定して勤務予定を取得する
			// 【条件】
			//	・年月日　Between　Input．期間
			//	・社員ID　＝　Input．List＜社員ID＞
			DatePeriod periodWorkSche = new DatePeriod(exDate.addDays(-1), exDate.addDays(-1)); 
			workScheInDayBeforeOpt = workScheduleWorkInfos.stream()
					.filter(x -> x.getEmployeeId().equals(sid) && x.getYmd().beforeOrEquals(periodWorkSche.start()) && x.getYmd().afterOrEquals(periodWorkSche.end()))
					.findFirst();
			// 取得した前日の勤務予定をチェック
			if (!workScheInDayBeforeOpt.isPresent()) {
				return null;
			}
		}
		
		// 当日の勤務予定
		WorkScheduleWorkInforImport workScheInDayBefore = workScheInDayBeforeOpt.get();
		
		// Input．List＜勤務予定＞から当日の勤務予定を探す
		// 条件： 年月日　＝　Input．年月日
		// Output: 前日の勤務予定
		Optional<WorkScheduleWorkInforImport> workScheInDayOpt = workScheduleWorkInfos.stream().filter(x -> x.getYmd().equals(exDate)).findFirst();
		if (!workScheInDayOpt.isPresent()) {
			return null;
		}
		
		// 前日の勤務予定
		WorkScheduleWorkInforImport workScheInDay = workScheInDayOpt.get();
		
		// 就業時間帯をチェック
		//・当日の勤務予定．勤務情報．就業時間帯コード　＝＝　Empty
		//OR
		//・前日の勤務予定．勤務情報．勤務情報．就業時間帯コード　＝＝　Empty
		//OR
		//・当日の勤務予定．勤務情報．就業時間帯コード　＝＝　前日の勤務予定．勤務情報．務情報．就業時間帯コード
		if (workScheInDay.getWorkTime() == null
				|| workScheInDayBefore.getWorkTime() == null
				|| workScheInDay.getWorkTime().equals(workScheInDayBefore.getWorkTime())) {
			return null;
		}
		
		// Input．List＜就業時間帯＞から当日の就業時間帯を探す
		//条件：
		//　・就業時間帯コード　＝　当日の勤務予定．勤務情報．勤務情報．就業時間帯コード
		// Output:　・当日の就業時間帯
		Optional<WorkTimeSetting> worktimeInDayOpt = listWorktime.stream()
				.filter(x -> x.getWorktimeCode().equals(workScheInDay.getWorkTime())).findFirst();
		if (!worktimeInDayOpt.isPresent()) {
			return null;
		}
		WorkTimeSetting worktimeInDay = worktimeInDayOpt.get();
		
		// Input．List＜就業時間帯＞から前日の就業時間帯を探す
		//条件：
		//　・就業時間帯コード　＝　前日の勤務予定．勤務情報．勤務情報．就業時間帯コード
		// Output: ・前日の就業時間帯
		Optional<WorkTimeSetting> worktimeBeforeDayOpt = listWorktime.stream()
				.filter(x -> x.getWorktimeCode().v().equals(workScheInDayBefore.getWorkTime())).findFirst();
		if (!worktimeBeforeDayOpt.isPresent()) {
			return null;
		}
		WorkTimeSetting worktimeBeforeDay = worktimeBeforeDayOpt.get();
		
		// 所定時間設定
		Optional<PredetemineTimeSetting> predTimeSetInDayOpt = this.predTimeSetRepo.findByWorkTimeCode(cid, worktimeInDay.getWorktimeCode().v());
		Optional<PredetemineTimeSetting> predTimeSetInDayBeforeOpt = this.predTimeSetRepo.findByWorkTimeCode(cid, worktimeBeforeDay.getWorktimeCode().v());
		
		// 時刻を比較
		if (predTimeSetInDayOpt.isPresent() && predTimeSetInDayBeforeOpt.isPresent()) {
			PredetemineTimeSetting predetemineTimeSettingInDay  = predTimeSetInDayOpt.get();
			PrescribedTimezoneSetting prescribedTimezoneSettingInDay = predetemineTimeSettingInDay.getPrescribedTimezoneSetting();
			TimezoneUse timezoneUseInDay = prescribedTimezoneSettingInDay.getLstTimezone().stream().filter(x -> x.getWorkNo() == TimezoneUse.SHIFT_ONE).findFirst().get();
			
			PredetemineTimeSetting predetemineTimeSettingInDayBefore  = predTimeSetInDayBeforeOpt.get();
			PrescribedTimezoneSetting prescribedTimezoneSettingInDayBefore = predetemineTimeSettingInDayBefore.getPrescribedTimezoneSetting();
			TimezoneUse timezoneUseInDayBefore = prescribedTimezoneSettingInDayBefore.getLstTimezone().stream().filter(x -> x.getWorkNo() == TimezoneUse.SHIFT_ONE).findFirst().get();
			
			//（
			//・前日の就業時間帯．終了．日区分　＝＝　当日の就業時間帯．開始．日区分
			//AND
			//・前日の就業時間帯．終了．時刻　＝＝　当日の就業時間帯．開始．時刻
			//）
			//OR
			//・前日の就業時間帯．終了．日区分　＞　当日の就業時間帯．開始．日区分
			if ((timezoneUseInDayBefore.getEnd().getDayDivision().equals(timezoneUseInDay.getStart().getDayDivision()) 
					&& timezoneUseInDayBefore.getEnd().getDayTime() == timezoneUseInDay.getStart().getDayTime())
					|| timezoneUseInDayBefore.getEnd().getDayDivision().hours > timezoneUseInDay.getStart().getDayDivision().hours) {
				String alarmMessage = TextResource.localize("KAL010_1005", exDate.addDays(-1).toString(), exDate.toString());
				
				String param1 = timezoneUseInDayBefore.getStart().getRawTimeWithFormat() + ErrorAlarmConstant.PERIOD_SEPERATOR + timezoneUseInDayBefore.getEnd().getRawTimeWithFormat();
				String param2 = exDate.toString();
				String param3 = timezoneUseInDay.getStart().getRawTimeWithFormat() + ErrorAlarmConstant.PERIOD_SEPERATOR + timezoneUseInDay.getEnd().getRawTimeWithFormat();
				String alarmTarget = TextResource.localize("KAL010_1021", exDate.addDays(-1).toString(), param1, param2, param3);
				
				return new AlarmMsgOutput(alarmMessage, alarmTarget);
			}
		}
		
		return null;
	}
	
	/**
	 * get fix condition NO5
	 * @param workInformation 日別勤怠の勤務情報
	 * @param attendanceLeave 日別勤怠の出退勤
	 * @param listWorkType List<勤務種類＞
	 * @param listWorktime List＜就業時間帯＞
	 * @param alarmMessage アラーム表示値
	 * @param alarmTarget チェック対象値
	 */
	private AlarmMsgOutput getNo5(
			String workTypeCode, 
			String workTimeCode,
			Optional<TimeLeavingOfDailyAttdImport> attendanceLeaveOpt,
			Optional<AttendanceTimeOfDailyAttendanceImport> optAttendanceTime,
			List<WorkType> listWorkType,
			List<WorkTimeSetting> listWorktime) {
		// Input．日別勤怠の出退勤が存在するかチェックする
		// 存在しない場合
		if (!attendanceLeaveOpt.isPresent() || workTimeCode == null || !optAttendanceTime.isPresent()) {
			return null;
		}
		
		TimeLeavingOfDailyAttdImport attendanceLeave = attendanceLeaveOpt.get();
		
		// 勤務回数を取得する
		// QA#115985
		int workTime = optAttendanceTime.get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getWorkTimes();
		// Input．日別勤怠の出退勤．勤務回数　＞＝２
		if (workTime < 2) {
			return null;			
		}
		
		Optional<WorkType> workTypeOpt = listWorkType.stream().filter(x -> x.getWorkTypeCode().equals(workTypeCode)).findFirst();
		Optional<WorkTimeSetting> workTimeSettingOpt = listWorktime.stream().filter(x -> x.getWorktimeCode().equals(workTimeCode)).findFirst();
		
		// Input．日別勤怠の勤務情報．勤務情報．勤務種類コード+’　’+List<勤務種類＞にInput．日別勤怠の勤務情報．勤務情報．勤務種類コード　の名称
		String msgParam0 = workTypeCode + "　" + (workTypeOpt.isPresent() ? workTypeOpt.get().getName().v() : "");
		// Input．日別勤怠の勤務情報．勤務情報．就業時間帯コード+’　’+List<就業時間帯＞にInput．日別勤怠の勤務情報．勤務情報．就業時間帯コード　の名称
		String msgParam1 = workTypeCode + "　" + (workTimeSettingOpt.isPresent() ? workTimeSettingOpt.get().getWorkTimeDisplayName().getWorkTimeName().v() : "");
		String alarmMessage = TextResource.localize("KAL010_1307", msgParam0, msgParam1);
		
		// Input．日別勤怠の出退勤．勤務NO．実打刻　＝＝　1
		Optional<TimeLeavingWorkImport> timeLeavingWorkImport1 = attendanceLeave.getTimeLeavingWorks().stream()
				.filter(x -> x.getWorkNo() == 1).findFirst();
		// Input．日別勤怠の出退勤．勤務NO．実打刻　＝＝　2
		Optional<TimeLeavingWorkImport> timeLeavingWorkImport2 = attendanceLeave.getTimeLeavingWorks().stream()
				.filter(x -> x.getWorkNo() == 2).findFirst();
		if (timeLeavingWorkImport1.isPresent() && timeLeavingWorkImport2.isPresent()) {
			if (!timeLeavingWorkImport1.get().getAttendanceStamp().isPresent() 
					|| !timeLeavingWorkImport2.get().getLeaveStamp().isPresent()) {
				return null;
			}
			
			WorkStampImport attendanceStamp1 = null;
			WorkStampImport attendanceStamp2 = null;
			
			// QA#115444
			// 日別勤怠の出退勤．出退勤．出勤
			if (!timeLeavingWorkImport1.get().getAttendanceStamp().get().getActualStamp().isPresent()) {
				if (timeLeavingWorkImport1.get().getAttendanceStamp().get().getStamp().isPresent()) {
					attendanceStamp1 = timeLeavingWorkImport1.get().getAttendanceStamp().get().getStamp().get();
				}
			} else {
				attendanceStamp1 = timeLeavingWorkImport1.get().getAttendanceStamp().get().getActualStamp().get();
			}
			
			if (!timeLeavingWorkImport2.get().getAttendanceStamp().get().getActualStamp().isPresent()) {
				if (timeLeavingWorkImport2.get().getAttendanceStamp().get().getStamp().isPresent()) {
					attendanceStamp2 = timeLeavingWorkImport2.get().getAttendanceStamp().get().getStamp().get();
				}
			} else {
				attendanceStamp2 = timeLeavingWorkImport2.get().getAttendanceStamp().get().getActualStamp().get();
			}
			
			// 日別勤怠の出退勤．出退勤．退勤
			WorkStampImport leaveStamp1 = null;
			WorkStampImport leaveStamp2 = null;
			if (timeLeavingWorkImport1.get().getLeaveStamp().get().getActualStamp().isPresent()) {
				leaveStamp1 = timeLeavingWorkImport1.get().getLeaveStamp().get().getActualStamp().get();
			} else {
				if (timeLeavingWorkImport1.get().getLeaveStamp().get().getStamp().isPresent()) {
					leaveStamp1 = timeLeavingWorkImport1.get().getLeaveStamp().get().getStamp().get();
				}
			}
			
			if (timeLeavingWorkImport2.get().getLeaveStamp().get().getActualStamp().isPresent()) {
				leaveStamp2 = timeLeavingWorkImport2.get().getLeaveStamp().get().getActualStamp().get();
			} else {
				if (timeLeavingWorkImport2.get().getLeaveStamp().get().getStamp().isPresent()) {
					leaveStamp2 = timeLeavingWorkImport2.get().getLeaveStamp().get().getStamp().get();
				}
			}
			
			String targetParam0 = Strings.EMPTY;
			String targetParam1 = Strings.EMPTY;
			if (attendanceStamp1 != null && leaveStamp1 != null) {
				// Input．日別勤怠の出退勤．出退勤．出勤．実打刻.時刻　+　’～’　+　Input．日別勤怠の出退勤．出退勤．退勤．実打刻.時刻
				targetParam0 = formatTimeWithDay(attendanceStamp1.getTimeDay().getTimeWithDay()) + ErrorAlarmConstant.PERIOD_SEPERATOR + formatTimeWithDay(leaveStamp1.getTimeDay().getTimeWithDay());
			} else {
				targetParam0 = TextResource.localize("KAL010_1027");
			}
			if (attendanceStamp2 != null && leaveStamp2 != null) {
				// Input．日別勤怠の出退勤．出退勤．出勤．実打刻.時刻　+　’～’　+　Input．日別勤怠の出退勤．出退勤．退勤．実打刻.時刻
				targetParam1 = formatTimeWithDay(attendanceStamp2.getTimeDay().getTimeWithDay()) + ErrorAlarmConstant.PERIOD_SEPERATOR + formatTimeWithDay(leaveStamp2.getTimeDay().getTimeWithDay());
			} else {
				targetParam1 = TextResource.localize("KAL010_1027");
			}
			
			String alarmTarget = TextResource.localize("KAL010_1022", targetParam0, targetParam1);
			
			return new AlarmMsgOutput(alarmMessage, alarmTarget);
		}
		
		return null;
	}
	
	/**
	 * Convert int to format hours:minute
	 * @param value
	 * @return
	 */
	private String formatTimeWithDay(Integer value) {
		TimeWithDayAttr timeWithday = new TimeWithDayAttr(value);
		return timeWithday.getRawTimeWithFormat();
	}
	
	/**
	 * Get fix condition NO6
	 * @param companyId company id
	 * @param wplId 職場ID
	 * @param exDate 年月日
	 * @param workType 勤務種類
	 * @param alarmMessage アラーム表示値
	 * @param alarmTarget チェック対象値
	 */
	private AlarmMsgOutput getNo6(String companyId, String wplId, GeneralDate exDate, WorkType workType) {
		// Input．勤務種類の出勤休日区分をチェック
		if (workType.chechAttendanceDay().isHoliday()) {
			return null;
		}
		
		// 1日休日じゃないの場合
		// 職場の特定日設定を取得する (Acquire specific day setting of the workplace)
		RecSpecificDateSettingImport specificDateSetting = specificDateSettingAdapter.specificDateSettingService(companyId, wplId, exDate);
		
		// 取得した「特定日」．特定日項目をチェック
		if (specificDateSetting.getDate() == null) {
			return null;
		}
		
		// 特定日項目NO（List）から特定日を取得
		List<Integer> listSpecificDayItemNo = specificDateSetting.getNumberList();
		if (listSpecificDayItemNo.isEmpty()) {
			return null;
		}
		
		// アラーム表示値を生成する
		String msgParam0 = specificDateSettingAdapter.getSpecifiDateItem(companyId, listSpecificDayItemNo).stream()
				.map(x -> x.getSpecificName()).collect(Collectors.joining("、"));
		String msgParam1 = workType.getWorkTypeCode().v() + "　" + workType.getName().v();
		
		String alarmMessage = TextResource.localize("KAL010_1006", msgParam0, msgParam1);
		String alarmTarget = TextResource.localize("KAL010_1023", msgParam1);
		
		return new AlarmMsgOutput(alarmMessage, alarmTarget);
	}
	
	/**
	 * Get fix condition NO7
	 * @param confirmedATR 確定区分
	 * @param alarmMessage アラーム表示値
	 * @param alarmTarget チェック対象値
	 */
	private AlarmMsgOutput getNo7(int confirmedATR) {
		if (confirmedATR != 0) {
			return null;
		}
		
		// 未確定の場合
		// ・アラーム表示値　＝　#KAL010_1007　
		String alarmMessage = TextResource.localize("KAL010_1007");
		// ・チェック対象値　＝　#KAL010_1024
		String alarmTarget = TextResource.localize("KAL010_1024");
		
		return new AlarmMsgOutput(alarmMessage, alarmTarget);
	}
	
	private String getWorkplaceId(String sid, GeneralDate exDate, List<WorkPlaceHistImportAl> wplByListSidAndPeriod) {
		String wplId = "";
		Optional<WorkPlaceHistImportAl> optWorkPlaceHistImportAl = wplByListSidAndPeriod.stream().filter(x -> x.getEmployeeId().equals(sid)).findFirst();
		if(optWorkPlaceHistImportAl.isPresent()) {
			Optional<WorkPlaceIdAndPeriodImportAl> optWorkPlaceIdAndPeriodImportAl = optWorkPlaceHistImportAl.get().getLstWkpIdAndPeriod().stream()
					.filter(x -> x.getDatePeriod().start().beforeOrEquals(exDate) && x.getDatePeriod().end().afterOrEquals(exDate)).findFirst();
			if(optWorkPlaceIdAndPeriodImportAl.isPresent()) {
				wplId = optWorkPlaceIdAndPeriodImportAl.get().getWorkplaceId();
			}
		}
		
		return wplId;
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
	
	@Data
	@AllArgsConstructor
	private class AlarmMsgOutput {
		private String alarmMessage;
		private String alarmTarget;
	}
}
