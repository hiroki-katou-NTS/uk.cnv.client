package nts.uk.ctx.at.shared.dom.workrule.outsideworktime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.TimeItemTypeAtr;

public class AutoCalRaisingSalarySettingTest {

	@Test
	public void isCalc_NORMAL_TYPE() {
		AutoCalRaisingSalarySetting target = new AutoCalRaisingSalarySetting(false, true);
		assertThat(target.isCalc(TimeItemTypeAtr.NORMAL_TYPE)).isTrue();
	}
	
	@Test
	public void isCalc_SPECIAL_TYPE() {
		AutoCalRaisingSalarySetting target = new AutoCalRaisingSalarySetting(false, true);
		assertThat(target.isCalc(TimeItemTypeAtr.SPECIAL_TYPE)).isFalse();
	}
}
