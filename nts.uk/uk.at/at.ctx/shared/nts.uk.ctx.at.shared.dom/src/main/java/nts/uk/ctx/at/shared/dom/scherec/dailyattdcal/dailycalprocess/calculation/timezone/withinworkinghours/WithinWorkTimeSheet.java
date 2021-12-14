package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.PremiumAtr;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.AddSetting;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayAddtionSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.HolidayCalcMethodSet;
import nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime.TimeHolidayAdditionSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalcOfLeaveEarlySetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BPTimeItemSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.ConditionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.VacationClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.SettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ActualWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.CommonFixedWorkTimezoneSet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.FlexWithinWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.LateLeaveEarlyManagementTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerPersonDailySet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSheetRoundingAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeVacationWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.VacationAddTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.WorkHour;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.MidNightTimeSheetForCalcList;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.service.ActualWorkTimeSheetListService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.holidaypriorityorder.CompanyHolidayPriorityOrder;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.DailyUnit;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeZoneSet;
import nts.uk.ctx.at.shared.dom.worktime.common.LateEarlyAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.flexset.CoreTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 就業時間内時間帯
 * @author keisuke_hoshina
 */
@Getter
public class WithinWorkTimeSheet implements LateLeaveEarlyManagementTimeSheet{

	//就業時間内時間枠
	protected final List<WithinWorkTimeFrame> withinWorkTimeFrame;
	//短時間時間帯
	protected List<TimeSheetOfDeductionItem> shortTimeSheet;
	//早退判断時刻
	protected List<LeaveEarlyDecisionClock> leaveEarlyDecisionClock = new ArrayList<>();
	//遅刻判断時刻
	protected List<LateDecisionClock> lateDecisionClock = new ArrayList<>();
	
	//時間休暇使用時間
	protected TimeVacationWork timeVacationUseTime = TimeVacationWork.defaultValue();
	//上限実働就業時間
	protected Optional<AttendanceTime> limitActualWorkTime = Optional.empty();
	
	public WithinWorkTimeSheet(
			List<WithinWorkTimeFrame> withinWorkTimeFrame,
			List<TimeSheetOfDeductionItem> shortTimeSheet,
			List<LeaveEarlyDecisionClock> leaveEarlyDecisionClock,
			List<LateDecisionClock> lateDecisionClock){
		
		this.withinWorkTimeFrame = withinWorkTimeFrame;
		this.shortTimeSheet = shortTimeSheet;
		this.leaveEarlyDecisionClock = leaveEarlyDecisionClock;
		this.lateDecisionClock = lateDecisionClock;
	}
	
	public WithinWorkTimeSheet(List<WithinWorkTimeFrame> withinWorkTimeFrame,List<TimeSheetOfDeductionItem> shortTimeSheets,Optional<LateDecisionClock> lateDecisionClock,Optional<LeaveEarlyDecisionClock> leaveEarlyDecisionClock) {
		this.withinWorkTimeFrame = withinWorkTimeFrame;
		this.shortTimeSheet = shortTimeSheets;
		if(lateDecisionClock != null
			&& lateDecisionClock.isPresent())
			this.lateDecisionClock.add(lateDecisionClock.get());
		if(leaveEarlyDecisionClock != null
			&& leaveEarlyDecisionClock.isPresent())
			this.leaveEarlyDecisionClock.add(leaveEarlyDecisionClock.get());
	}

	/**
	 * 空で作成する
	 * @return 就業時間内時間帯
	 */
	public static WithinWorkTimeSheet createEmpty() {
		return new WithinWorkTimeSheet(
				Arrays.asList(WithinWorkTimeFrame.createEmpty(new EmTimeFrameNo(1), new WorkNo(1))),
				Collections.emptyList(),
				Optional.empty(),
				Optional.empty());
	}

	/**
	 * 就業時間内時間帯
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param timeLeavingWork 出退勤
	 * @param timeVacationWork 時間休暇WORK
	 * @return 就業時間内時間帯
	 */
	public static WithinWorkTimeSheet createAsFixed(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			DeductionTimeSheet deductionTimeSheet,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			TimeLeavingWork timeLeavingWork,
			TimeVacationWork timeVacationWork) {

		/*出勤系ではない場合は時間帯は作成しない*/
		if(!todayWorkType.isWeekDayAttendance())
			return new WithinWorkTimeSheet(new ArrayList<>(),new ArrayList<>(),Optional.empty(),Optional.empty());
		
		// 遅刻判断時刻を求める
		Optional<LateDecisionClock> lateDesClock = LateDecisionClock.create(
				timeLeavingWork.getWorkNo().v(),
				predetermineTimeSetForCalc,
				integrationOfWorkTime,
				integrationOfDaily,
				timeLeavingWork,
				todayWorkType,
				deductionTimeSheet);
		
		// 早退判断時刻を求める
		Optional<LeaveEarlyDecisionClock>leaveEarlyDesClock = LeaveEarlyDecisionClock.create(
				timeLeavingWork.getWorkNo().v(),
				predetermineTimeSetForCalc,
				integrationOfWorkTime,
				integrationOfDaily,
				timeLeavingWork,
				todayWorkType,
				deductionTimeSheet);
		
		//相殺前の時間休暇WORKをセット（クローン）
		TimeVacationWork timeVacationWorkCloned = timeVacationWork.clone();
		
		//就業時間内枠の作成
		List<WithinWorkTimeFrame> timeFrames = isWeekDayProcess(
				companyCommonSetting,
				personDailySetting,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				timeLeavingWork,
				deductionTimeSheet,
				predetermineTimeSetForCalc,
				personDailySetting.getPredetermineTimeSetByPersonWeekDay(),
				lateDesClock,
				leaveEarlyDesClock,
				timeVacationWork);
		
		//短時間時間帯の取得
		// upd 2021.1.20 shuichi_ishida (ver4対応) 
		//List<TimeSheetOfDeductionItem> shortTimeSheets = toHaveShortTime(timeFrames,deductionTimeSheet.getForDeductionTimeZoneList());
		List<TimeSheetOfDeductionItem> shortTimeSheets = new ArrayList<>();
		
		WithinWorkTimeSheet result = new WithinWorkTimeSheet(timeFrames,shortTimeSheets,lateDesClock,leaveEarlyDesClock);
		result.timeVacationUseTime = timeVacationWorkCloned;
		return result;
	}

	/**
	 * 就業時間内時間帯の作成
	 * アルゴリズム：固定勤務（平日・就内）
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param timeLeavingWork 出退勤
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param predetermineTimeSetByPersonWeekDay 計算用所定時間設定（個人平日時）
	 * @param lateDesClock 遅刻判断時刻
	 * @param leaveEarlyDesClock 早退判断時刻
	 * @param timeVacationWork 時間休暇WORK
	 * @return 就業時間内時間枠(List)
	 */
	private static List<WithinWorkTimeFrame> isWeekDayProcess(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeLeavingWork timeLeavingWork,
			DeductionTimeSheet deductionTimeSheet,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			PredetermineTimeSetForCalc predetermineTimeSetByPersonWeekDay,
			Optional<LateDecisionClock> lateDesClock,
			Optional<LeaveEarlyDecisionClock>leaveEarlyDesClock,
			TimeVacationWork timeVacationWork) {
		
		//出退勤時刻と就業時間帯の勤務時間帯との重複部分を取得
		CommonFixedWorkTimezoneSet workTimeZone = new CommonFixedWorkTimezoneSet();
		workTimeZone = workTimeZone.forWorkTime(integrationOfWorkTime);
		List<WithinWorkTimeFrame> withinWorkTimeFrame = duplicatedByStamp(timeLeavingWork,workTimeZone,todayWorkType,integrationOfWorkTime,predetermineTimeSetForCalc);
		
		val timeFrames = new ArrayList<WithinWorkTimeFrame>();
		for(WithinWorkTimeFrame duplicateTimeSheet :withinWorkTimeFrame) {
			//就業時間内時間枠の作成
			timeFrames.add(WithinWorkTimeFrame.createWithinWorkTimeFrame(
					companyCommonSetting,
					personDailySetting,
					todayWorkType,
					integrationOfWorkTime,
					integrationOfDaily,
					timeLeavingWork,
					duplicateTimeSheet,
					deductionTimeSheet,
					predetermineTimeSetForCalc,
					lateDesClock,
					leaveEarlyDesClock,
					timeVacationWork,
					duplicateTimeSheet.getWorkingHoursTimeNo().v().intValue() == withinWorkTimeFrame.size()));
		}
		return timeFrames;
	}
	
