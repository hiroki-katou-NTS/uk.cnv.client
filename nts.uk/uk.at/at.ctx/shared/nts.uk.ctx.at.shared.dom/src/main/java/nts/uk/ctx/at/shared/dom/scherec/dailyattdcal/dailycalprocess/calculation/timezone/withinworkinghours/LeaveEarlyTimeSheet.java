package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
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
 * 早退時間帯
 * @author ken_takasu
 */
@Getter
public class LeaveEarlyTimeSheet {
	
	//計上用遅刻早退時間帯
	//早退していない場合はempty
	@Getter
	private Optional<LateLeaveEarlyTimeSheet> forRecordTimeSheet;
	
	//控除用遅刻早退時間帯
	@Getter
	private Optional<LateLeaveEarlyTimeSheet> forDeducationTimeSheet;

	@Getter
	//勤務No
	private int workNo;
	
	public LeaveEarlyTimeSheet(
			Optional<LateLeaveEarlyTimeSheet> recordTimeSheet,
			Optional<LateLeaveEarlyTimeSheet> deductionTimeSheet,
			int workNo) {
		
		this.forRecordTimeSheet = recordTimeSheet;
		this.forDeducationTimeSheet = deductionTimeSheet;
		this.workNo = workNo;
	}
	
	public static LeaveEarlyTimeSheet createAsLeaveEarly(LateLeaveEarlyTimeSheet recordTimeSheet,LateLeaveEarlyTimeSheet deductionTimeSheet,int workNo,Optional<LateTimeOfDaily> lateTime) {
		return new LeaveEarlyTimeSheet(Optional.of(recordTimeSheet), Optional.of(deductionTimeSheet),workNo);
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
		if(dedAtr.isDeduction()) {
			this.forDeducationTimeSheet = forTimeSheet;
		}
	}

