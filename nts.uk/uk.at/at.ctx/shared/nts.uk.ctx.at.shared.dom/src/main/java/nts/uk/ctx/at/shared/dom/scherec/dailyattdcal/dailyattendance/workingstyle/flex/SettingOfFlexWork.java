package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.CalcMethodOfNoWorkingDayForCalc;

/**
 * フレックス勤務の設定
 * @author keisuke_hoshina
 */
@Getter
public class SettingOfFlexWork {
	private FlexCalcMethodOfHalfWork flexCalcMethod;
	/** フレックス勤務の非勤務日の場合の計算方法 */
	private CalcMethodOfNoWorkingDayForCalc calcMethod = CalcMethodOfNoWorkingDayForCalc.isCalculateFlexTime;

	public SettingOfFlexWork(FlexCalcMethodOfHalfWork flexCalcMethod) {
		super();
		this.flexCalcMethod = flexCalcMethod;
	}
	
	/**
	 * デフォルト値で作成する
	 * @return
	 */
	public static SettingOfFlexWork defaultValue() {
		return new SettingOfFlexWork(new FlexCalcMethodOfHalfWork(
				new FlexCalcMethodOfEachPremiumHalfWork(FlexCalcMethod.OneDay, FlexCalcMethod.OneDay),
				new FlexCalcMethodOfEachPremiumHalfWork(FlexCalcMethod.OneDay, FlexCalcMethod.OneDay)));
	}
}
