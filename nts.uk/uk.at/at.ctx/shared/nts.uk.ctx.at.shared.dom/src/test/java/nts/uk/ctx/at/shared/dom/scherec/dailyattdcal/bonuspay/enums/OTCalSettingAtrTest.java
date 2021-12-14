package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;

public class OTCalSettingAtrTest {
	
	@Test
	public void isCalculation_CALCULATION() {
		assertThat(OTCalSettingAtr.CALCULATION.isCalculation()).isTrue();
	}
	
	@Test
	public void isCalculation_NON_CALCULATION() {
		assertThat(OTCalSettingAtr.NON_CALCULATION.isCalculation()).isFalse();
	}
	
	@Test
	public void isNonCalculation_NON_CALCULATION() {
		assertThat(OTCalSettingAtr.NON_CALCULATION.isNonCalculation()).isTrue();
	}
	
	@Test
	public void isNonCalculation_FOLLOW_OVERTIME_SETTING() {
		assertThat(OTCalSettingAtr.FOLLOW_OVERTIME_SETTING.isNonCalculation()).isFalse();
	}
	
	@Test
	public void isFollowOvertimeSetting_FOLLOW_OVERTIME_SETTING() {
		assertThat(OTCalSettingAtr.FOLLOW_OVERTIME_SETTING.isFollowOvertimeSetting()).isTrue();
	}
	
	@Test
	public void isFollowOvertimeSetting_CALCULATION() {
		assertThat(OTCalSettingAtr.CALCULATION.isFollowOvertimeSetting()).isFalse();
	}
	
	@Test
	public void isCalc_CALCULATION() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER); //申請または手入力
		boolean result = OTCalSettingAtr.CALCULATION.isCalc(set); //自動計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_NON_CALCULATION() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER); //申請または手入力
		boolean result = OTCalSettingAtr.NON_CALCULATION.isCalc(set); //自動計算しない
		assertThat(result).isFalse();
	}
	
	@Test
	public void isCalc_FOLLOW_OVERTIME_SETTING_True() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS); //打刻から計算する
		boolean result = OTCalSettingAtr.FOLLOW_OVERTIME_SETTING.isCalc(set); //残業の自動計算の設定に従う
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_FOLLOW_OVERTIME_SETTING_False() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER); //申請または手入力
		boolean result = OTCalSettingAtr.FOLLOW_OVERTIME_SETTING.isCalc(set); //残業の自動計算の設定に従う
		assertThat(result).isFalse();
	}

}