	/**
	 * 早退時間帯の作成
	 * @param leaveEarlyDesClock 早退判断時刻
	 * @param timeLeavingWork 出退勤
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param deductionTimeSheet 控除時間帯
	 * @param predetermineTime 所定時間設定
	 * @param workNo 勤務NO
	 * @param workType 勤務種類
	 * @param predetermineTimeSetForCalc 計算用所定時間設定
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param integrationOfDaily 日別実績(Work)
	 * @return 早退時間帯
	 */
	public static Optional<LeaveEarlyTimeSheet> createLeaveEarlyTimeSheet(
			Optional<LeaveEarlyDecisionClock> leaveEarlyDesClock,
			TimeLeavingWork timeLeavingWork,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductionTimeSheet,
			Optional<TimezoneUse> predetermineTime,
			int workNo,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc,
			IntegrationOfWorkTime integrationOfWorkTime,
			IntegrationOfDaily integrationOfDaily) {
		
		//退勤時刻
		Optional<TimeWithDayAttr> leave = Optional.empty();
		leave = timeLeavingWork.getLeaveTime();
		if(!leave.isPresent() || !leaveEarlyDesClock.isPresent() || !predetermineTime.isPresent()) {
			return Optional.empty();
		}
		
		//早退時間帯
		Optional<LateLeaveEarlyTimeSheet> leaveEarlyAppTimeSheet = Optional.empty();
		//早退控除時間帯
		Optional<LateLeaveEarlyTimeSheet> leaveEarlyDeductTimeSheet = Optional.empty();
		
		//退勤時刻と早退判断時刻を比較	
		if(leaveEarlyDesClock.get().getLeaveEarlyDecisionClock().greaterThan(leave.get())) {
			//早退控除時間帯の作成
			leaveEarlyDeductTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Deduction,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTime.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
			//早退時間帯の作成
			leaveEarlyAppTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Appropriate,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTime.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
		}else {	
			//早退控除時間帯の作成
			leaveEarlyDeductTimeSheet = createLateLeaveEarlyTimeSheet(
					DeductionAtr.Deduction,
					timeLeavingWork,
					integrationOfWorkTime,
					integrationOfDaily,
					predetermineTime.get(),
					withinWorkTimeFrame,
					deductionTimeSheet,
					workType,
					predetermineTimeSetForCalc,
					otherEmTimezoneLateEarlySet);
		}
		if(!leaveEarlyAppTimeSheet.isPresent() && !leaveEarlyDeductTimeSheet.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new LeaveEarlyTimeSheet(leaveEarlyAppTimeSheet,leaveEarlyDeductTimeSheet, workNo));
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
		// 早退時間帯を作成する
		Optional<LateLeaveEarlyTimeSheet> instance = createLeaveEarlyTimeSheetInstance(
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
	 * 早退時間帯を作成する
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
	private static Optional<LateLeaveEarlyTimeSheet> createLeaveEarlyTimeSheetInstance(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			TimezoneUse predetermineTimeSet,
			WithinWorkTimeFrame withinWorkTimeFrame,
			DeductionTimeSheet deductTimeSheet,
			WorkType workType,
			PredetermineTimeSetForCalc predetermineTimeForSet,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){
		
		// 早退終了時刻を取得
		Optional<TimeSpanForDailyCalc> calcRange = LeaveEarlyDecisionClock.getCalcRange(
				predetermineTimeSet, timeLeavingWork, integrationOfWorkTime,
				predetermineTimeForSet, workType.getDailyWork().decisionNeedPredTime());
		if (!calcRange.isPresent() || calcRange.get().isReverse() || calcRange.get().isEqual()) return Optional.empty();
		TimeWithDayAttr leaveEndClock = calcRange.get().getEnd();
		// 早退時間を計算する時間帯を判断
		Optional<LateLeaveEarlyTimeSheet> beforeAdjustOpt = checkTimeSheetForCalcLeaveTime(
				deductionAtr, timeLeavingWork, integrationOfWorkTime,
				deductTimeSheet, leaveEndClock, withinWorkTimeFrame, otherEmTimezoneLateEarlySet);
		if (!beforeAdjustOpt.isPresent()) return Optional.empty();
		LateLeaveEarlyTimeSheet beforeAdjust = beforeAdjustOpt.get();
		// 早退時間を計算（丸め前、丸め後）
		AttendanceTime beforeRounding = beforeAdjust.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE);
		AttendanceTime afterRounding = beforeAdjust.calcTotalTime();
		// 早退開始時刻を取得
		TimeWithDayAttr leaveStartClock = beforeAdjust.getLeaveStartTime(beforeRounding, afterRounding, deductTimeSheet);
		// 遅刻早退時間帯を作成
		LateLeaveEarlyTimeSheet result = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(leaveStartClock, leaveEndClock), beforeAdjust.getRounding());
		// 控除時間帯の登録
		result.registDeductionList(ActualWorkTimeSheetAtrForLate.LeaveEarly,
				deductTimeSheet, integrationOfWorkTime.getCommonSetting());
		// 遅刻早退時間帯を返す
		return Optional.of(result);
	}