	/**
	 * 自身が持つ短時間勤務時間帯(控除)を収集
	 * @return　短時間勤務時間帯
	 */
	public static List<TimeSheetOfDeductionItem> toHaveShortTime(List<WithinWorkTimeFrame> afterWithinPremiumCreate,List<TimeSheetOfDeductionItem> dedTimeSheet) {
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		List<TimeSheetOfDeductionItem> allInFrameShortTimeSheets = new ArrayList<>();
		for(WithinWorkTimeFrame frame : afterWithinPremiumCreate) {
			allInFrameShortTimeSheets.addAll(frame.collectShortTimeSheetInFrame());
		}
		//マスタ側の控除時間帯ループ
		val loopList = dedTimeSheet.stream().filter(tc -> tc.getDeductionAtr().isChildCare()).collect(Collectors.toList());
		for(TimeSheetOfDeductionItem masterDedItem : loopList) {
			List<TimeSheetOfDeductionItem> notDupShort = Arrays.asList(masterDedItem);
			for(TimeSheetOfDeductionItem dedItem:allInFrameShortTimeSheets) {
				//ループ中短時間のどこにも所属していない時間帯
				List<TimeSpanForDailyCalc> timeReplace = notDupShort.stream().map(tc -> tc.getTimeSheet().getNotDuplicationWith(dedItem.getTimeSheet())).flatMap(List::stream).collect(Collectors.toList());
				timeReplace = timeReplace.stream().filter(ts -> ts.lengthAsMinutes() > 0).collect(Collectors.toList());
				notDupShort = timeReplace.stream().map(ts -> dedItem.cloneWithNewTimeSpan(Optional.of(ts))).collect(Collectors.toList());
			}
			if(!notDupShort.isEmpty()) {
				returnList.addAll(notDupShort);
			}
		}		
		return returnList;
	}

	/***
	 * 出勤、退勤時刻との重複部分を調べる
	 * @param workingHourSet 就業時間枠の時間帯
	 * @param timeLeavingWork　出退勤
	 * @return　時間枠の時間帯と出退勤の重複時間
	 */
	private static List<WithinWorkTimeFrame> duplicatedByStamp(
			TimeLeavingWork timeLeavingWork,
			CommonFixedWorkTimezoneSet lstHalfDayWorkTimezone,
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc) {
		List<WithinWorkTimeFrame> returnList = new ArrayList<>();
		Optional<TimeSpanForCalc> duplicatedRange = Optional.empty(); 
		val attendanceHolidayAttr = workType.getAttendanceHolidayAttr();
		val emTimeZoneSet = getWorkingHourSetByAmPmClass(lstHalfDayWorkTimezone, attendanceHolidayAttr);
		
		for(EmTimeZoneSet timeZone:emTimeZoneSet) {
			duplicatedRange = timeZone.getTimezone().getDuplicatedWith(timeLeavingWork.getTimespan());
			if(duplicatedRange.isPresent()) {
				returnList.add(new WithinWorkTimeFrame(
						timeZone.getEmploymentTimeFrameNo(),
						timeLeavingWork.getWorkNo(),
						new TimeSpanForDailyCalc(duplicatedRange.get()),
						WithinWorkTimeFrame.getBeforeLateEarlyTimeSheet(
								new TimeSpanForDailyCalc(duplicatedRange.get()),
								workType,
								integrationOfWorkTime,
								predetermineTimeSetForCalc,
								timeLeavingWork,
								Optional.empty()),
						timeZone.getTimezone().getRounding(),
						new ArrayList<>(),
						new ArrayList<>(),
						new ArrayList<>(),
						MidNightTimeSheetForCalcList.createEmpty(),
						new ArrayList<>(),
						Optional.empty(),
						Optional.empty()));
			}else {
				returnList.add(new WithinWorkTimeFrame(timeZone.getEmploymentTimeFrameNo(),
						timeLeavingWork.getWorkNo(),
						new TimeSpanForDailyCalc(timeZone.getTimezone().getStart(),timeZone.getTimezone().getStart()),
						new TimeSpanForDailyCalc(timeZone.getTimezone().getStart(),timeZone.getTimezone().getStart()),
						timeZone.getTimezone().getRounding(),
						new ArrayList<>(),
						new ArrayList<>(),
						new ArrayList<>(),
						MidNightTimeSheetForCalcList.createEmpty(),
						new ArrayList<>(),
						Optional.empty(),
						Optional.empty()));
			}
		}
		return returnList;
	}

	/**
	 * 平日出勤の出勤時間帯を取得
	 * @param fixedWorkSetting 固定勤務設定クラス
	 * @param attendanceHolidayAttr 出勤休日区分
	 * @return 出勤時間帯
	 */
	private static List<EmTimeZoneSet> getWorkingHourSetByAmPmClass(
			CommonFixedWorkTimezoneSet lstHalfDayWorkTimezone,
			AttendanceHolidayAttr attendanceHolidayAttr) {
		
		switch (attendanceHolidayAttr) {
		case FULL_TIME:
		case HOLIDAY:
			return lstHalfDayWorkTimezone.getFixedWorkTimezoneMap().get(AmPmAtr.ONE_DAY).getLstWorkingTimezone();
		case MORNING:
			return lstHalfDayWorkTimezone.getFixedWorkTimezoneMap().get(AmPmAtr.AM).getLstWorkingTimezone();
		case AFTERNOON:
			return lstHalfDayWorkTimezone.getFixedWorkTimezoneMap().get(AmPmAtr.PM).getLstWorkingTimezone();
		default:
			throw new RuntimeException("unknown attendanceHolidayAttr" + attendanceHolidayAttr);
		}
	}
	
	/**
	 * 遅刻判断時刻を取得する
	 * @param workNo
	 * @return 遅刻判断時刻
	 */
	public Optional<LateDecisionClock> getLateDecisionClock(int workNo) {
		return this.lateDecisionClock.stream().filter(tc -> tc.getWorkNo() == workNo).findFirst();
	}
	
	/**
	 * 早退判断時刻を取得する
	 * @param workNo
	 * @return 早退判断時刻
	 */
	public Optional<LeaveEarlyDecisionClock> getLeaveEarlyDecisionClock(int workNo) {
		return this.leaveEarlyDecisionClock.stream().filter(tc -> tc.getWorkNo() == workNo).findFirst();
	}
	
	/**
	 * コアタイムのセット
	 * @param coreTimeSetting コアタイム時間設定
	 */
	public WithinWorkTimeSheet createWithinFlexTimeSheet(CoreTimeSetting coreTimeSetting,DeductionTimeSheet dedTimeSheet) {
		List<TimeSpanForDailyCalc> duplicateCoreTimeList = new ArrayList<>();
		for(WithinWorkTimeFrame workTimeFrame : this.withinWorkTimeFrame) {
			Optional<TimeSpanForDailyCalc> duplicateSpan = workTimeFrame.getTimeSheet().getDuplicatedWith(new TimeSpanForDailyCalc(coreTimeSetting.getCoreTimeSheet().getStartTime(),
																									 					 coreTimeSetting.getCoreTimeSheet().getEndTime())); 
			if(duplicateSpan.isPresent()) {
				duplicateCoreTimeList.add(duplicateSpan.get());
			}
		}
		TimeWithDayAttr startTime = new TimeWithDayAttr(0);
		TimeWithDayAttr endTime = new TimeWithDayAttr(0);
		if(!duplicateCoreTimeList.isEmpty()) {
			startTime = duplicateCoreTimeList.stream().sorted((first,second)-> first.getStart().compareTo(second.getStart())).collect(Collectors.toList()).get(0).getStart();
			endTime = duplicateCoreTimeList.stream().sorted((first,second)-> first.getStart().compareTo(second.getStart())).collect(Collectors.toList()).get(duplicateCoreTimeList.size() - 1).getEnd();
			/*フレックス時間帯に入れる*/
			return FlexWithinWorkTimeSheet.createNew(
					this.withinWorkTimeFrame,
					toHaveShortTime(this.withinWorkTimeFrame,dedTimeSheet.getForDeductionTimeZoneList()),
					this.leaveEarlyDecisionClock,
					this.lateDecisionClock,
					Optional.of(new TimeSpanForDailyCalc(startTime, endTime)),
					this.timeVacationUseTime,
					this.limitActualWorkTime);
		}
		else {
			return FlexWithinWorkTimeSheet.createNew(
					this.withinWorkTimeFrame,
					toHaveShortTime(this.withinWorkTimeFrame,dedTimeSheet.getForDeductionTimeZoneList()),
					this.leaveEarlyDecisionClock,
					this.lateDecisionClock,
					Optional.empty(),
					this.timeVacationUseTime,
					this.limitActualWorkTime);
		}
	}
	
