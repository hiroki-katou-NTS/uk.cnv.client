package nts.uk.ctx.at.record.dom.dailyprocess.calc.withinstatutory;

import java.util.List;
import java.util.Optional;

import lombok.val;
import lombok.Getter;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.record.dom.MidNightTimeSheet;
import nts.uk.ctx.at.record.dom.daily.AttendanceLeavingWork;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculationTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.DeductionAtr;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.DeductionTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.LateLeaveEarlyManagementTimeFrame;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.LateTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.LeaveEarlyTimeSheet;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.TimeSheetOfDeductionItem;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BonusPayTimesheet;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.SpecifiedbonusPayTimeSheet;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.HolidayAdditionAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.addsettingofworktime.WorkTimeCalcMethodDetailOfHoliday;
import nts.uk.ctx.at.shared.dom.worktime.WorkTime;
import nts.uk.ctx.at.shared.dom.worktime.CommonSetting.lateleaveearly.GraceTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedworkset.WorkTimeCommonSet;
import nts.uk.ctx.at.shared.dom.worktime.fixedworkset.timespan.TimeSpanWithRounding;
import nts.uk.ctx.at.shared.dom.worktime.fluidworkset.Rounding;
import nts.uk.ctx.at.shared.dom.worktime.fluidworkset.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.worktime.fluidworkset.Unit;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.DailyWork;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 就業時間内時間枠
 * @author keisuke_hoshina
 *
 */
public class WithinWorkTimeFrame extends CalculationTimeSheet implements LateLeaveEarlyManagementTimeFrame {

	@Getter
	private final int workingHoursTimeNo;
	
	private final Optional<TimeSpanForCalc> premiumTimeSheetInPredetermined;
	
	//遅刻時間帯・・・deductByLateLeaveEarlyを呼ぶまでは値が無い
	private Finally<LateTimeSheet> lateTimeSheet = Finally.empty();
	//早退時間帯・・・deductByLateLeaveEarlyを呼ぶまでは値が無い
	private Finally<LeaveEarlyTimeSheet> leaveEarlyTimeSheet = Finally.empty();
	
	public TimeSpanForCalc getPremiumTimeSheetInPredetermined() {
		return this.premiumTimeSheetInPredetermined.get();
	}
	
