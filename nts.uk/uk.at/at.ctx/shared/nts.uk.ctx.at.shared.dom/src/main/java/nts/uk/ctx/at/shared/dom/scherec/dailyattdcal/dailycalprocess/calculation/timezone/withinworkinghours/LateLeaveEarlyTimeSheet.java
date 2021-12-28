package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.ActualWorkTimeSheetAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.TimevacationUseTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.DeductionTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeVacationOffSetItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.DeductionAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.deductiontime.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.worktime.common.LateEarlyAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSet;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 遅刻早退時間帯
 * @author ken_takasu
 */
public class LateLeaveEarlyTimeSheet extends TimeVacationOffSetItem implements Cloneable {

	public LateLeaveEarlyTimeSheet(TimeSpanForDailyCalc timeSheet, TimeRoundingSetting rounding) {
		super(timeSheet, rounding);
	}

	public LateLeaveEarlyTimeSheet(
			TimeSpanForDailyCalc timeSheet,
			TimeRoundingSetting rounding,
			List<TimeSheetOfDeductionItem> recorddeductionTimeSheets,
			List<TimeSheetOfDeductionItem> deductionTimeSheets,
			Optional<TimevacationUseTimeOfDaily> deductionOffsetTime) {
		super(timeSheet, rounding, recorddeductionTimeSheets, deductionTimeSheets, deductionOffsetTime);
	}
	
	/**
	 * 他の遅刻早退時間帯の値を追加する
	 * @param other 追加する遅刻早退時間帯
	 */
	public void addOtherSheet(LateLeaveEarlyTimeSheet other){
		// 時間帯．終了を変更する
		this.timeSheet = this.timeSheet.shiftOnlyEnd(other.timeSheet.getEnd());
		// 控除時間帯に追加する
		this.recordedTimeSheet.addAll(other.recordedTimeSheet);
		this.deductionTimeSheet.addAll(other.deductionTimeSheet);
		// 控除相殺時間に加算する
		if (other.deductionOffSetTime.isPresent()){
			if (this.deductionOffSetTime.isPresent()){
				this.deductionOffSetTime = Optional.of(this.deductionOffSetTime.get().add(other.deductionOffSetTime.get()));
			}
			else{
				this.deductionOffSetTime = other.deductionOffSetTime;
			}
		}
	}
	