	/**
	 * 早退時間を計算する時間帯を判断
	 * @param deductionAtr 控除区分
	 * @param timeLeavingWork 出退勤
	 * @param integrationOfWorkTime 統合就業時間帯
	 * @param deductTimeSheet 控除時間帯
	 * @param leaveEndClock 早退終了時刻
	 * @param withinWorkTimeFrame 就業時間内時間枠
	 * @param otherEmTimezoneLateEarlySet 就業時間帯の遅刻・早退別設定
	 * @return 遅刻早退時間帯
	 */
	private static Optional<LateLeaveEarlyTimeSheet> checkTimeSheetForCalcLeaveTime(
			DeductionAtr deductionAtr,
			TimeLeavingWork timeLeavingWork,
			IntegrationOfWorkTime integrationOfWorkTime,
			DeductionTimeSheet deductTimeSheet,
			TimeWithDayAttr leaveEndClock,
			WithinWorkTimeFrame withinWorkTimeFrame,
			OtherEmTimezoneLateEarlySet otherEmTimezoneLateEarlySet){
		
		// 退勤時刻を確認する
		TimeWithDayAttr leave = null;
		if (timeLeavingWork.getLeaveStamp().isPresent()){
			TimeActualStamp leaveStamp = timeLeavingWork.getLeaveStamp().get();
			if (leaveStamp.getStamp().isPresent()){
				WorkStamp stamp = leaveStamp.getStamp().get();
				if (stamp.getTimeDay().getTimeWithDay().isPresent()) {
					leave = stamp.getTimeDay().getTimeWithDay().get();
				}
			}
		}
		if (leave == null) return Optional.empty();
		// 早退終了時刻←input.早退終了時刻
		TimeWithDayAttr end = leaveEndClock;
		// 早退開始時刻←退勤時刻
		TimeWithDayAttr start = leave;
		if (leave.lessThanOrEqualTo(withinWorkTimeFrame.getTimeSheet().getStart())){
			// 早退開始時刻←就業時間内時間枠の開始時刻
			start = withinWorkTimeFrame.getTimeSheet().getStart();
		}
		// 丸め設定を保持
		TimeRoundingSetting roundingSet = otherEmTimezoneLateEarlySet.getRoundingSetByDedAtr(deductionAtr.isDeduction());
		// 遅刻早退時間帯を作成
		LateLeaveEarlyTimeSheet result = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(start, end),
				new TimeRoundingSetting(roundingSet.getRoundingTime(), roundingSet.getRounding()));
		// 控除時間帯の登録
		result.registDeductionList(ActualWorkTimeSheetAtrForLate.LeaveEarly,
				deductTimeSheet, integrationOfWorkTime.getCommonSetting());
		// 遅刻早退時間帯を返す
		return Optional.of(result);
	}
	
	/**
	 * 早退計上時間の計算
	 * @param leaveEarly 計算区分
	 * @param deductOffset 相殺時間控除区分
	 * @param roundAtr 丸めを行う
	 * @return 早退計上時間
	 */
	public TimeWithCalculation calcForRecordTime(boolean leaveEarly, boolean deductOffset, NotUseAtr roundAtr){
		
		AttendanceTime calcforRecordTime = AttendanceTime.ZERO;
		//早退時間←0：00
		AttendanceTime earlyTimeForRecord = AttendanceTime.ZERO;
		if (!this.forRecordTimeSheet.isPresent()) {
			return TimeWithCalculation.createTimeWithCalculation(earlyTimeForRecord, calcforRecordTime);
		}
		// 計算区分を取得
		if (leaveEarly) {
			//早退時間の計算
			earlyTimeForRecord = this.forRecordTimeSheet.get()
					.calcTotalTime(deductOffset ? NotUseAtr.USE : NotUseAtr.NOT_USE, roundAtr);
		}

		// 計算早退時間の計算
		calcforRecordTime = this.forRecordTimeSheet.get().calcTotalTime(NotUseAtr.NOT_USE, roundAtr);
		
		return TimeWithCalculation.createTimeWithCalculation(earlyTimeForRecord, calcforRecordTime);
		
	}
	
	/**
	 * 早退控除時間の計算
	 * @param leaveEarly 計算区分
	 * @param notUseAtr 早退控除区分
	 * @return 早退控除時間
	 */
	public TimeWithCalculation calcDedctionTime(boolean leaveEarly, NotUseAtr notUseAtr) {
		TimeWithCalculation leaveEarlyDeductionTime = TimeWithCalculation.sameTime(new AttendanceTime(0));
		if(notUseAtr==NotUseAtr.USE) {//控除する場合
			AttendanceTime calcDeductionTime = this.forDeducationTimeSheet.isPresent() ?
					this.forDeducationTimeSheet.get().calcTotalTime(NotUseAtr.NOT_USE, NotUseAtr.USE) :
						new AttendanceTime(0);
			leaveEarlyDeductionTime =  leaveEarly ?
					TimeWithCalculation.sameTime(calcDeductionTime) :
						TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(0),calcDeductionTime);
		}
		return leaveEarlyDeductionTime;
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
	 * 早退時間の休暇時間相殺
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
	 * @return 早退時間帯 
	 */
	public Optional<LeaveEarlyTimeSheet> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan, DeductionTimeSheet deductionTimeSheet, WorkTimezoneCommonSet commonSet) {
		//計上用時間帯を変更する
		Optional<LateLeaveEarlyTimeSheet> record = this.forRecordTimeSheet.flatMap(
				r -> r.recreateWithDuplicate(timeSpan, ActualWorkTimeSheetAtrForLate.LeaveEarly, deductionTimeSheet, commonSet));
		//控除用時間帯を変更する
		Optional<LateLeaveEarlyTimeSheet> deducation = this.forDeducationTimeSheet.flatMap(
				d -> d.recreateWithDuplicate(timeSpan, ActualWorkTimeSheetAtrForLate.LeaveEarly, deductionTimeSheet, commonSet));
		if(!record.isPresent() && !deducation.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(new LeaveEarlyTimeSheet(
				record,
				deducation,
				this.workNo));
	}
}