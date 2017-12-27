package nts.uk.ctx.at.shared.dom.bonuspay.enums;

/**
 * 休出時間帯自動計算区分
 * @author keisuke_hoshina
 *
 */
public enum HolidayTimesheetCalculationSetting {
	CalculateAutomatical,
	NotAutoCalculation,
	AccordingToAutoCalcSetOfHolidayWork;
	
	/**
	 * 自動計算するかどうか判定する
	 * @return　自動計算する
	 */
	public boolean isCalculateAutomatical() {
		return CalculateAutomatical.equals(this);
	}
}
