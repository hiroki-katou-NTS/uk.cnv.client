package nts.uk.ctx.at.shared.dom.scherec.optitem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;

@RunWith(JMockit.class)
public class AmountItemInputUnitTest {

	@Test
	public void getters() {
		AmountItemInputUnit amountItemInputUnit = AmountItemInputUnit.valueOf(0);
		NtsAssert.invokeGetters(amountItemInputUnit);
	}
	
	@Test
	public void test() {
		AmountItemInputUnit amountItemInputUnit = AmountItemInputUnit.valueOf(0);
		assertThat(amountItemInputUnit).isEqualTo(AmountItemInputUnit.ONE);
		amountItemInputUnit = AmountItemInputUnit.valueOf(1);
		assertThat(amountItemInputUnit).isEqualTo(AmountItemInputUnit.TEN);
		amountItemInputUnit = AmountItemInputUnit.valueOf(2);
		assertThat(amountItemInputUnit).isEqualTo(AmountItemInputUnit.ONE_HUNDRED);
		amountItemInputUnit = AmountItemInputUnit.valueOf(3);
		assertThat(amountItemInputUnit).isEqualTo(AmountItemInputUnit.ONE_THOUSAND);
		amountItemInputUnit = AmountItemInputUnit.valueOf(4);
		assertThat(amountItemInputUnit).isEqualTo(AmountItemInputUnit.TEN_THOUSAND);
	}
	
	@Test
	public void testValueEnum() {
		AmountItemInputUnit amountItemInputUnit = AmountItemInputUnit.valueOf(0);
		assertThat(amountItemInputUnit.valueEnum()).isEqualTo(1);
		amountItemInputUnit = AmountItemInputUnit.valueOf(1);
		assertThat(amountItemInputUnit.valueEnum()).isEqualTo(10);
		amountItemInputUnit = AmountItemInputUnit.valueOf(2);
		assertThat(amountItemInputUnit.valueEnum()).isEqualTo(100);
		amountItemInputUnit = AmountItemInputUnit.valueOf(3);
		assertThat(amountItemInputUnit.valueEnum()).isEqualTo(1000);
		amountItemInputUnit = AmountItemInputUnit.valueOf(4);
		assertThat(amountItemInputUnit.valueEnum()).isEqualTo(10000);
	}

}
