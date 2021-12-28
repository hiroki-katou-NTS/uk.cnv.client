package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;


public class PremiumRateTest {

	@Test
	public void toDecimalTest_min() {
		PremiumRate target = new PremiumRate(1);
		BigDecimal result = target.toDecimal();
		assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(0.01));
	}

	@Test
	public void toDecimalTest_max() {
		PremiumRate target = new PremiumRate(999);
		BigDecimal result = target.toDecimal();
		assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(9.99));
	}
}
