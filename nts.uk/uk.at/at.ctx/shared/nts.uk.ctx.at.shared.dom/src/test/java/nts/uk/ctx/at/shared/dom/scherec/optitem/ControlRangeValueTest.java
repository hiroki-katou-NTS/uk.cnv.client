package nts.uk.ctx.at.shared.dom.scherec.optitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

public class ControlRangeValueTest {

	@Test
	public void getters() {
		ControlRangeValue controlRangeValue = new ControlRangeValue(Optional.of(new BigDecimal(3000)), Optional.of(new BigDecimal(1000)));
		NtsAssert.invokeGetters(controlRangeValue);
	}
	
	/**
	 * @上限値 is empty
	 * @下限値 is empty
	 */
	@Test
	public void testCheckInputRange_1() {
		ControlRangeValue controlRangeValue = new ControlRangeValue(Optional.empty(), Optional.empty());
		BigDecimal inputValue = new BigDecimal(2000);
		boolean result = controlRangeValue.checkInputRange(inputValue);
		assertThat(result).isTrue();
	}
	
	/**
	 * @上限値.isPresent AND @上限値 < 入力値
	 */
	@Test
	public void testCheckInputRange_2() {
		ControlRangeValue controlRangeValue = new ControlRangeValue(Optional.of(new BigDecimal(3000)), Optional.of(new BigDecimal(1000)));
		BigDecimal inputValue = new BigDecimal(3001);
		boolean result = controlRangeValue.checkInputRange(inputValue);
		assertThat(result).isFalse();
	}
	
	/**
	 * @上限値.isPresent AND @上限値 >= 入力値
	 * @下限値.isPresent AND 入力値 < @下限値
	 */
	@Test
	public void testCheckInputRange_3() {
		ControlRangeValue controlRangeValue = new ControlRangeValue(Optional.of(new BigDecimal(3000)), Optional.of(new BigDecimal(1000)));
		BigDecimal inputValue = new BigDecimal(999);
		boolean result = controlRangeValue.checkInputRange(inputValue);
		assertThat(result).isFalse();
	}
	
	/**
	 * @上限値.isPresent AND @上限値 > 入力値
	 * @下限値.isPresent AND 入力値 > @下限値
	 */
	@Test
	public void testCheckInputRange_4() {
		ControlRangeValue controlRangeValue = new ControlRangeValue(Optional.of(new BigDecimal(3000)), Optional.of(new BigDecimal(1000)));
		BigDecimal inputValue = new BigDecimal(1999);
		boolean result = controlRangeValue.checkInputRange(inputValue);
		assertThat(result).isTrue();
	}
	/**
	 * @上限値.isPresent AND @上限値 >= 入力値
	 * @下限値.isPresent AND 入力値 = @下限値
	 */
	@Test
	public void testCheckInputRange_5() {
		ControlRangeValue controlRangeValue = new ControlRangeValue(Optional.of(new BigDecimal(3000)), Optional.of(new BigDecimal(1000)));
		BigDecimal inputValue = new BigDecimal(1000);
		boolean result = controlRangeValue.checkInputRange(inputValue);
		assertThat(result).isTrue();
	}
	/**
	 * @上限値.isPresent AND @上限値 = 入力値
	 * @下限値.isPresent AND 入力値 > @下限値
	 */
	@Test
	public void testCheckInputRange_6() {
		ControlRangeValue controlRangeValue = new ControlRangeValue(Optional.of(new BigDecimal(3000)), Optional.of(new BigDecimal(1000)));
		BigDecimal inputValue = new BigDecimal(3000);
		boolean result = controlRangeValue.checkInputRange(inputValue);
		assertThat(result).isTrue();
	}

}