	/**
	 * constructor
	 * @param workingHoursTimeNo
	 * @param timeSheet
	 * @param calculationTimeSheet
	 */
	public WithinWorkTimeFrame(
			int workingHoursTimeNo,
			TimeSpanWithRounding timeSheet,
			TimeSpanForCalc calculationTimeSheet,
			List<TimeSheetOfDeductionItem> deductionTimeSheets,
			List<BonusPayTimesheet> bonusPayTimeSheet,
			Optional<MidNightTimeSheet> midNighttimeSheet,
			List<SpecifiedbonusPayTimeSheet> specifiedBonusPayTimeSheet) {
		
		super(timeSheet, calculationTimeSheet,deductionTimeSheets,bonusPayTimeSheet,specifiedBonusPayTimeSheet,midNighttimeSheet);
		this.workingHoursTimeNo = workingHoursTimeNo;
		this.premiumTimeSheetInPredetermined = Optional.empty();
	}
	
	
	public TimeSpanWithRounding getTimeSheet() {
		return this.timeSheet;
	}
	
	
	/**
	 * 時間帯から遅刻早退時間を控除
	 * @param workTime
	 * @param goWorkTime
	 * @param workNo
	 * @param workTimeCommonSet
	 * @param withinWorkTimeSheet
	 * @param deductionTimeSheet
	 * @param graceTimeSetting
	 * @param workTimeCalcMethodDetailOfHoliday
	 */
	public void deductByLateLeaveEarly(
			WorkTime workTime,
			TimeWithDayAttr goWorkTime,
			TimeWithDayAttr leaveWorkTime,
			int workNo,
			WorkTimeCommonSet workTimeCommonSet,
			WithinWorkTimeSheet withinWorkTimeSheet,
			DeductionTimeSheet deductionTimeSheet,
			GraceTimeSetting graceTimeSetting,
			WorkTimeCalcMethodDetailOfHoliday workTimeCalcMethodDetailOfHoliday) {

		//遅刻時間を計算する
		this.lateTimeSheet.set(LateTimeSheet.lateTimeCalc(
				this,
				workTime.getLateTimeCalcRange(goWorkTime, workNo),
				workTimeCommonSet,
				withinWorkTimeSheet.getlateDecisionClock(workNo),
				deductionTimeSheet));
		
		if (!this.lateTimeSheet.get().isLate()) {
			return;
		}
		
		int lateDeductTime = this.lateTimeSheet.get().getLateDeductionTime();
		
		//就業時間内時間帯から控除するか判断し控除する				
		if(workTimeCalcMethodDetailOfHoliday.deductsFromWithinWorkTimeSheet(lateDeductTime, graceTimeSetting)) {
			//遅刻時間帯の終了時刻を開始時刻にする
			this.timeSheet = this.lateTimeSheet.get().deductForm(this.timeSheet);
		}
		
		this.deductByLate(graceTimeSetting, workTimeCalcMethodDetailOfHoliday);

		
		//早退時間を計算する		
		this.leaveEarlyTimeSheet.set(LeaveEarlyTimeSheet.leaveEarlyTimeCalc(
				this, 
				workTime.getleaveEarlyTimeCalcRange(leaveWorkTime, workNo), 
				workTimeCommonSet, 
				withinWorkTimeSheet.getleaveEarlyDecisionClock(workNo), 
				deductionTimeSheet));
		
		if (!this.leaveEarlyTimeSheet.get().isLeaveEarly()) {
			return;
		}
		
		int leaveEarlyDeductTime = this.leaveEarlyTimeSheet.get().getLeaveEarlyDeductionTime();
		
		//就業時間内時間帯から控除するか判断し控除する				
		if(workTimeCalcMethodDetailOfHoliday.deductsFromWithinWorkTimeSheet(leaveEarlyDeductTime, graceTimeSetting)) {
			//早退時間帯の終了時刻を開始時刻にする
			this.timeSheet = this.lateTimeSheet.get().deductForm(this.timeSheet);
		}
		
		this.deductByLeaveEarly(graceTimeSetting, workTimeCalcMethodDetailOfHoliday);	
		
	}

	/**
	 *  就業時間内時間帯から遅刻分を控除
	 * @param graceTimeSetting
	 * @param workTimeCalcMethodDetailOfHoliday
	 */
	private void deductByLate(GraceTimeSetting graceTimeSetting,
			WorkTimeCalcMethodDetailOfHoliday workTimeCalcMethodDetailOfHoliday) {
		//遅刻時間を計算する
		int lateDeductTime = this.lateTimeSheet.get().getForDeducationTimeSheet().get().lengthAsMinutes();
		
		//就業時間内時間帯から控除するか判断し控除する				
		if(workTimeCalcMethodDetailOfHoliday.deductsFromWithinWorkTimeSheet(lateDeductTime, graceTimeSetting)) {
			//遅刻時間帯の終了時刻を開始時刻にする
			this.timeSheet = this.timeSheet.newTimeSpan(
					this.timeSheet.shiftOnlyStart(this.lateTimeSheet.get().getForDeducationTimeSheet().get().getEnd()));
		}
	}
	
	/**
	 * 就業時間内時間帯から早退分を控除
	 * @param graceTimeSetting
	 * @param workTimeCalcMethodDetailOfHoliday
	 */
	private void deductByLeaveEarly(GraceTimeSetting graceTimeSetting,
			WorkTimeCalcMethodDetailOfHoliday workTimeCalcMethodDetailOfHoliday) {
		//早退時間を計算する
		int leaveEarlyDeductTime = this.leaveEarlyTimeSheet.get().getForDeducationTimeSheet().get().lengthAsMinutes();
		
		//就業時間内時間帯から控除するか判断し控除する				
		if(workTimeCalcMethodDetailOfHoliday.deductsFromWithinWorkTimeSheet(leaveEarlyDeductTime, graceTimeSetting)) {
			//早退時間帯の開始時刻を終了時刻にする
			this.timeSheet = this.timeSheet.newTimeSpan(
					this.timeSheet.shiftOnlyEnd(this.leaveEarlyTimeSheet.get().getForDeducationTimeSheet().get().getStart()));
		}
	}
	

