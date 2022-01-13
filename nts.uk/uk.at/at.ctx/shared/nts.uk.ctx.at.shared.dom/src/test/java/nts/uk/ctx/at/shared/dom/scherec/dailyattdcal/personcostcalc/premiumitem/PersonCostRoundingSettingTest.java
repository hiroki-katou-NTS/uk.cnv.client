package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRounding;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.amountrounding.AmountUnit;

public class PersonCostRoundingSettingTest {

	@Test
	public void getters() {
		PersonCostRoundingSetting target = PersonCostRoundingSetting.defaultValue();
		NtsAssert.invokeGetters(target);
	}
	
	@Test
	public void roundWorkTimeAmountTest() {
		PersonCostRoundingSetting target = new PersonCostRoundingSetting(
				new UnitPriceRoundingSetting(UnitPriceRounding.TRUNCATION),
				new AmountRoundingSetting(AmountUnit.ONE_HUNDRED_YEN, AmountRounding.ROUND_UP));//100円切り上げ
		AttendanceAmountDaily result = target.roundWorkTimeAmount(BigDecimal.valueOf(945));
		assertThat(result).isEqualTo(new AttendanceAmountDaily(1000));
	}
}
