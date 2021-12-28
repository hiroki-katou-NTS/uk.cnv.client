package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.holidaypriorityorder.CompanyHolidayPriorityOrder;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.ootsuka.OotsukaStaticService;
import nts.uk.ctx.at.shared.dom.worktime.IntegrationOfWorkTime;
import nts.uk.ctx.at.shared.dom.worktime.common.OtherEmTimezoneLateEarlySet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 遅刻時間帯
 * @author keisuke_hoshina
 */
@AllArgsConstructor
@Getter
public class LateTimeSheet {
	
	// 遅刻していない場合はempty
	//計上用時間帯
	private Optional<LateLeaveEarlyTimeSheet> forRecordTimeSheet;
	
	//控除用時間帯
	private Optional<LateLeaveEarlyTimeSheet> forDeducationTimeSheet;
	
	//勤務No
	private int workNo;
	
	//コアなしフレックス遅刻時間
	private Optional<AttendanceTime> noCoreFlexLateTime = Optional.empty();
	
	public LateTimeSheet(
			Optional<LateLeaveEarlyTimeSheet> recordTimeSheet,
			Optional<LateLeaveEarlyTimeSheet> deductionTimeSheet,
			int workNo) {
		
		this.forRecordTimeSheet = recordTimeSheet;
		this.forDeducationTimeSheet = deductionTimeSheet;
		this.workNo = workNo;
	}
	
	/**
	 * 指定された区分の時間帯を返す
	 * @param dedAtr
	 * @return
	 */
	public Optional<LateLeaveEarlyTimeSheet> getDecitionTimeSheet(DeductionAtr dedAtr){
		if(dedAtr.isAppropriate()) {
			return this.forRecordTimeSheet;
		}
		return this.forDeducationTimeSheet;
	}
	
	/**
	 * 指定された区分の時間帯に入れる
	 * @param dedAtr
	 * @return
	 */
	public void setDecitionTimeSheet(DeductionAtr dedAtr, Optional<LateLeaveEarlyTimeSheet> forTimeSheet){
		if(dedAtr.isAppropriate()) {
			this.forRecordTimeSheet = forTimeSheet;
		}
		this.forDeducationTimeSheet = forTimeSheet;
	}
	
	/**
	 * 遅刻時間帯の作成
	 * @param lateDesClock 遅刻判断時刻
	 * @param timeLeavingWork 出退勤
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTimeSet 所定時間設定
	 * @param workNo 勤務NO
	 * @param workType 勤務種類
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @return 遅刻時間帯
	 */
	public static Optional<LateTimeSheet> createLateTimeSheet(
			Optional<LateDecisionClock> lateDesClock,
			TimeLeavingWork timeLeavingWork,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductionTimeSheet,
			Optional<TimezoneUse> predetermineTimeSet,
			int workNo,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		
		//出勤時刻
		Optional<TimeWithDayAttr> attendance = timeLeavingWork.getAttendanceTime();
		if(!attendance.isPresent() || !lateDesClock.isPresent() || !predetermineTimeSet.isPresent()) {
			return Optional.empty();
		}
		
		//遅刻時間帯
		Optional<LateLeaveEarlyTimeSheet> lateAppTimeSheet = Optional.empty();
		//遅刻控除時間帯
		Optional<LateLeaveEarlyTimeSheet> lateDeductTimeSheet = Optional.empty();
		
		//出勤時刻と遅刻判断時刻を比較	
		if(lateDesClock.get().getLateDecisionClock().lessThan(attendance.get())) {
			//遅刻控除時間帯の作成
			lateDeductTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Deduction,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTimeSet.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
			//遅刻時間帯の作成
			lateAppTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Appropriate,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTimeSet.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
		}else {
			//遅刻控除時間帯の作成
			lateDeductTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Deduction,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTimeSet.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
		}
		if(!lateAppTimeSheet.isPresent() && !lateDeductTimeSheet.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new LateTimeSheet(lateAppTimeSheet, lateDeductTimeSheet, workNo));
	}
	
