package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeWithCalculation;

/**
 * フレックス時間
 * @author keisuke_hoshina
 *
 */
@Getter
public class FlexTime implements Cloneable{
	//フレックス時間
	private TimeDivergenceWithCalculationMinusExist flexTime;
	//申請時間
	@Setter
	private AttendanceTime beforeApplicationTime; 
	
	/**
	 * Constructor
	 * @param flexTime
	 * @param beforeApplicationTime
	 */
	public FlexTime(TimeDivergenceWithCalculationMinusExist flexTime, AttendanceTime beforeApplicationTime) {
		super();
		this.flexTime = flexTime;
		this.beforeApplicationTime = beforeApplicationTime;
	}
	
	
	public TimeWithCalculation getNotMinusFlexTime() {
		return TimeWithCalculation.createTimeWithCalculation(new AttendanceTime(this.flexTime.getTime() == null ? 0 :this.flexTime.getTime().valueAsMinutes()),
				 new AttendanceTime(this.flexTime.getCalcTime()==null ? 0 : this.flexTime.getCalcTime().valueAsMinutes()));
	}
	
	public void setFlexTime(AttendanceTimeOfExistMinus flexTime, AttendanceTimeOfExistMinus calcFlexTime) {
		this.setOnlyFlexTime(flexTime);
		this.setOnlyCalcFlexTime(calcFlexTime);
	}
	
	/**
	 * フレックス時間のみのセッター(計算フレに入れない)
	 * @param flexTime
	 */
	public void setOnlyFlexTime(AttendanceTimeOfExistMinus flexTime) {
		this.flexTime = TimeDivergenceWithCalculationMinusExist.createTimeWithCalculation(flexTime, this.flexTime.getCalcTime());
	}
	
	/**
	 * 計算フレックス時間のみのセッター(フレに入れない)
	 * @param flexTime
	 */
	public void setOnlyCalcFlexTime(AttendanceTimeOfExistMinus calcFlexTime) {
		this.flexTime = TimeDivergenceWithCalculationMinusExist.createTimeWithCalculation(this.flexTime.getTime(), calcFlexTime);
	}
	
	/**
	 * 実績超過乖離時間の計算
	 * @return
	 */
	public int calcOverLimitDivergenceTime() {
		return this.getFlexTime().getDivergenceTime().valueAsMinutes();
	}

	/**
	 * 実績超過乖離時間が発生しているか判定する
	 * @return 乖離時間が発生している
	 */
	public boolean isOverLimitDivergenceTime() {
		return this.calcOverLimitDivergenceTime() > 0 ? true:false;
	}
	
	/**
	 * 事前申請超過時間の計算
	 * @return
	 */
	public int calcPreOverLimitDivergenceTime() {
		return this.getFlexTime().getTime().valueAsMinutes() - this.beforeApplicationTime.valueAsMinutes();
	}

	/**
	 * 事前申請超過時間が発生しているか判定する
	 * @return 乖離時間が発生している
	 */
	public boolean isPreOverLimitDivergenceTime() {
		return this.calcPreOverLimitDivergenceTime() > 0 ? true:false;
	}
	
	/**
	 * 乖離時間のみ再計算
	 * @return
	 */
	public FlexTime calcDiverGenceTime() {
		TimeDivergenceWithCalculationMinusExist calcedDiverGenceTime = this.flexTime==null?this.flexTime:this.flexTime.calcDiverGenceTime();
		return new FlexTime(calcedDiverGenceTime,this.beforeApplicationTime);
	}
	
	@Override
	public FlexTime clone() {
		return new FlexTime(flexTime.clone(), new AttendanceTime(beforeApplicationTime.v()));
	}
	
	public AttendanceTime getFlexOverTime() {
			return new AttendanceTime(this.getFlexTime().getTime().v());
	}
}