	/**
	 * ループ処理
	 * 就業時間の計算(控除時間差し引いた後)
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param autoCalcOfLeaveEarlySetting 遅刻早退の自動計算設定
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param flexCalcMethod フレックス勤務の設定
	 * @param dailyUnit 法定労働時間
	 * @param commonSetting 就業時間帯の共通設定
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @return 就業時間
	 */
	public WorkHour calcWorkTime(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSet,
			AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			Optional<SettingOfFlexWork> flexCalcMethod,
			DailyUnit dailyUnit,
			Optional<WorkTimezoneCommonSet> commonSetting,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			NotUseAtr lateEarlyMinusAtr) {

		// 統合就業時間帯の確認
		Optional<CoreTimeSetting> coreTimeSettingForCalc = Optional.empty();	// コアタイム時間帯設定（計算用）
		if (integrationOfWorkTime.isPresent()){
			coreTimeSettingForCalc = integrationOfWorkTime.get().getCoreTimeSettingForCalc(Optional.of(workType));
		}
		
		//就業時間の計算
		AttendanceTime workTime = this.calcWorkTimeBeforeDeductPremium(
				integrationOfDaily,
				integrationOfWorkTime,
				premiumAtr,
				vacationClass,
				workType,
				predetermineTimeSet,
				autoCalcOfLeaveEarlySetting,
				addSetting,
				holidayAddtionSet,
				flexCalcMethod,
				dailyUnit,
				commonSetting,
				conditionItem,
				predetermineTimeSetByPersonInfo,
				lateEarlyMinusAtr);
		
		//休暇加算処理
		int	holidayAddMinutes = this.vacationAddProcess(
					integrationOfWorkTime,
					premiumAtr,
					vacationClass,
					workType,
					addSetting,
					holidayAddtionSet,
					predetermineTimeSet,
					conditionItem,
					predetermineTimeSetByPersonInfo).valueAsMinutes();
		workTime = workTime.addMinutes(holidayAddMinutes);
		
		//所定内割増時間の計算
		AttendanceTime withinpremiumTime = calcPredeterminePremiumTime(workType, dailyUnit, predetermineTimeSet, workTime);
		//所定内割増時間を減算
		workTime = workTime.minusMinutes(withinpremiumTime.valueAsMinutes());
		
		// 休暇の計算方法の設定を取得する
		HolidayCalcMethodSet holidayCalcMethodSet = addSetting.getVacationCalcMethodSet();
		//コア無しフレックスで遅刻した場合の時間補正
		if(holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().isPresent()&&coreTimeSettingForCalc.isPresent()&&!coreTimeSettingForCalc.get().isUseTimeSheet()) {
			//遅刻時間を就業時間から控除しない場合
			if(!holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().isDeductLateLeaveEarly(commonSetting)) {
				TimeWithCalculation calcedLateTime = calcNoCoreCalcLateTimeForWorkTime(
						workTime,
						DeductionAtr.Appropriate,
						autoCalcOfLeaveEarlySetting.isLate(),
						holidayCalcMethodSet,
						coreTimeSettingForCalc,
						commonSetting);
				
				//コア無しフレックス遅刻時間　＞　0 の場合
				if(calcedLateTime.getCalcTime().greaterThan(0)) {
					workTime = coreTimeSettingForCalc.get().getMinWorkTime();
				}
			}
		}
		return new WorkHour(workTime, new AttendanceTime(holidayAddMinutes), withinpremiumTime);
	}
	
	/**
	 * 就業時間内時間枠の全枠分の就業時間を算出する
	 * (所定内割増時間を差し引く前)
	 * アルゴリズム：就業時間の計算
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param autoCalcOfLeaveEarlySetting 遅刻早退の自動計算設定
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param flexCalcMethod フレックス勤務の設定
	 * @param dailyUnit 法定労働時間
	 * @param commonSetting 就業時間帯の共通設定
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @return 就業時間内時間枠の全枠分の就業時間
	 */
	public AttendanceTime calcWorkTimeBeforeDeductPremium(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSet,
			AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			Optional<SettingOfFlexWork> flexCalcMethod,
			DailyUnit dailyUnit,
			Optional<WorkTimezoneCommonSet> commonSetting,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			NotUseAtr lateEarlyMinusAtr) {
		int workTime = 0;
		// 就業時間帯を取得
		for (WithinWorkTimeFrame copyItem : withinWorkTimeFrame) {
			// 就業時間の計算
			workTime += copyItem.calcActualWorkTimeAndWorkTime(
					integrationOfDaily,
					integrationOfWorkTime,
					autoCalcOfLeaveEarlySetting,
					addSetting,
					holidayAddtionSet,
					premiumAtr,
					commonSetting,
					lateEarlyMinusAtr,
					this)
					.valueAsMinutes();
		}
		// 就業時間を返す
		return new AttendanceTime(workTime);
	}
	
	/**
	 * 休暇加算処理
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @return 休暇加算時間
	 */
	public AttendanceTime vacationAddProcess(
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			PredetermineTimeSetForCalc predetermineTimeSet,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo) {

		int vacationAddMinutes = 0;
		// 就業時間帯コードの確認
		Optional<WorkTimeCode> workTimeCode = Optional.empty();
		if (integrationOfWorkTime.isPresent()) workTimeCode = Optional.of(integrationOfWorkTime.get().getCode());
		// 休暇加算時間を計算
		VacationAddTime vacationAddTime = vacationClass.calcVacationAddTime(
				premiumAtr,
				workType,
				workTimeCode,
				conditionItem,
				addSetting,
				Optional.of(holidayAddtionSet),
				predetermineTimeSet == null ? Optional.empty() : Optional.of(predetermineTimeSet),
				predetermineTimeSetByPersonInfo);
		vacationAddMinutes += vacationAddTime.calcTotaladdVacationAddTime();
		// 休暇加算時間を返す
		return new AttendanceTime(vacationAddMinutes);
	}
	
