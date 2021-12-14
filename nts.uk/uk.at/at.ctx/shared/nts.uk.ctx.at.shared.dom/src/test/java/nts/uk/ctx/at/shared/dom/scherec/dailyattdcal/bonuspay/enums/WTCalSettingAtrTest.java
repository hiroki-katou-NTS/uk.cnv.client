package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class WTCalSettingAtrTest {

	@Test
	public void isCalculation_CALCULATION() {
		assertThat(WTCalSettingAtr.CALCULATION.isCalculation()).isTrue();
	}
	
	@Test
	public void isCalculation_NON_CALCULATION() {
		assertThat(WTCalSettingAtr.NON_CALCULATION.isCalculation()).isFalse();
	}
}