	/**
	 * 作成処理
	 * @param deductionAtr 控除区分
	 * @param timeLeavingWork 出退勤
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @param predetermineTimeSet 所定時間帯（使用区分付き）
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param workType 勤務種類
	 * @param predetermineTimeForSet 所定時間設定
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @return 遅刻早退時間帯
	 */
	private static Optional<LateLeaveEarlyTimeSheet> createLateLeaveEarlyTimeSheet(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily,
			TimezoneUse predetermineTimeSet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductionTimeSheet,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeForSet,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){

		// 遅刻早退用控除時間帯
		DeductionTimeSheet deductForLateEarly = new DeductionTimeSheet(
				new ArrayList<>(deductionTimeSheet.getForDeductionTimeZoneList()),
				new ArrayList<>(deductionTimeSheet.getForRecordTimeZoneList()),
				deductionTimeSheet.getBreakTimeOfDailyList(),
				deductionTimeSheet.getDailyGoOutSheet(),
				deductionTimeSheet.getShortTimeSheets());
		// 大塚モードの休憩時間帯取得
		List<TimeSheetOfDeductionItem> ootsukaBreak = OotsukaStaticService.getBreakTimeSheet(
				workType, integrationOfWorkTime, integrationOfDaily.getAttendanceLeave());
		deductForLateEarly.getForDeductionTimeZoneList().addAll(ootsukaBreak);
		deductForLateEarly.getForRecordTimeZoneList().addAll(ootsukaBreak);
		// 遅刻時間帯を作成する
		Optional<LateLeaveEarlyTimeSheet> instance = createLateTimeSheetInstance(
				deductionAtr,
				timeLeavingWork,
				integrationOfWorkTime,
				predetermineTimeSet,
				withinWorkTimeFrame,
				deductForLateEarly,
				workType,
				predetermineTimeForSet,
				otherEmTimezoneLateEarlySet);
		
		return instance;
	}
	