	@Override
	public LateTimeSheet getLateTimeSheet() {
		return this.lateTimeSheet.get();
	}

	@Override
	public LeaveEarlyTimeSheet getLeaveEarlyTimeSheet() {
		return this.leaveEarlyTimeSheet.get();
	}
	
	/**
	 * 実働時間を求め、就業時間を計算する
	 * @param holidayAdditionAtr
	 * @param dedTimeSheet
	 * @return
	 */
	public int calcActualWorkTimeAndWorkTime(HolidayAdditionAtr holidayAdditionAtr,DeductionTimeSheet dedTimeSheet) {
		int actualTime = calcWorkTime(); 
		actualTime = actualTime - dedTimeSheet.calcDeductionAllTimeSheet(DeductionAtr.Deduction, ((CalculationTimeSheet)this).getTimeSheet());
		int workTime = calcActualTime(actualTime);
		/*就業時間算出ロジックをここに*/
		
		return workTime;
	}
	
	/**
	 * 実働時間を計算する
	 * @param actualTime
	 * @return　実働時間
	 */
	public int calcActualTime(int actualTime) {
		int workTime = 0;
		return workTime;
	}
	
	/**
	 * 就業時間を計算する
	 * @return　就業時間
	 */
	public int calcWorkTime() {
		return ((CalculationTimeSheet)this).getTimeSheet().lengthAsMinutes();
	}

	//＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
	
	/**
	 * 計算範囲を判断（流動）　　流動時の就業時間内時間枠
	 */
	public void createWithinWorkTimeFrameForFluid(
			AttendanceLeavingWork attendanceLeavingWork,
			DailyWork dailyWork,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc) {
		TimeSpanForCalc timeSheet = new TimeSpanForCalc(
				attendanceLeavingWork.getAttendance().getEngrave().getTimesOfDay(),
				attendanceLeavingWork.getLeaveWork().getEngrave().getTimesOfDay());
		this.correctTimeSheet(dailyWork, timeSheet, predetermineTimeSetForCalc);
	}
	
	/**
	 * 勤務の単位を基に時間帯の開始、終了を補正
	 * @param dailyWork 1日の勤務
	 */
	public WithinWorkTimeFrame correctTimeSheet(
			DailyWork dailyWork,
			TimeSpanForCalc timeSheet,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc) {
		
		//丸め設定を作成
		Finally<TimeRoundingSetting> rounding = Finally.of(new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN,Rounding.ROUNDING_DOWN));
		
		if (dailyWork.getAttendanceHolidayAttr().isHalfDayWorking()) {
			TimeSpanForCalc timeSheetForRounding = this.getHalfDayWorkingTimeSheetOf(dailyWork.getAttendanceHolidayAttr(),timeSheet,predetermineTimeSetForCalc);
			TimeSpanWithRounding calcRange = new TimeSpanWithRounding(timeSheetForRounding.getStart(),timeSheetForRounding.getEnd(),rounding);
			CalculationTimeSheet t = new CalculationTimeSheet(calcRange,timeSheetForRounding,Optional.empty());
			return new WithinWorkTimeFrame(workingHoursTimeNo,timeSheet,t);
		}
	}

	/**
	 * 午前出勤、午後出勤の判定
	 * @param attr
	 * @return
	 */
	private TimeSpanForCalc getHalfDayWorkingTimeSheetOf(
			AttendanceHolidayAttr attr,
			TimeSpanForCalc timeSheet,
			PredetermineTimeSetForCalc predetermineTimeSetForCalc) {
		switch (attr) {
		case MORNING:
			return new TimeSpanForCalc(timeSheet.getStart(), predetermineTimeSetForCalc.getAMEndTime());
		case AFTERNOON:
			return new TimeSpanForCalc(predetermineTimeSetForCalc.getPMStartTime(),timeSheet.getEnd());
		default:
			throw new RuntimeException("半日専用のメソッドです: " + attr);
		}
	}

		
}
