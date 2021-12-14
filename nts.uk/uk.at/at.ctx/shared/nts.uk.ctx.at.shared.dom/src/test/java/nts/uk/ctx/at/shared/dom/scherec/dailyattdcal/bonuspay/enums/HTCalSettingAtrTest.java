package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;

public class HTCalSettingAtrTest {
	
	@Test
	public void isCalculation_CALCULATION() {
		assertThat(HTCalSettingAtr.CALCULATION.isCalculation()).isTrue();
	}
	
	@Test
	public void isCalculation_NON_CALCULATION() {
		assertThat(HTCalSettingAtr.NON_CALCULATION.isCalculation()).isFalse();
	}
	
	@Test
	public void isNonCalculation_NON_CALCULATION() {
		assertThat(HTCalSettingAtr.NON_CALCULATION.isNonCalculation()).isTrue();
	}
	
	@Test
	public void isNonCalculation_FOLLOW_HOLIDAY_SETTING() {
		assertThat(HTCalSettingAtr.FOLLOW_HOLIDAY_SETTING.isNonCalculation()).isFalse();
	}
	
	@Test
	public void isFollowHolidaySetting_FOLLOW_HOLIDAY_SETTING() {
		assertThat(HTCalSettingAtr.FOLLOW_HOLIDAY_SETTING.isFollowHolidaySetting()).isTrue();
	}
	
	@Test
	public void isFollowHolidaySetting_CALCULATION() {
		assertThat(HTCalSettingAtr.CALCULATION.isFollowHolidaySetting()).isFalse();
	}
	
	@Test
	public void isCalc_CALCULATION() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER); //申請または手入力
		boolean result = HTCalSettingAtr.CALCULATION.isCalc(set); //自動計算する
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_NON_CALCULATION() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER); //申請または手入力
		boolean result = HTCalSettingAtr.NON_CALCULATION.isCalc(set); //自動計算しない
		assertThat(result).isFalse();
	}
	
	@Test
	public void isCalc_FOLLOW_HOLIDAY_SETTING_True() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.CALCULATEMBOSS); //打刻から計算する
		boolean result = HTCalSettingAtr.FOLLOW_HOLIDAY_SETTING.isCalc(set); //休出の自動計算の設定に従う
		assertThat(result).isTrue();
	}
	
	@Test
	public void isCalc_FOLLOW_HOLIDAY_SETTING_False() {
		AutoCalSetting set = new AutoCalSetting(TimeLimitUpperLimitSetting.NOUPPERLIMIT, AutoCalAtrOvertime.APPLYMANUALLYENTER); //申請または手入力
		boolean result = HTCalSettingAtr.FOLLOW_HOLIDAY_SETTING.isCalc(set); //休出の自動計算の設定に従う
		assertThat(result).isFalse();
	}
}