	/**
	 * 遅刻時間帯を作成する
	 * @param deductionAtr 控除区分
	 * @param timeLeavingWork 出退勤
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param predetermineTimeSet 所定時間帯（使用区分付き）
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param deductTimeSheet 遅刻早退用控除時間帯
	 * @param workType 勤務種類
	 * @param predetermineTimeForSet 所定時間設定
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @return 遅刻早退時間帯
	 */
	private static Optional<LateLeaveEarlyTimeSheet> createLateTimeSheetInstance(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			TimezoneUse predetermineTimeSet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductTimeSheet,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeForSet,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){

		// 遅刻開始時刻を取得
		Optional<TimeSpanForDailyCalc> calcRange = LateDecisionClock.getCalcRange(
				predetermineTimeSet, timeLeavingWork, integrationOfWorkTime,
				predetermineTimeForSet, workType.getDailyWork().decisionNeedPredTime());
		if (!calcRange.isPresent()) return Optional.empty();
		TimeWithDayAttr lateStartClock = calcRange.get().getStart();
		// 遅刻時間を計算する時間帯を判断
		Optional<LateLeaveEarlyTimeSheet> beforeAdjustOpt = checkTimeSheetForCalcLateTime(
				deductionAtr, timeLeavingWork, integrationOfWorkTime,
				deductTimeSheet, lateStartClock, withinWorkTimeFrame, otherEmTimezoneLateEarlySet);
		if (!beforeAdjustOpt.isPresent()) return Optional.empty();
		LateLeaveEarlyTimeSheet beforeAdjust = beforeAdjustOpt.get();
		// 遅刻時間を計算（丸め前、丸め後）
		AttendanceTime beforeRounding = beforeAdjust.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE);
		AttendanceTime afterRounding = beforeAdjust.calcTotalTime();
		// 遅刻終了時刻を取得
		TimeWithDayAttr lateEndClock = beforeAdjust.getLateEndTime(beforeRounding, afterRounding, deductTimeSheet);
		// 遅刻早退時間帯を作成
		LateLeaveEarlyTimeSheet result = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(lateStartClock, lateEndClock), beforeAdjust.getRounding());
		// 控除時間帯の登録
		result.registDeductionList(ActualWorkTimeSheetAtrForLate.Late,
				deductTimeSheet, integrationOfWorkTime.getCommonSetting());
		// 遅刻早退時間帯を返す
		return Optional.of(result);
	}

	/**
	 * 遅刻時間を計算する時間帯を判断
	 * @param deductionAtr 控除区分
	 * @param timeLeavingWork 出退勤
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param deductTimeSheet 控除時間帯
	 * @param lateStartClock 遅刻開始時刻
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @return 遅刻早退時間帯
	 */
	private static Optional<LateLeaveEarlyTimeSheet> checkTimeSheetForCalcLateTime(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			DeductionTimeSheet deductTimeSheet,
			TimeWithDayAttr lateStartClock,
			WithinWorkTimeFrame withinWorkTimeFrame,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){
		
		// 出勤時刻を確認する
		TimeWithDayAttr attendance = null;
		if (timeLeavingWork.getAttendanceStamp().isPresent()){
			TimeActualStamp attdStamp = timeLeavingWork.getAttendanceStamp().get();
			if (attdStamp.getStamp().isPresent()){
				WorkStamp stamp = attdStamp.getStamp().get();
				if (stamp.getTimeDay().getTimeWithDay().isPresent()) {
					attendance = stamp.getTimeDay().getTimeWithDay().get();
				}
			}
		}
		if (attendance == null) return Optional.empty();
		// 遅刻開始時刻←input.遅刻開始時刻
		TimeWithDayAttr start = lateStartClock;
		// 遅刻終了時刻←出勤時刻
		TimeWithDayAttr end = attendance;
		if (attendance.greaterThan(withinWorkTimeFrame.getTimeSheet().getEnd())){
			// 遅刻終了時刻←就業時間内時間枠の終了時刻
			end = withinWorkTimeFrame.getTimeSheet().getEnd();
		}
		// 丸め設定を保持
		TimeRoundingSetting roundingSet = otherEmTimezoneLateEarlySet.getRoundingSetByDedAtr(deductionAtr.isDeduction());
		// 遅刻早退時間帯を作成
		LateLeaveEarlyTimeSheet result = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(start, end),
				new TimeRoundingSetting(roundingSet.getRoundingTime(), roundingSet.getRounding()));
		// 控除時間帯の登録
		result.registDeductionList(ActualWorkTimeSheetAtrForLate.Late,
				deductTimeSheet, integrationOfWorkTime.getCommonSetting());
		// 遅刻早退時間帯を返す
		return Optional.of(result);
	}
	
	/**
	 * 遅刻計上時間の計算
	 * @param late 計算区分
	 * @param deductOffset 相殺時間控除区分
	 * @param roundAtr 丸めを行う
	 * @return 遅刻計上時間
	 */
	public TimeWithCalculation calcForRecordTime(boolean late, boolean deductOffset, NotUseAtr roundAtr){
		//遅刻時間の計算
		AttendanceTime calcforRecordTime = AttendanceTime.ZERO;
		//遅刻時間←0：00
		AttendanceTime lateTimeForRecord = AttendanceTime.ZERO;
		if (!this.forRecordTimeSheet.isPresent()) {
			return TimeWithCalculation.createTimeWithCalculation(lateTimeForRecord, calcforRecordTime);
		}
		// 計算区分を取得
		if (late) {
			// 遅刻時間の計算
			lateTimeForRecord = this.forRecordTimeSheet.get()
					.calcTotalTime(deductOffset ? NotUseAtr.USE : NotUseAtr.NOT_USE, roundAtr);
		}

		// 計算遅刻時間の計算
		calcforRecordTime = this.forRecordTimeSheet.get().calcTotalTime(NotUseAtr.NOT_USE, roundAtr);
		
		// インターバル免除時間を控除する
		return TimeWithCalculation.createTimeWithCalculation(lateTimeForRecord, calcforRecordTime);
		
	}
	
	/**
	 * 遅刻控除時間の計算
	 * @param late 計算区分
	 * @param notUseAtr 遅刻控除区分
	 * @return 遅刻控除時間
	 */
	public TimeWithCalculation calcDedctionTime(boolean late, NotUseAtr notUseAtr) {
		TimeWithCalculation lateDeductionTime = TimeWithCalculation.sameTime(new AttendanceTime(0));
		if(notUseAtr==NotUseAtr.USE) {//控除する場合
			AttendanceTime calcDeductionTime = this.forDeducationTimeSheet.isPresent() ?
					this.forDeducationTimeSheet.get().calcTotalTime(NotUseAtr.NOT_USE, NotUseAtr.USE) :
						new AttendanceTime(0);
			lateDeductionTime =  late ?
					TimeWithCalculation.sameTime(calcDeductionTime) :
						TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(0),calcDeductionTime);
		}
		return lateDeductionTime;
	}
	
	/**
	 * 自身が持つ短時間勤務時間帯(控除)を収集
	 * @return　短時間勤務時間帯
	 */
	public List<TimeSheetOfDeductionItem> getShortTimeSheet(){
		List<TimeSheetOfDeductionItem> returnList = new ArrayList<>();
		if(this.getForRecordTimeSheet() != null && this.getForRecordTimeSheet().isPresent()) {
			returnList.addAll(this.getForRecordTimeSheet().get().collectShortTimeSheet());
		}
		return returnList;
	}
	
	/**
	 * 遅刻時間の休暇時間相殺
	 * @param deductionAtr 控除 or 計上
	 * @param companyholidayPriorityOrder 時間休暇相殺優先順位
	 * @param timeVacationUseTime 日別実績の時間休暇使用時間
	 */
	public void offsetVacationTime(
			DeductionAtr deductionAtr,
			CompanyHolidayPriorityOrder companyholidayPriorityOrder,
			TimevacationUseTimeOfDaily timeVacationUseTime){
		
		if (!this.getDecitionTimeSheet(deductionAtr).isPresent()) return;
		// 相殺する
		timeVacationUseTime = this.getDecitionTimeSheet(deductionAtr).get().offsetProcess(
				companyholidayPriorityOrder, timeVacationUseTime, NotUseAtr.NOT_USE);
	}
	
	/**
	 * 重複する時間帯で作り直す
	 * @param timeSpan 時間帯
	 * @param deductionTimeSheet 控除時間帯
	 * @param commonSet 就業時間帯の共通設定
	 * @return 遅刻時間帯
	 */
	public Optional<LateTimeSheet> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan, DeductionTimeSheet deductionTimeSheet, WorkTimezoneCommonSet commonSet) {
		//計上用時間帯を変更する
		Optional<LateLeaveEarlyTimeSheet> record = this.forRecordTimeSheet.flatMap(
				r -> r.recreateWithDuplicate(timeSpan, ActualWorkTimeSheetAtrForLate.Late, deductionTimeSheet, commonSet));
		//控除用時間帯を変更する
		Optional<LateLeaveEarlyTimeSheet> deducation = this.forDeducationTimeSheet.flatMap(
				d -> d.recreateWithDuplicate(timeSpan, ActualWorkTimeSheetAtrForLate.Late, deductionTimeSheet, commonSet));
		if(!record.isPresent() && !deducation.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new LateTimeSheet(
				record,
				deducation,
				this.workNo,
				this.noCoreFlexLateTime.map(n -> new AttendanceTime(n.valueAsMinutes()))));
	}
}
