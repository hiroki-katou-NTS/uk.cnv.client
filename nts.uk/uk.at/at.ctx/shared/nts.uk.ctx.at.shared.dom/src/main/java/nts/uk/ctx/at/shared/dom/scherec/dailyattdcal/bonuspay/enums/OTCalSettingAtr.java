/**
 * 10:23:19 AM Jun 6, 2017
 */
package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums;

import lombok.AllArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;

/**
 * @author hungnm
 *
 */
@AllArgsConstructor
public enum OTCalSettingAtr {
	
	// 自動計算しない
	NON_CALCULATION(0, "Enum_AutoCalcAtr_NonCalculation"),
	// 自動計算する
	CALCULATION(1, "Enum_AutoCalcAtr_Calculation"),
	// 残業の自動計算の設定に従う
	FOLLOW_OVERTIME_SETTING(2, "Enum_AutoCalcAtr_FollowOTSetting");

	public final int value;

	public final String nameId;
	
	/**
	 * 自動計算しないか
	 * @return 自動計算しない
	 */
	public boolean isNonCalculation() {
		return this.equals(NON_CALCULATION);
	}
	
	/**
	 * 自動計算するか
	 * @return 自動計算する
	 */
	public boolean isCalculation() {
		return this.equals(CALCULATION);
	}
	
	/**
	 * 残業の自動計算の設定に従うか
	 * @return 残業の自動計算の設定に従う
	 */
	public boolean isFollowOvertimeSetting() {
		return this.equals(FOLLOW_OVERTIME_SETTING);
	}
	
	/**
	 * 計算するか
	 * @param set 自動計算設定
	 * @return true:計算する、false:計算しない
	 */
	public boolean isCalc(AutoCalSetting set) {
		if(this.isCalculation()) {
			return true;
		}
		if(this.isFollowOvertimeSetting()) {
			return set.getCalAtr().isCalculateEmbossing();
		}
		return false;
	}
}
