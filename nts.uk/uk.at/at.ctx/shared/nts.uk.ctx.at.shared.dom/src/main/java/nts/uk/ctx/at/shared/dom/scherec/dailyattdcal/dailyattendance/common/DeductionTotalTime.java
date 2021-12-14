package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common;

import lombok.Value;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * 控除合計時間
 * @author keisuke_hoshina
 *
 */
@Value
public class DeductionTotalTime {
	//合計時間
	private TimeWithCalculation totalTime;
	//所定内合計時間
	private TimeWithCalculation withinStatutoryTotalTime;
	//所定外合計時間
	private TimeWithCalculation excessOfStatutoryTotalTime;
	
	/**
	 * Constructor
	 */
	private DeductionTotalTime(TimeWithCalculation totalTime,TimeWithCalculation withinStatutoryTotalTime,TimeWithCalculation excessOfStatutoryTotalTime) {
		this.totalTime                  = totalTime;
		this.withinStatutoryTotalTime   = withinStatutoryTotalTime;
		this.excessOfStatutoryTotalTime = excessOfStatutoryTotalTime;
	}
	
	/**
	 * 控除合計時間の再作成
	 * @return
	 */
	public static DeductionTotalTime of(TimeWithCalculation totalTime,TimeWithCalculation withinStatutoryTotalTime,TimeWithCalculation excessOfStatutoryTotalTime) {
		return new DeductionTotalTime(
									 totalTime
									,withinStatutoryTotalTime
									,excessOfStatutoryTotalTime);
	}
	
	/**
	 * デフォルト値で作成
	 * @return 控除合計時間
	 */
	public static DeductionTotalTime defaultValue() {
		return new DeductionTotalTime(TimeWithCalculation.sameTime(AttendanceTime.ZERO)
										,TimeWithCalculation.sameTime(AttendanceTime.ZERO)
										,TimeWithCalculation.sameTime(AttendanceTime.ZERO));
	}
}