	/**
	 * 遅刻終了時刻を取得
	 * @param beforeRounding 丸め前の遅刻時間
	 * @param afterRounding 丸め後の遅刻時間
	 * @param deductTimeSheet 控除時間帯
	 * @return 終了時刻
	 */
	public TimeWithDayAttr getLateEndTime(
			AttendanceTime beforeRounding,
			AttendanceTime afterRounding,
			DeductionTimeSheet deductTimeSheet) {
		
		// 丸め後の遅刻時間=0の時
		if (afterRounding.valueAsMinutes() == 0){
			// 丸め前の遅刻時間=0なら、終了時刻（出勤時刻）をそのまま返す
			if (beforeRounding.valueAsMinutes() == 0) return this.getTimeSheet().getEnd();
			// 丸め前の遅刻時間>0なら、終了時刻から丸め前の分（丸め差分）だけ戻した時間を返す
			int beforeMinutes = beforeRounding.valueAsMinutes();
			int diffMinutes = 0;
			AttendanceTime diffTime = new AttendanceTime(0);
			while (diffTime.valueAsMinutes() < beforeMinutes){
				// 差分時間帯(diffSheet)の計算時間(diffTime)が、丸め前の遅刻時間(beforeMinutes)に足りるまで、
				// 差分時間帯の開始を足りない分づつ広げながら、ループする。
				diffMinutes += beforeMinutes - diffTime.valueAsMinutes();	// ←　足りない分、広げている
				LateLeaveEarlyTimeSheet diffSheet = new LateLeaveEarlyTimeSheet(
						new TimeSpanForDailyCalc(
								this.getTimeSheet().getEnd().backByMinutes(diffMinutes),
								this.getTimeSheet().getEnd()),
						new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN));
				diffSheet.addDuplicatedDeductionTimeSheet(
						deductTimeSheet.getForDeductionTimeZoneList(), DeductionAtr.Deduction, Optional.empty());
				diffTime = diffSheet.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE);
			}
			return this.getTimeSheet().getEnd().backByMinutes(diffMinutes);
		}
		// 開始から丸め前の遅刻時間分加算した時刻を求める　→　遅刻時間帯
		TimeSpanForDailyCalc lateTimeSheet = new TimeSpanForDailyCalc(
				this.getTimeSheet().getStart(),
				this.getTimeSheet().getStart().forwardByMinutes(beforeRounding.valueAsMinutes()));
		// 控除時間帯を取得　（開始時刻でソート）
		List<TimeSheetOfDeductionItem> deductItems = deductTimeSheet.getForDeductionTimeZoneList().stream()
				.sorted((time1,time2) -> time1.getTimeSheet().getStart().compareTo(time2.getTimeSheet().getStart()))
				.collect(Collectors.toList());
		// 控除時間帯分ループ
		for (TimeSheetOfDeductionItem deductItem : deductItems) {
			// 遅刻時間帯と重複しているか確認する
			Optional<TimeSpanForDailyCalc> dupSpan = lateTimeSheet.getDuplicatedWith(deductItem.getTimeSheet());
			if (!dupSpan.isPresent()) continue;
			// 重複していた時、対象の時間帯から重複開始時刻以降の時間帯を取り出す
			TimeSheetOfDeductionItem diffSheet = deductItem.reCreateOwn(dupSpan.get().getStart(), false);
			// 控除時間の計算
			int deductTime = diffSheet.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE).valueAsMinutes();
			if (deductTime > 0){
				// 控除時間分、終了時刻を後ろにズラす
				lateTimeSheet = new TimeSpanForDailyCalc(
						lateTimeSheet.getStart(), lateTimeSheet.getEnd().forwardByMinutes(deductTime));
			}
		}
		// 遅刻時間帯.終了時刻を返す
		return lateTimeSheet.getEnd();
	}
	
	/**
	 * 早退開始時刻を取得
	 * @param beforeRounding 丸め前の早退時間
	 * @param afterRounding 丸め後の早退時間
	 * @param deductTimeSheet 控除時間帯
	 * @return 開始時刻
	 */
	public TimeWithDayAttr getLeaveStartTime(
			AttendanceTime beforeRounding,
			AttendanceTime afterRounding,
			DeductionTimeSheet deductTimeSheet) {
		
		// 丸め後の早退時間=0の時
		if (afterRounding.valueAsMinutes() == 0){
			// 丸め前の早退時間=0なら、開始時刻（退勤時刻）をそのまま返す
			if (beforeRounding.valueAsMinutes() == 0) return this.getTimeSheet().getStart();
			// 丸め前の早退時間>0なら、開始時刻から丸め前の分（丸め差分）だけ進めた時間を返す
			int beforeMinutes = beforeRounding.valueAsMinutes();
			int diffMinutes = 0;
			AttendanceTime diffTime = new AttendanceTime(0);
			while (diffTime.valueAsMinutes() < beforeMinutes){
				// 差分時間帯(diffSheet)の計算時間(diffTime)が、丸め前の早退時間(beforeMinutes)に足りるまで、
				// 差分時間帯の終了を足りない分づつ広げながら、ループする。
				diffMinutes += beforeMinutes - diffTime.valueAsMinutes();	// ←　足りない分、広げている
				LateLeaveEarlyTimeSheet diffSheet = new LateLeaveEarlyTimeSheet(
						new TimeSpanForDailyCalc(
								this.getTimeSheet().getStart(),
								this.getTimeSheet().getStart().forwardByMinutes(diffMinutes)),
						new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN));
				diffSheet.addDuplicatedDeductionTimeSheet(
						deductTimeSheet.getForDeductionTimeZoneList(), DeductionAtr.Deduction, Optional.empty());
				diffTime = diffSheet.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE);
			}
			return this.getTimeSheet().getStart().forwardByMinutes(diffMinutes);
		}
		// 終了から丸め前の早退時間分減算した時刻を求める　→　早退時間帯
		TimeSpanForDailyCalc leaveTimeSheet = new TimeSpanForDailyCalc(
				this.getTimeSheet().getEnd().backByMinutes(beforeRounding.valueAsMinutes()),
				this.getTimeSheet().getEnd());
		// 控除時間帯を取得　（開始時刻で逆ソート）
		List<TimeSheetOfDeductionItem> deductItems = new ArrayList<>(
				deductTimeSheet.getForDeductionTimeZoneList().stream()
				.sorted((time1,time2) -> time2.getTimeSheet().getStart().compareTo(time1.getTimeSheet().getStart()))
				.collect(Collectors.toList()));
		// 控除時間帯分ループ
		for (TimeSheetOfDeductionItem deductItem : deductItems) {
			// 早退時間帯と重複しているか確認する
			Optional<TimeSpanForDailyCalc> dupSpan = leaveTimeSheet.getDuplicatedWith(deductItem.getTimeSheet());
			if (!dupSpan.isPresent()) continue;
			// 重複していた時、対象の時間帯から重複終了時刻以前の時間帯を取り出す
			TimeSheetOfDeductionItem diffSheet = deductItem.reCreateOwn(dupSpan.get().getEnd(), true);
			// 控除時間の計算
			int deductTime = diffSheet.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE).valueAsMinutes();
			if (deductTime > 0){
				// 控除時間分、開始時刻を手前にズラす
				leaveTimeSheet = new TimeSpanForDailyCalc(
						leaveTimeSheet.getStart().backByMinutes(deductTime), leaveTimeSheet.getEnd());
			}
		}
		// 早退時間帯.開始時刻を返す
		return leaveTimeSheet.getStart();
	}
	
	/**
	 * 控除時間帯の登録（遅刻早退時間帯）
	 * @param actualAtr 実働時間帯区分
	 * @param deductionTimeSheet 控除時間帯
	 * @param commonSet 就業時間帯の共通設定
	 */
	public void registDeductionList(
			ActualWorkTimeSheetAtrForLate actualAtr,
			DeductionTimeSheet deductionTimeSheet,
			WorkTimezoneCommonSet commonSet){
		
		// 控除時間帯の登録
		this.registDeductionList(deductionTimeSheet, Optional.empty());
		// 控除時間帯へ丸め設定を付与
		this.grantRoundingToDeductionTimeSheetForLate(actualAtr, commonSet);
	}
	
	/**
	 * 控除時間帯へ丸め設定を付与
	 * @param actualAtr 実働時間帯区分
	 * @param commonSet 就業時間帯の共通設定
	 */
	public void grantRoundingToDeductionTimeSheetForLate(
			ActualWorkTimeSheetAtrForLate actualAtr,
			WorkTimezoneCommonSet commonSet){
		
		// 計上用控除時間帯に丸め設定を付与
		this.grantRoundingDeductionOrAppropriateForLate(actualAtr, DeductionAtr.Appropriate, commonSet);
		// 控除用控除時間帯に丸め設定を付与
		this.grantRoundingDeductionOrAppropriateForLate(actualAtr, DeductionAtr.Deduction, commonSet);
	}
	
	/**
	 * 丸め後の遅刻時間帯を取得する
	 * @return 丸め後の遅刻時間帯
	 */
	public TimeSpanForDailyCalc getAfterRoundingAsLate() {
		//丸め差分
		int roundDiff = this.calcTotalTime(NotUseAtr.USE, NotUseAtr.USE).valueAsMinutes() - this.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE).valueAsMinutes();
		TimeSpanForDailyCalc result = this.timeSheet.shiftEndAhead(roundDiff);
		return result;
	}
	
	/**
	 * 丸め後の早退時間帯を取得する
	 * @return 丸め後の早退時間帯
	 */
	public TimeSpanForDailyCalc getAfterRoundingAsLeaveEarly() {
		//丸め差分
		int roundDiff = this.calcTotalTime(NotUseAtr.USE, NotUseAtr.USE).valueAsMinutes() - this.calcTotalTime(NotUseAtr.USE, NotUseAtr.NOT_USE).valueAsMinutes();
		TimeSpanForDailyCalc result = this.timeSheet.shiftStartBack(roundDiff);
		return result;
	}
	
	/**
	 * ループ処理（控除時間帯へ丸め設定を付与）
	 * @param actualAtr 実働時間帯区分
	 * @param dedAtr 控除 or 計上
	 * @param commonSet 就業時間帯の共通設定
	 */
	private void grantRoundingDeductionOrAppropriateForLate(
			ActualWorkTimeSheetAtrForLate actualAtr,
			DeductionAtr dedAtr,
			WorkTimezoneCommonSet commonSet){
		
		List<TimeSheetOfDeductionItem> targetList = this.deductionTimeSheet;
		if (dedAtr.isAppropriate()) targetList = this.recordedTimeSheet;
		targetList.forEach(dt -> {
			if (dt.getDeductionAtr().isChildCare()) {
				// 育児時間帯の丸め設定を付与する
				TimeRoundingSetting roundingSet = new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN);
				if (dedAtr.isDeduction()){
					if (actualAtr.isLate()){
						roundingSet = commonSet.getLateEarlySet()
								.getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE).getRoundingSetByDedAtr(true);
					}
					else if (actualAtr.isLeaveEarly()){
						roundingSet = commonSet.getLateEarlySet()
								.getOtherEmTimezoneLateEarlySet(LateEarlyAtr.EARLY).getRoundingSetByDedAtr(true);
					}
				}
				else if (dedAtr.isAppropriate()){
					// 2021.2.11 shuichi_ishida
					// ※　本来は「就業時間帯の短時間勤務設定.丸め設定」を適用する必要があるが、属性が無いため、暫定的に控除と同じ処理にする。
					if (actualAtr.isLate()){
						roundingSet = commonSet.getLateEarlySet()
								.getOtherEmTimezoneLateEarlySet(LateEarlyAtr.LATE).getRoundingSetByDedAtr(true);
					}
					else if (actualAtr.isLeaveEarly()){
						roundingSet = commonSet.getLateEarlySet()
								.getOtherEmTimezoneLateEarlySet(LateEarlyAtr.EARLY).getRoundingSetByDedAtr(true);
					}
				}
				dt.getRounding().correctData(roundingSet);
			}
			else {
				// 丸め設定を付与する
				this.grantRoundingDeductionOrAppropriate(ActualWorkTimeSheetAtr.WithinWorkTime, dedAtr, commonSet);
			}
		});
	}
	
	/**
	 * 重複する時間帯で作り直す
	 * @param timeSpan 時間帯
	 * @param deductionTimeSheet 控除時間帯
	 * @param actualAtr 実働時間帯区分
	 * @param commonSet 就業時間帯の共通設定
	 * @return 遅刻早退時間帯
	 */
	public Optional<LateLeaveEarlyTimeSheet> recreateWithDuplicate(TimeSpanForDailyCalc timeSpan, ActualWorkTimeSheetAtrForLate actualAtr,
			DeductionTimeSheet deductionTimeSheet, WorkTimezoneCommonSet commonSet) {
		Optional<TimeSpanForDailyCalc> duplicate = this.timeSheet.getDuplicatedWith(timeSpan);
		if(!duplicate.isPresent()) {
			return Optional.empty();
		}
		LateLeaveEarlyTimeSheet clone = this.clone();
		clone.timeSheet = duplicate.get();
		//控除時間帯の登録
		clone.registDeductionList(actualAtr, deductionTimeSheet, commonSet);
		//控除相殺時間を削除する
		clone.deductionOffSetTime = Optional.empty();
		return Optional.of(clone);
	}
	
	public LateLeaveEarlyTimeSheet clone() {
		LateLeaveEarlyTimeSheet clone = new LateLeaveEarlyTimeSheet(
				this.timeSheet,
				this.rounding,
				this.recordedTimeSheet,
				this.deductionTimeSheet,
				this.deductionOffSetTime);
		try {
			clone.timeSheet = this.timeSheet.clone();
			clone.rounding = this.rounding.clone();
			clone.recordedTimeSheet = this.recordedTimeSheet.stream().map(r -> r.clone()).collect(Collectors.toList());
			clone.deductionTimeSheet = this.deductionTimeSheet.stream().map(r -> r.clone()).collect(Collectors.toList());
			clone.deductionOffSetTime = this.deductionOffSetTime.map(d -> d.clone());
		}
		catch (Exception e) {
			throw new RuntimeException("LateLeaveEarlyTimeSheet clone error.");
		}
		return clone;
	}
}
