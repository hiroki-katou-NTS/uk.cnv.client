package nts.uk.ctx.at.shared.dom.scherec.optitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;

/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class OptionalItemTest {

	@Test
	public void getters() {
		OptionalItem optionalItem = OptionalItemTestHelper.createDefault();
		NtsAssert.invokeGetters(optionalItem);
	}

	/**
	 * no error
	 */
	@Test
	public void testCheckInputValueCorrect_1() {
		OptionalItem optionalItem = OptionalItemTestHelper.createDefault();
		BigDecimal inputValue = new BigDecimal(69);

		List<String> listErrors = new ArrayList<>();
		CheckValueInputCorrectOuput checkValueInputCorrectOuput = new CheckValueInputCorrectOuput(true, listErrors);
		new MockUp<InputControlSetting>() {
			@Mock
			public CheckValueInputCorrectOuput checkValueInputCorrect(BigDecimal inputValue,
					PerformanceAtr performanceAtr, OptionalItemAtr optionalItemAtr) {
				return checkValueInputCorrectOuput;
			}
		};

		CheckValueInputCorrectOuput dataResult = optionalItem.checkInputValueCorrect(inputValue);
		assertThat(dataResult.isCheckResult()).isTrue();
		assertThat(dataResult.getErrorContent()).isEmpty();
	}

	/**
	 * one error
	 */
	@Test
	public void testCheckInputValueCorrect_2() {
		OptionalItem optionalItem = OptionalItemTestHelper.createDefault();
		BigDecimal inputValue = new BigDecimal(69);

		List<String> listErrors = new ArrayList<>();
		listErrors.add("10分の単位で入力してください。");
		CheckValueInputCorrectOuput checkValueInputCorrectOuput = new CheckValueInputCorrectOuput(false, listErrors);
		new MockUp<InputControlSetting>() {
			@Mock
			public CheckValueInputCorrectOuput checkValueInputCorrect(BigDecimal inputValue,
					PerformanceAtr performanceAtr, OptionalItemAtr optionalItemAtr) {
				return checkValueInputCorrectOuput;
			}
		};

		CheckValueInputCorrectOuput dataResult = optionalItem.checkInputValueCorrect(inputValue);
		assertThat(dataResult.isCheckResult()).isFalse();
		assertThat(dataResult.getErrorContent().get(0)).isEqualTo("10分の単位で入力してください。");
	}

	/**
	 * two error
	 */
	@Test
	public void testCheckInputValueCorrect_3() {
		OptionalItem optionalItem = OptionalItemTestHelper.createDefault();
		BigDecimal inputValue = new BigDecimal(69);

		List<String> listErrors = new ArrayList<>();
		listErrors.add("10分の単位で入力してください。");
		listErrors.add("70.0以上の値で入力してください。");
		CheckValueInputCorrectOuput checkValueInputCorrectOuput = new CheckValueInputCorrectOuput(false, listErrors);
		new MockUp<InputControlSetting>() {
			@Mock
			public CheckValueInputCorrectOuput checkValueInputCorrect(BigDecimal inputValue,
					PerformanceAtr performanceAtr, OptionalItemAtr optionalItemAtr) {
				return checkValueInputCorrectOuput;
			}
		};

		CheckValueInputCorrectOuput dataResult = optionalItem.checkInputValueCorrect(inputValue);
		assertThat(dataResult.isCheckResult()).isFalse();
		assertThat(dataResult.getErrorContent().get(0)).isEqualTo("10分の単位で入力してください。");
		assertThat(dataResult.getErrorContent().get(1)).isEqualTo("70.0以上の値で入力してください。");
	}

}
