/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.ot.autocalsetting.wkp;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;

/**
 * Gets the cal atr.
 *
 * @return the cal atr
 */
@Getter

/**
 * Sets the cal atr.
 *
 * @param calAtr the new cal atr
 */
@Setter
public class AutoCalSettingDto {

	/** The up limit ot set. */
	// 上限残業時間の設定
	private Integer upLimitOtSet;

	/** The cal atr. */
	/// 計算区分
	private Integer calAtr;

	public AutoCalSettingDto(Integer upLimitOtSet, Integer calAtr) {
		super();
		this.upLimitOtSet = upLimitOtSet;
		this.calAtr = calAtr;
	}
	
	public AutoCalSettingDto() {
		super();
	}

	public static AutoCalSettingDto fromDomain(AutoCalSetting domain) {

		return new AutoCalSettingDto(domain.getUpLimitORtSet().value, domain.getCalAtr().value);
	}

	
}