	/**
	 * コアタイム無しの遅刻時間計算
	 * @param workTime 就業時間帯
	 * @param deductionAtr 控除区分
	 * @param late 日別実績の計算区分.遅刻早退の自動計算設定.遅刻
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param coreTimeSetting コアタイム時間帯設定
	 * @param commonSetting 就業時間帯の共通設定
	 * @return コアタイム無しの遅刻時間
	 */
	public TimeWithCalculation calcNoCoreCalcLateTimeForWorkTime(
			AttendanceTime workTime,
			DeductionAtr deductionAtr,
			boolean late,//日別実績の計算区分.遅刻早退の自動計算設定.遅刻
			HolidayCalcMethodSet holidayCalcMethodSet,
			Optional<CoreTimeSetting> coreTimeSetting,
			Optional<WorkTimezoneCommonSet> commonSetting){
		
		//遅刻時間の計算
		AttendanceTime lateTime = calcLateTimeForWorkTime(
				workTime,
				deductionAtr,
				holidayCalcMethodSet,
				coreTimeSetting,
				commonSetting);
		
		//時間休暇との相殺処理(いずれ実装が必要)
		
		//遅刻早退の自動計算設定．遅刻をチェック
		if(late) {
			return TimeWithCalculation.sameTime(lateTime);
		}
		return TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(0), lateTime);
	}
	
	/**
	 * 遅刻時間の計算
	 * フレックスのコア無しの場合専用の遅刻時間の計算処理(就業時間計算用)
	 * @param workTime 勤務種類
	 * @param deductionAtr 控除区分
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param coreTimeSetting コアタイム時間帯設定
	 * @param commonSetting 就業時間帯の共通設定
	 * @return 遅刻時間
	 */
	public AttendanceTime calcLateTimeForWorkTime(
			AttendanceTime workTime,
			DeductionAtr deductionAtr,
			HolidayCalcMethodSet holidayCalcMethodSet,
			Optional<CoreTimeSetting> coreTimeSetting,
			Optional<WorkTimezoneCommonSet> commonSetting) {
		
		//パラメータ「控除区分」＝”控除”　かつ　控除しない
		if(deductionAtr.isDeduction()&&holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().isPresent()&&!holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().isDeductLateLeaveEarly(commonSetting)) {
			return new AttendanceTime(0);
		}
		//遅刻時間の計算   (最低勤務時間　－　パラメータで受け取った就業時間)
		AttendanceTime result = coreTimeSetting.get().getMinWorkTime().minusMinutes(workTime.valueAsMinutes());
		//計算結果がマイナスの場合は0
		if(result.valueAsMinutes()<0) {
			return new AttendanceTime(0);
		}
		return result;
	}
	
	/**
	 * コア無しフレックス遅刻時間の計算
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param deductionAtr 控除区分
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param autoCalcOfLeaveEarlySetting 遅刻早退の自動計算設定
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param dailyUnit 法定労働時間
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @param flexCalcMethod フレックス勤務の設定
	 * @return 遅刻時間
	 */
	public TimeWithCalculation calcNoCoreCalcLateTime(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			DeductionAtr deductionAtr,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSet,
			AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			DailyUnit dailyUnit,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			NotUseAtr lateEarlyMinusAtr,
			Optional<SettingOfFlexWork> flexCalcMethod){
		
		//遅刻時間の計算
		AttendanceTime lateTime = this.calcLateTime(
				integrationOfDaily,
				integrationOfWorkTime,
				deductionAtr,
				premiumAtr,
				vacationClass,
				workType,
				predetermineTimeSet,
				autoCalcOfLeaveEarlySetting,
				addSetting,
				holidayAddtionSet,
				dailyUnit,
				conditionItem,
				predetermineTimeSetByPersonInfo,
				lateEarlyMinusAtr,
				flexCalcMethod);
		//時間休暇との相殺処理(いずれ実装が必要)
		//丸め
//		if(commonSetting.isPresent()) {
//			val setting = commonSetting.get().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE).getRoundingSetByDedAtr(deductionAtr.isDeduction());
//			lateTime = new AttendanceTime(setting.round(lateTime.valueAsMinutes()));
//		}
//		
		//遅刻早退の自動計算設定．遅刻をチェック
		if(autoCalcOfLeaveEarlySetting.isLate()) {
			return TimeWithCalculation.sameTime(lateTime);
		}
		return TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(0), lateTime);
	}
	
	/**
	 * 遅刻時間の計算
	 * フレックスのコア無しの場合専用の遅刻時間の計算処理
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param deductionAtr 控除区分
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param autoCalcOfLeaveEarlySetting 遅刻早退の自動計算設定
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param dailyUnit 法定労働時間
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @param flexCalcMethod フレックス勤務の設定
	 * @return 遅刻時間
	 */
	public AttendanceTime calcLateTime(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			DeductionAtr deductionAtr,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSet,
			AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			DailyUnit dailyUnit,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			NotUseAtr lateEarlyMinusAtr,
			Optional<SettingOfFlexWork> flexCalcMethod) {
		
		// 就業時間帯の共通設定の確認
		Optional<WorkTimezoneCommonSet> commonSetting = Optional.empty();
		Optional<CoreTimeSetting> coreTimeSettingForCalc = Optional.empty();
		if (integrationOfWorkTime.isPresent()){
			commonSetting = Optional.of(integrationOfWorkTime.get().getCommonSetting());
			coreTimeSettingForCalc = integrationOfWorkTime.get().getCoreTimeSettingForCalc(Optional.of(workType));
		}
		//休暇の計算方法の設定を確認する
		HolidayCalcMethodSet holidayCalcMethodSet = addSetting.getVacationCalcMethodSet();
		// パラメータ「控除区分」＝”控除”　かつ　控除しない
		if (deductionAtr.isDeduction() &&
				holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().isPresent() &&
				!holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().isDeductLateLeaveEarly(commonSetting)) {
			return new AttendanceTime(0);
		}
		//コア無し計算遅刻時間
		AttendanceTime noCore = this.clacNoCoreWorkTime(
				integrationOfDaily,
				integrationOfWorkTime,
				premiumAtr,
				vacationClass,
				workType,
				predetermineTimeSet,
				autoCalcOfLeaveEarlySetting,
				addSetting,
				holidayAddtionSet,
				dailyUnit,
				conditionItem,
				predetermineTimeSetByPersonInfo,
				lateEarlyMinusAtr,
				flexCalcMethod);
		//遅刻時間の計算   (最低勤務時間　－　パラメータで受け取った就業時間)
		AttendanceTime result = coreTimeSettingForCalc.get().getMinWorkTime().minusMinutes(noCore.valueAsMinutes());
		//計算結果がマイナスの場合は0
		if(result.valueAsMinutes()<0) {
			return new AttendanceTime(0);
		}
		return result;
	}
	
	/**
	 * コアタイム無し遅刻時間計算用の就業時間の計算
	 * 遅刻時間の計算時にのみ利用する（就業時間計算時には利用しない）
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param autoCalcOfLeaveEarlySetting 遅刻早退の自動計算設定
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param dailyUnit 法定労働時間
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @param flexCalcMethod フレックス勤務の設定
	 * @return 就業時間
	 */
	public AttendanceTime clacNoCoreWorkTime(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSet,
			AutoCalcOfLeaveEarlySetting autoCalcOfLeaveEarlySetting,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			DailyUnit dailyUnit,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			NotUseAtr lateEarlyMinusAtr,
			Optional<SettingOfFlexWork> flexCalcMethod) {
		
		// 就業時間帯の共通設定の確認
		Optional<WorkTimezoneCommonSet> commonSetting = Optional.empty();
		if (integrationOfWorkTime.isPresent()){
			commonSetting = Optional.of(integrationOfWorkTime.get().getCommonSetting());
		}
		
		//遅刻、早退の控除設定を「控除する」に変更する
		AddSetting changeAddSet = addSetting.createNewDeductLateEarly();
		//休暇の計算方法の設定を確認する
		HolidayCalcMethodSet holidayCalcMethodSet = changeAddSet.getVacationCalcMethodSet();
		//就業時間帯の遅刻早退を控除するかを見る場合、就業時間帯の遅刻、早退の控除設定を「控除する」に変更する
		Optional<WorkTimezoneCommonSet> changeCommonSetting = commonSetting;
		if(holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().isPresent()&&
		   holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().getNotDeductLateLeaveEarly().isEnableSetPerWorkHour()&&
		   commonSetting.isPresent()) {
			 changeCommonSetting = Optional.of(commonSetting.get().changeWorkTimezoneLateEarlySet());
		}
		
		//就業時間（法定内用）の計算
		AttendanceTime result = this.calcWorkTime(
				integrationOfDaily,
				integrationOfWorkTime,
				premiumAtr,
				vacationClass,
				workType,
				predetermineTimeSet,
				autoCalcOfLeaveEarlySetting,
				changeAddSet,
				holidayAddtionSet,
				flexCalcMethod,
				dailyUnit,
				changeCommonSetting,
				conditionItem,
				predetermineTimeSetByPersonInfo,
				lateEarlyMinusAtr).getWorkTime();
		
		return result;
	}
		
	/**
	 * 就業時間内時間帯に入っている加給時間の計算
	 * アルゴリズム：加給時間の計算
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 加給時間(List)
	 */
	public List<BonusPayTime> calcBonusPayTimeInWithinWorkTime(List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily) {
		
		List<BonusPayTime> bonusPayList = new ArrayList<>();
		for(WithinWorkTimeFrame timeFrame : withinWorkTimeFrame) {
			bonusPayList.addAll(timeFrame.calcBonusPay(
					ActualWorkTimeSheetAtr.WithinWorkTime,
					bpTimeItemSets,
					calcAtrOfDaily));
		}
		//同じNo同士はここで加算し、Listのサイズを減らす
		return sumBonusPayTime(bonusPayList);
	}

	/**
	 * 就業時間内時間帯に入っている特定加給時間の計算
	 * アルゴリズム：加給時間の計算
	 * @param bpTimeItemSets 加給自動計算設定
	 * @param calcAtrOfDaily 日別実績の計算区分
	 * @return 特定加給時間(List)
	 */
	public List<BonusPayTime> calcSpecifiedBonusPayTimeInWithinWorkTime(List<BPTimeItemSetting> bpTimeItemSets, CalAttrOfDailyAttd calcAtrOfDaily) {
		
		List<BonusPayTime> bonusPayList = new ArrayList<>();
		for(WithinWorkTimeFrame timeFrame : withinWorkTimeFrame) {
			bonusPayList.addAll(timeFrame.calcSpacifiedBonusPay(
					ActualWorkTimeSheetAtr.WithinWorkTime,
					bpTimeItemSets,
					calcAtrOfDaily));
		}
		//同じNo同士はここで加算し、Listのサイズを減らす
		return sumBonusPayTime(bonusPayList);
	}
	
	/**
	 * 同じ加給時間Ｎｏを持つものを１つにまとめる
	 * @param bonusPayTime　加給時間
	 * @return　Noでユニークにした加給時間List
	 */
	private List<BonusPayTime> sumBonusPayTime(List<BonusPayTime> bonusPayTime){
		List<BonusPayTime> returnList = new ArrayList<>();
		List<BonusPayTime> refineList = new ArrayList<>();
		for(int bonusPayNo = 1 ; bonusPayNo <= 10 ; bonusPayNo++) {
			refineList = getByBonusPayNo(bonusPayTime, bonusPayNo);
			if(refineList.size()>0) {
				returnList.add(new BonusPayTime(bonusPayNo,
												new AttendanceTime(refineList.stream().map(tc -> tc.getBonusPayTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))),
												TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(refineList.stream().map(tc -> tc.getWithinBonusPay().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))),
																							  new AttendanceTime(refineList.stream().map(tc -> tc.getWithinBonusPay().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc)))),
												TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(refineList.stream().map(tc -> tc.getExcessBonusPayTime().getTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))),
																							  new AttendanceTime(refineList.stream().map(tc -> tc.getExcessBonusPayTime().getCalcTime().valueAsMinutes()).collect(Collectors.summingInt(tc -> tc))))
												));
			}
		}
		return returnList;
	}
	
	/**
	 * 受け取った加給時間Ｎｏを持つ加給時間を取得
	 * @param bonusPayTime 加給時間
	 * @param bonusPayNo　加給時間Ｎｏ
	 * @return　加給時間リスト
	 */
	private List<BonusPayTime> getByBonusPayNo(List<BonusPayTime> bonusPayTime,int bonusPayNo){
		return bonusPayTime.stream().filter(tc -> tc.getBonusPayTimeItemNo() == bonusPayNo).collect(Collectors.toList());
	}
	
	/**
	 * 法定内深夜時間の計算
	 * @return　法定内深夜時間
	 */
	public AttendanceTime calcMidNightTime() {
		int totalMidNightTime = 0;
		totalMidNightTime = withinWorkTimeFrame.stream()
											   .map(ts -> ts.getMidNightTimeSheet().calcTotalTime().v())
											   .collect(Collectors.summingInt(tc -> tc));
		
		return new AttendanceTime(totalMidNightTime);
	}
	
	/**
	 * 控除時間を取得
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @param roundAtr 丸め区分
	 * @return 控除時間
	 */
	public AttendanceTime getDeductionTime(
			ConditionAtr conditionAtr, DeductionAtr dedAtr, TimeSheetRoundingAtr roundAtr, NotUseAtr canOffset) {
		
		return ActualWorkTimeSheetListService.calcDeductionTime(conditionAtr, dedAtr, roundAtr,
				this.withinWorkTimeFrame.stream().map(tc -> (ActualWorkingTimeSheet)tc).collect(Collectors.toList()), canOffset);
	}
	
	/**
	 * 控除回数の計算
	 * @param conditionAtr 控除種別区分
	 * @param dedAtr 控除区分
	 * @return 控除回数
	 */
	public int calcDeductionCount(ConditionAtr conditionAtr, DeductionAtr dedAtr) {
		
		return ActualWorkTimeSheetListService.calcDeductionCount(conditionAtr, dedAtr,
				this.withinWorkTimeFrame.stream().map(tc -> (ActualWorkingTimeSheet)tc).collect(Collectors.toList()));
	}
	
	/**
	 * 休憩未取得用の処理(大塚要件)
	 * 就業時間内時間帯に含まれている休憩時間を計算する
	 * @param restTimeSheet　就業時間帯マスタの休憩時間帯
	 * @return　取得した休憩時間
	 */
	public AttendanceTime getDupRestTime(DeductionTime restTimeSheet) {
		List<TimeSpanForDailyCalc> notDupSpanList = Arrays.asList(new TimeSpanForDailyCalc(restTimeSheet.getStart(), restTimeSheet.getEnd()));
		for(WithinWorkTimeFrame frame : this.getWithinWorkTimeFrame()) {
			notDupSpanList = notDupSpanList.stream()
										   .filter(tc -> tc.getDuplicatedWith(frame.getTimeSheet()).isPresent())
										   .map(tc -> tc.getDuplicatedWith(frame.getTimeSheet()).get())
										   .collect(Collectors.toList());
		}
		return new AttendanceTime(notDupSpanList.stream().map(ts -> ts.lengthAsMinutes()).collect(Collectors.summingInt(tc -> tc)));
	}
	/**
	 * 大塚モード使用時専用の遅刻、早退削除処理
	 */
	public void cleanLateLeaveEarlyTimeForOOtsuka() {
		this.withinWorkTimeFrame.forEach(tc -> tc.cleanLateLeaveEarlyTimeForOOtsuka());
		cleanLateTime();
		cleanLeaveEarly();
	}
	
	private void cleanLateTime() {
		this.lateDecisionClock = Collections.emptyList();
	}
	
	private void cleanLeaveEarly() {
		this.leaveEarlyDecisionClock = Collections.emptyList();
	}

	/**
	 * 大塚所定内割増→残業への変換後に所定内割増リセット
	 */
	public void resetPremiumTimeSheet() {
		this.withinWorkTimeFrame.forEach(tc -> {
			tc.setPremiumTimeSheetInPredetermined(Optional.empty());
		});
	}
	
	 /**
	 * 遅刻控除
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param deductionTimeSheet 控除時間帯
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param timeLeavingWork 出退勤
	 * @param predetermineTimeSet 所定時間設定(計算用クラス)
	 * @return 出退勤
	 */
	public TimeLeavingWork calcLateTimeDeduction(
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			DeductionTimeSheet deductionTimeSheet,
			HolidayCalcMethodSet holidayCalcMethodSet,
			TimeLeavingWork timeLeavingWork,
			PredetermineTimeSetForCalc predetermineTimeSet){

		int workNo = timeLeavingWork.getWorkNo().v();
		
		//遅刻判断時刻を取得
		Optional<LateDecisionClock> lateDecisionClock = LateDecisionClock.create(
				workNo,
				predetermineTimeSet,
				integrationOfWorkTime,
				integrationOfDaily,
				timeLeavingWork,
				todayWorkType,
				deductionTimeSheet);
		
		if(lateDecisionClock.isPresent())
			this.lateDecisionClock.add(lateDecisionClock.get());
		
		val within = getWithinWorkTimeFrame(workNo);
		
		if(within == null) return timeLeavingWork; 
		
		//遅刻時間帯の作成
		within.createLateTimeSheet(
				lateDecisionClock,
				timeLeavingWork,
				integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE),
				within,
				deductionTimeSheet,
				predetermineTimeSet.getTimeSheets(todayWorkType.getDailyWork().decisionNeedPredTime(), workNo),
				workNo,
				todayWorkType,
				predetermineTimeSet,
				integrationOfWorkTime,
				integrationOfDaily);
		
		if(!within.getLateTimeSheet().isPresent() || !within.getLateTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
			return timeLeavingWork;
		}
		//遅刻控除時間帯
		LateLeaveEarlyTimeSheet lateDeducation = within.getLateTimeSheet().get().getForDeducationTimeSheet().get();
		
		//控除判断処理
		boolean isDeductLateTime = holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().decisionLateDeductSetting(
				lateDeducation.calcTotalTime(),
				integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE).getGraceTimeSet(),
				integrationOfWorkTime.getCommonSetting(),
				todayWorkType);

		//控除する場合
		if(isDeductLateTime && timeLeavingWork.getStampOfAttendance().isPresent()){
			//出退勤．出勤 ← 遅刻時間帯終了時刻
			timeLeavingWork.getStampOfAttendance().get().getTimeDay().setTimeWithDay(
					Optional.of(lateDeducation.getAfterRoundingAsLate().getEnd()));
			
			//時間帯．開始 ← 遅刻時間帯終了時刻
			this.withinWorkTimeFrame.stream()
					.filter(c -> c.getWorkingHoursTimeNo().v() == workNo)
					.findFirst().ifPresent(c -> c.shiftStart(lateDeducation.getAfterRoundingAsLate().getEnd()));
		}
		return timeLeavingWork;
	}


	private WithinWorkTimeFrame getWithinWorkTimeFrame(int workNo) {
		return this.withinWorkTimeFrame.stream().filter(c -> c.getWorkingHoursTimeNo().v() == workNo)
				.findFirst().orElse(null);
	}
	
	 /**
	 * 早退控除
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param deductionTimeSheet 控除時間帯
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param timeLeavingWork 出退勤
	 * @param predetermineTimeSet 所定時間設定(計算用クラス)
	 * @return 出退勤
	 */
	public TimeLeavingWork calcLeaveEarlyTimeDeduction(
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			DeductionTimeSheet deductionTimeSheet,
			HolidayCalcMethodSet holidayCalcMethodSet,
			TimeLeavingWork timeLeavingWork,
			PredetermineTimeSetForCalc predetermineTimeSet){
	
		int workNo = timeLeavingWork.getWorkNo().v();
		
		//早退判断時刻を取得
		Optional<LeaveEarlyDecisionClock> leaveEarlyDecisionClock = LeaveEarlyDecisionClock.create(
				workNo,
				predetermineTimeSet,
				integrationOfWorkTime,
				integrationOfDaily,
				timeLeavingWork,
				todayWorkType,
				deductionTimeSheet);
		
		if(leaveEarlyDecisionClock.isPresent())
			this.leaveEarlyDecisionClock.add(leaveEarlyDecisionClock.get());
		
		val within = getWithinWorkTimeFrame(workNo);
		
		if(within == null) return timeLeavingWork; 
		
		//早退時間帯の作成
		within.createLeaveEarlyTimeSheet(
				leaveEarlyDecisionClock,
				timeLeavingWork,
				integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.EARLY),
				within,
				deductionTimeSheet,
				predetermineTimeSet.getTimeSheets(todayWorkType.getDailyWork().decisionNeedPredTime(), workNo),
				workNo,
				todayWorkType,
				predetermineTimeSet,
				integrationOfWorkTime,
				integrationOfDaily);
		
		if(!within.getLeaveEarlyTimeSheet().isPresent() || !within.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().isPresent()) {
			return timeLeavingWork;
		}
		//早退控除時間帯
		LateLeaveEarlyTimeSheet leaveEarlyDeducation = within.getLeaveEarlyTimeSheet().get().getForDeducationTimeSheet().get();
		
		//控除判断処理
		boolean isDeductLateTime = holidayCalcMethodSet.getWorkTimeCalcMethodOfHoliday().getAdvancedSet().get().decisionLateDeductSetting(
				AttendanceTime.ZERO,
				integrationOfWorkTime.getCommonSetting().getLateEarlySet().getOtherEmTimezoneLateEarlySet(LateEarlyAtr.EARLY).getGraceTimeSet(),
				integrationOfWorkTime.getCommonSetting(),
				todayWorkType);
		
		//控除する場合
		if(isDeductLateTime && timeLeavingWork.getStampOfLeave().isPresent()){
			//出退勤．退勤 ← 早退時間帯開始時刻 
			timeLeavingWork.getStampOfLeave().get().getTimeDay().setTimeWithDay(
				 Optional.of(leaveEarlyDeducation.getAfterRoundingAsLeaveEarly().getStart()));
			
			//時間帯．終了 ← 早退時間帯開始時刻
			this.withinWorkTimeFrame.stream()
					.filter(c -> c.getWorkingHoursTimeNo().v() == workNo)
					.findFirst().ifPresent(c -> c.shiftEnd(leaveEarlyDeducation.getAfterRoundingAsLeaveEarly().getStart()));
		}
		return timeLeavingWork;
	}
	
	/**
	 * 流動勤務(平日・就内)
	 * @param companyCommonSetting 会社別設定管理
	 * @param personDailySetting 社員設定管理
	 * @param todayWorkType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param deductionTimeSheet 控除時間帯
	 * @param creatingWithinWorkTimeSheet 就業時間内時間帯（遅刻早退を事前に求めた結果が入っている）
	 * @param timeVacationWork 時間休暇WORK
	 * @param timeLeavingOfDaily 日別勤怠の出退勤
	 * @return 就業時間内時間帯
	 */
	public static WithinWorkTimeSheet createAsFlow(
			ManagePerCompanySet companyCommonSetting,
			ManagePerPersonDailySet personDailySetting,
			WorkType todayWorkType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			PredetermineTimeSetForCalc predetermineTimeSet,
			DeductionTimeSheet deductionTimeSheet,
			WithinWorkTimeSheet creatingWithinWorkTimeSheet,
			TimeVacationWork timeVacationWork,
			TimeLeavingOfDailyAttd timeLeavingOfDaily) {
		
		// 1回目の開始
		Optional<TimeWithDayAttr> startTime = timeLeavingOfDaily.getAttendanceLeavingWork(new WorkNo(1)).flatMap(t -> t.getAttendanceTime());
		if(!startTime.isPresent()) {
			return creatingWithinWorkTimeSheet;
		}
		// 就業時間内時間帯を作成
		creatingWithinWorkTimeSheet.createWithinWorkTimeSheetAsFlowWork(
				personDailySetting,
				todayWorkType,
				integrationOfWorkTime,
				integrationOfDaily,
				startTime.get(),
				deductionTimeSheet,
				integrationOfWorkTime.getFlowWorkSetting().get(),
				predetermineTimeSet,
				companyCommonSetting.getHolidayAdditionPerCompany());
		// 控除時間帯の登録
		creatingWithinWorkTimeSheet.getWithinWorkTimeFrame().get(0).registDeductionListForWithin(
				deductionTimeSheet, Optional.of(integrationOfWorkTime.getCommonSetting()));
		// 相殺前の時間休暇WORKをセット
		creatingWithinWorkTimeSheet.timeVacationUseTime = timeVacationWork.clone();
		// 控除時間中の時間休暇相殺時間の計算
		creatingWithinWorkTimeSheet.calcTimeVacationOffsetTime(
				new CompanyHolidayPriorityOrder(integrationOfWorkTime.getWorkTimeSetting().getCompanyId()),
				timeVacationWork);
		// 加給時間帯を作成
		creatingWithinWorkTimeSheet.getWithinWorkTimeFrame().get(0).createBonusPayTimeSheet(
				personDailySetting.getBonusPaySetting(),
				integrationOfDaily.getSpecDateAttr(),
				deductionTimeSheet);
		// 深夜時間帯を作成
		creatingWithinWorkTimeSheet.getWithinWorkTimeFrame().get(0).createMidNightTimeSheet(
				companyCommonSetting.getMidNightTimeSheet(),
				Optional.of(integrationOfWorkTime.getCommonSetting()),
				deductionTimeSheet);
		
		return creatingWithinWorkTimeSheet;
	}
	
	/**
	 * 就業時間内時間帯を作成
	 * @param personDailySetting 社員設定管理
	 * @param workType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param startTime 開始時刻
	 * @param deductionTimeSheet 控除時間帯
	 * @param flowWorkSetting 流動勤務設定
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param holidayAdditionSet 休暇加算時間設定
	 */
	private void createWithinWorkTimeSheetAsFlowWork(
			ManagePerPersonDailySet personDailySetting,
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimeWithDayAttr startTime,
			DeductionTimeSheet deductionTimeSheet,
			FlowWorkSetting flowWorkSetting,
			PredetermineTimeSetForCalc predetermineTimeSet,
			Optional<HolidayAddtionSet> holidayAdditionSet) {
		
		TimeWithDayAttr endTime;	// 終了時刻
		
		if (flowWorkSetting.getHalfDayWorkTimezoneLstOTTimezone().isEmpty()){
			// 1日の計算範囲から終了時刻を計算
			endTime = predetermineTimeSet.getOneDayTimeSpan().getEnd();
		}
		else{
			// 残業開始となる経過時間を取得
			AttendanceTime elapsedTime = flowWorkSetting.getHalfDayWorkTimezoneLstOTTimezone().get(0).getFlowTimeSetting().getElapsedTime();
			// 休暇加算時間を計算する
			VacationAddTime vacationAddTime = VacationClass.createAllZero().calcVacationAddTime(
					PremiumAtr.RegularWork,
					workType,
					Optional.of(integrationOfWorkTime.getCode()),
					personDailySetting.getPersonInfo(),
					personDailySetting.getAddSetting(),
					holidayAdditionSet,
					Optional.of(predetermineTimeSet),
					Optional.of(personDailySetting.getPredetermineTimeSetByPersonWeekDay()));
			int totalVacationAddMinutes = vacationAddTime.calcTotaladdVacationAddTime();
			// 時間休暇加算時間を計算する
			AttendanceTime timeVacationAddTime = holidayAdditionSet.map(h -> h.calcTimeVacationAddTime(
					integrationOfDaily,
					deductionTimeSheet,
					personDailySetting.getAddSetting().getVacationCalcMethodSet(),
					this.withinWorkTimeFrame,
					integrationOfWorkTime.getWorkTimeSetting().getWorkTimeDivision().getWorkTimeForm())).orElse(AttendanceTime.ZERO);
			// 経過時間を計算する
			int elapsedMinutes =
					elapsedTime.valueAsMinutes() - totalVacationAddMinutes - timeVacationAddTime.valueAsMinutes();
			// 経過時間から終了時刻を計算
			endTime = this.withinWorkTimeFrame.get(0).getTimeSheet().getStart().forwardByMinutes(elapsedMinutes);
			// 上限実働就業時間をセット
			this.limitActualWorkTime = Optional.empty();
			
			// 控除時間帯と重複している分、退勤時刻を後ろにずらす
			endTime = new TimeSpanForDailyCalc(startTime, endTime).forwardByDeductionTime(deductionTimeSheet.getForDeductionTimeZoneList());
			
		}
		// 残業開始時刻
		TimeWithDayAttr startOverTime = endTime;
		// 退勤時刻の補正
		endTime = this.correctleaveTimeForFlow(endTime);
		// 退勤時刻を更新する
		this.updateleaveTime(endTime);
		// 遅刻早退控除前時間帯を作成
		this.setBeforeLateEarlyTimeSheet(workType, integrationOfWorkTime, predetermineTimeSet, integrationOfDaily.getAttendanceLeave(), startOverTime);
	}
	
	/**
	 * 控除時間中の時間休暇相殺時間の計算
	 * @param priorityOrder 時間休暇相殺優先順位
	 * @param timeVacationWork 時間休暇WORK
	 */
	private void calcTimeVacationOffsetTime(
			CompanyHolidayPriorityOrder priorityOrder,
			TimeVacationWork timeVacationWork) {
		
		for (WithinWorkTimeFrame frame : this.withinWorkTimeFrame){
			// 控除時間帯の相殺時間を計算
			frame.calcOffsetTimeOfDeductTimeSheet(frame.getWorkNo().v(), timeVacationWork,
					new CompanyHolidayPriorityOrder(AppContexts.user().companyId()));
		}
	}

	/**
	 * まだ加算していない時間休暇を計算する
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param flexCalcMethod フレックス勤務の設定
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param dailyUnit 法定労働時間
	 * @param commonSetting 就業時間帯の共通設定
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @return 未相殺の時間
	 */
	public AttendanceTime calcNotAddedTimeVacationTime(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			Optional<SettingOfFlexWork> flexCalcMethod,
			PredetermineTimeSetForCalc predetermineTimeSet,
			DailyUnit dailyUnit,
			Optional<WorkTimezoneCommonSet> commonSetting,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			NotUseAtr lateEarlyMinusAtr) {

		// 休暇加算するかどうか判断
		if (addSetting.getNotUseAtr(premiumAtr) == NotUseAtr.NOT_USE) return AttendanceTime.ZERO;
		// 統合就業時間帯の確認
		Optional<WorkTimeForm> workTimeForm = Optional.empty();					// 就業時間帯の勤務形態
		if (integrationOfWorkTime.isPresent()){
			WorkTimeSetting workTimeSet = integrationOfWorkTime.get().getWorkTimeSetting();	// 就業時間帯の設定
			workTimeForm = Optional.of(workTimeSet.getWorkTimeDivision().getWorkTimeForm());
		}
		// 既に加算している合計時間
		AttendanceTime addedTime = this.getTotalAddTimeByOffset(
				integrationOfDaily, integrationOfWorkTime, premiumAtr, vacationClass,
				workType, addSetting, holidayAddtionSet,
				flexCalcMethod, predetermineTimeSet, dailyUnit, commonSetting,
				conditionItem, predetermineTimeSetByPersonInfo, lateEarlyMinusAtr);
		// 休暇の計算方法の設定を確認する
		HolidayCalcMethodSet holidayCalcMethodSet = addSetting.getVacationCalcMethodSet();
		// 相殺合計時間
		AttendanceTime totalOffsetTime = this.getTotalOffsetTimeForHolidayAdd(
				integrationOfWorkTime, premiumAtr, commonSetting, holidayAddtionSet, holidayCalcMethodSet,
				lateEarlyMinusAtr);
		// 時間休暇加算設定
		Optional<TimeHolidayAdditionSet> timeHolidayAddSet = Optional.empty();
		if (workTimeForm.isPresent()){
			timeHolidayAddSet = holidayAddtionSet.getTimeHolidayAdditionSet(workTimeForm.get());
		}
		// 未加算時間
		AttendanceTime notAddedTime = AttendanceTime.ZERO;
		if (timeHolidayAddSet.isPresent()){
			// 加算する時間を判断する(全加算用のまだ加算していない時間,相殺分加算用のまだ加算していない時間)
			notAddedTime = timeHolidayAddSet.get().getAddTime(
					this.timeVacationUseTime.getValueForAddWorkTime(
							integrationOfWorkTime, premiumAtr, commonSetting, holidayAddtionSet,
							holidayCalcMethodSet, this.getTotalOffsetTime(), lateEarlyMinusAtr)
						.total().minusMinutes(addedTime.valueAsMinutes()),
					totalOffsetTime.minusMinutes(addedTime.valueAsMinutes()));
		}
		else{
			// 相殺分加算用のまだ加算していない時間
			notAddedTime = totalOffsetTime.minusMinutes(addedTime.valueAsMinutes());
		}
		// まだ加算していない時間休暇の丸め設定を取得する
		Optional<TimeRoundingSetting> roundSet = this.getRoundingSetForNotAddedTime();
		if (roundSet.isPresent()){
			notAddedTime = new AttendanceTime(roundSet.get().round(notAddedTime.valueAsMinutes()));
		}
		// 未加算時間を返す
		return notAddedTime;
	}
	
	/**
	 * 休暇加算対象となる相殺時間の合計を取得する
	 * @return 相殺合計時間(時間休暇WORK)
	 */
	private TimeVacationWork getTotalOffsetTime(){
		TimeVacationWork result = new TimeVacationWork();	// 相殺合計時間
		for (WithinWorkTimeFrame frame : this.withinWorkTimeFrame){
			// 時間休暇相殺時間を取得　→　相殺合計時間に加算
			result = result.add(frame.getTimeVacationOffsetTime());
		}
		return result;
	}
	
	/**
	 * 休暇加算対象となる相殺時間の合計を取得する
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param commonSetting 就業時間帯の共通設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param holidayCalcMethodSet 休暇の計算方法の設定
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @return 相殺合計時間
	 */
	private AttendanceTime getTotalOffsetTimeForHolidayAdd(
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			Optional<WorkTimezoneCommonSet> commonSetting,
			HolidayAddtionSet holidayAddtionSet,
			HolidayCalcMethodSet holidayCalcMethodSet,
			NotUseAtr lateEarlyMinusAtr){
		
		int result = 0;
		for (WithinWorkTimeFrame frame : this.withinWorkTimeFrame){
			// 時間休暇相殺時間を取得．就業時間に加算する時間のみ取得．合計
			AttendanceTime frameTotal =
					frame.getTimeVacationOffsetTime().getValueForAddWorkTime(
							integrationOfWorkTime, premiumAtr, commonSetting, holidayAddtionSet, holidayCalcMethodSet,
							frame.getTimeVacationOffsetTime(), lateEarlyMinusAtr).total();
			result += frameTotal.valueAsMinutes();
		}
		return new AttendanceTime(result);
	}
	
	/**
	 * 時間枠毎の相殺による加算時間の合計を取得
	 * @param integrationOfDaily 日別実績(WORK)
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param premiumAtr 割増区分
	 * @param vacationClass 休暇クラス
	 * @param workType 勤務種類
	 * @param addSetting 加算設定
	 * @param holidayAddtionSet 休暇加算時間設定
	 * @param flexCalcMethod フレックス勤務の設定
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param dailyUnit 法定労働時間
	 * @param commonSetting 就業時間帯の共通設定
	 * @param conditionItem 労働条件項目
	 * @param predetermineTimeSetByPersonInfo 計算用所定時間設定（個人）
	 * @param lateEarlyMinusAtr 強制的に遅刻早退控除する
	 * @return 加算合計時間
	 */
	public AttendanceTime getTotalAddTimeByOffset(
			IntegrationOfDaily integrationOfDaily,
			Optional<IntegrationOfWorkTime> integrationOfWorkTime,
			PremiumAtr premiumAtr,
			VacationClass vacationClass,
			WorkType workType,
			AddSetting addSetting,
			HolidayAddtionSet holidayAddtionSet,
			Optional<SettingOfFlexWork> flexCalcMethod,
			PredetermineTimeSetForCalc predetermineTimeSet,
			DailyUnit dailyUnit,
			Optional<WorkTimezoneCommonSet> commonSetting,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			NotUseAtr lateEarlyMinusAtr) {

		// 休暇加算するかどうか判断
		if (addSetting.getNotUseAtr(premiumAtr) == NotUseAtr.NOT_USE) return AttendanceTime.ZERO;
		int totalAddMinutes = 0;
		for (WithinWorkTimeFrame frame : this.withinWorkTimeFrame){
			// 就業時間に加算する時間休暇相殺時間を取得
			AttendanceTime addTime = frame.getTimeVacationOffsetTimeForAddWorkTime(
					integrationOfDaily, integrationOfWorkTime, addSetting, holidayAddtionSet);
			totalAddMinutes += addTime.valueAsMinutes();
		}
		return new AttendanceTime(totalAddMinutes);
	}
	
	/**
	 * まだ加算していない時間休暇の丸め設定を取得する
	 * @return 時間丸め設定
	 */
	private Optional<TimeRoundingSetting> getRoundingSetForNotAddedTime(){
		return this.withinWorkTimeFrame.stream()
				.sorted((a, b) -> a.getTimeSheet().getStart().compareTo(b.getTimeSheet().getStart()))
				.map(c -> c.getRounding()).findFirst();
	}
	
	/**
	 * 退勤時刻の補正（流動勤務で経過時間より退勤時刻が早い場合に補正する）
	 * @param endTime 就業時間内時間帯終了時刻
	 * @return 補正後の退勤時刻
	 */
	private TimeWithDayAttr correctleaveTimeForFlow(TimeWithDayAttr endTime){
		
		TimeWithDayAttr leaveTime = TimeWithDayAttr.THE_PRESENT_DAY_0000;
		
		//退勤時刻を求める
		//2勤務目がある場合、2勤務目の退勤時刻
		if(this.withinWorkTimeFrame.size() >= 2) {
			leaveTime = this.withinWorkTimeFrame.stream()
					.sorted((c1, c2) -> c2.getWorkingHoursTimeNo().compareTo(c1.getWorkingHoursTimeNo()))
					.findFirst().get().getTimeSheet().getEnd();
		}
		//1勤務のみの場合、1勤務目の退勤時刻
		else {
			leaveTime = this.withinWorkTimeFrame.stream()
					.sorted((c1, c2) -> c1.getWorkingHoursTimeNo().compareTo(c2.getWorkingHoursTimeNo()))
					.findFirst().get().getTimeSheet().getEnd();
		}
		//就業時間内時間帯終了時刻←退勤時刻
		if(leaveTime.lessThan(endTime)) {
			endTime = endTime.backByMinutes(endTime.valueAsMinutes() - leaveTime.valueAsMinutes());
		}
		return endTime;
	}
	
	/**
	 * 指定時刻を含む就業時間内時間枠の終了時刻を更新する
	 * @param endTime 指定時刻
	 */
	private void updateleaveTime(TimeWithDayAttr endTime) {
		List<WithinWorkTimeFrame> frames = new ArrayList<WithinWorkTimeFrame>();
		
		for(WithinWorkTimeFrame frame : this.withinWorkTimeFrame) {
			//指定時刻が時間帯に含まれているか判断
			if(frame.getTimeSheet().contains(endTime)) {
				frame.shiftEnd(endTime);
				frames.add(frame);
				break;
			}
			frames.add(frame);
		}
		this.withinWorkTimeFrame.clear();
		this.withinWorkTimeFrame.addAll(frames);
	}
	
	/**
	 * 就業時間内時間枠(List)の最初の開始時刻～最後の終了時刻を求める
	 * @return 最初の開始時刻～最後の終了時刻
	 */
	public Optional<TimeSpanForDailyCalc> getFirstStartAndLastEnd() {
		Optional<TimeWithDayAttr> start = this.withinWorkTimeFrame.stream()
				.map(t -> t.getTimeSheet().getStart())
				.sorted((f, s) -> f.compareTo(s))
				.findFirst();
		Optional<TimeWithDayAttr> end = this.withinWorkTimeFrame.stream()
				.map(t -> t.getTimeSheet().getEnd())
				.sorted((f, s) -> s.compareTo(f))
				.findFirst();
		if(!start.isPresent() || !end.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new TimeSpanForDailyCalc(start.get(), end.get()));
	}
	
	/**
	 * 遅刻早退控除前時間帯を作成する
	 * @param workType 勤務種類
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param predetermineTimeSet 計算用所定時間設定
	 * @param timeLeavingOfDailyAttd 日別勤怠の出退勤
	 * @param startOverTime 残業開始時刻
	 */
	public void setBeforeLateEarlyTimeSheet(
			WorkType workType,
			IntegrationOfWorkTime integrationOfWorkTime,
			PredetermineTimeSetForCalc predetermineTimeSet,
			Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyAttd,
			TimeWithDayAttr startOverTime) {
		this.withinWorkTimeFrame.forEach(
				f -> f.setBeforeLateEarlyTimeSheet(workType, integrationOfWorkTime, predetermineTimeSet, timeLeavingOfDailyAttd, startOverTime));
	}
	
	/**
	 * 所定内割増時間の計算（就業時間の計算内部で呼ぶ処理）
	 * @param workType 勤務種類
	 * @param dailyUnit 法定労働時間
	 * @param predetermineTimeSet 所定時間設定
	 * @param workTime 就業時間
	 * @return 所定内割増時間
	 */
	private AttendanceTime calcPredeterminePremiumTime(
			WorkType workType,
			DailyUnit dailyUnit,
			PredetermineTimeSetForCalc predetermineTimeSet,
			AttendanceTime workTime) {
		if(dailyUnit.getDailyTime().lessThanOrEqualTo(0))
			return AttendanceTime.ZERO;
		
		//所定労働時間
		AttendanceTime predetermineTime = predetermineTimeSet.getpredetermineTime(workType.getDailyWork());
		if(predetermineTime.lessThanOrEqualTo(dailyUnit.getDailyTime().valueAsMinutes()))
			return AttendanceTime.ZERO;
		
		return workTime.minusMinutes(dailyUnit.getDailyTime().valueAsMinutes());
	}
	
	/**
	 * 遅刻早退控除前時間帯に含まない時間帯の取得
	 * @param timeSpan 計算用時間帯
	 * @return 計算用時間帯List
	 */
	public List<TimeSpanForCalc> getTimeSheetNotDupBeforeLateEarly(TimeSpanForCalc timeSpan){
		
		List<TimeSpanForCalc> target = new ArrayList<>();	// 比較対象List
		// 就業時間内時間枠を確認する
		for (WithinWorkTimeFrame frame : this.withinWorkTimeFrame){
			// 比較対象Listに追加する
			target.add(frame.getBeforeLateEarlyTimeSheet().getTimeSpan());
		}
		// 指定Listと重複していない時間帯の取得
		List<TimeSpanForCalc> results = timeSpan.getNotDuplicatedWith(target);
		// 結果を返す
		return results;
	}
}