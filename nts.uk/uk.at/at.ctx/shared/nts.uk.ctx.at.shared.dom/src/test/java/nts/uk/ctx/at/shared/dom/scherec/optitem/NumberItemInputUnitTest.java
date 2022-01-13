package nts.uk.ctx.at.shared.dom.scherec.optitem;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
@RunWith(JMockit.class)
public class NumberItemInputUnitTest {

	@Test
	public void getters() {
		NumberItemInputUnit numberItemInputUnit = NumberItemInputUnit.valueOf(0);
		NtsAssert.invokeGetters(numberItemInputUnit);
	}

	@Test
	public void test() {
		NumberItemInputUnit numberItemInputUnit = NumberItemInputUnit.valueOf(0);
		assertThat(numberItemInputUnit).isEqualTo(NumberItemInputUnit.ONE_HUNDREDTH);
		numberItemInputUnit = NumberItemInputUnit.valueOf(1);
		assertThat(numberItemInputUnit).isEqualTo(NumberItemInputUnit.ONE_TENTH);
		numberItemInputUnit = NumberItemInputUnit.valueOf(2);
		assertThat(numberItemInputUnit).isEqualTo(NumberItemInputUnit.ONE_HALF);
		numberItemInputUnit = NumberItemInputUnit.valueOf(3);
		assertThat(numberItemInputUnit).isEqualTo(NumberItemInputUnit.ONE);
	}

	@Test
	public void testValueEnum() {
		NumberItemInputUnit numberItemInputUnit = NumberItemInputUnit.valueOf(0);
		assertThat(numberItemInputUnit.valueEnum()).isEqualTo(0.01);
		numberItemInputUnit = NumberItemInputUnit.valueOf(1);
		assertThat(numberItemInputUnit.valueEnum()).isEqualTo(0.1);
		numberItemInputUnit = NumberItemInputUnit.valueOf(2);
		assertThat(numberItemInputUnit.valueEnum()).isEqualTo(0.5);
		numberItemInputUnit = NumberItemInputUnit.valueOf(3);
		assertThat(numberItemInputUnit.valueEnum()).isEqualTo(1);
	}
}
