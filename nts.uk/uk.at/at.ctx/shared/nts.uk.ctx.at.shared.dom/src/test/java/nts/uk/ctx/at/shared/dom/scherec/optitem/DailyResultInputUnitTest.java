package nts.uk.ctx.at.shared.dom.scherec.optitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.shr.com.i18n.TextResource;
/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class DailyResultInputUnitTest {

	@Test
	public void getters() {
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(Optional.of(TimeItemInputUnit.ONE_MINUTE),
				Optional.empty(), Optional.empty());
		NtsAssert.invokeGetters(dailyResultInputUnit);

	}
	
	/**
	 * 任意項目の属性 = 時間
	 * INPUT「入力値」 %  返ってきた結果 == 0  && 時間項目の入力単位  == 30分
	 */
	@Test
	public void testCheckInputUnit_1() {
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(
				Optional.of(TimeItemInputUnit.THIRTY_MINUTES), Optional.empty(), Optional.empty());
		BigDecimal inputValue = new BigDecimal(930);
		OptionalItemAtr optionalItemAtr = OptionalItemAtr.TIME;
		
		ValueCheckResult dataResult = dailyResultInputUnit.checkInputUnit(inputValue, optionalItemAtr);
		assertThat(dataResult.isCheckResult()).isTrue();
		assertThat(dataResult.getErrorContent()).isEmpty();

	}
	
	/**
	 * 任意項目の属性 = 時間
	 * INPUT「入力値」 %  返ってきた結果 != 0  && 時間項目の入力単位  == 30分
	 */
	@Test
	public void testCheckInputUnit_2(@Mocked final TextResource tr) {
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(
				Optional.of(TimeItemInputUnit.THIRTY_MINUTES), Optional.empty(), Optional.empty());
		BigDecimal inputValue = new BigDecimal(934);
		OptionalItemAtr optionalItemAtr = OptionalItemAtr.TIME;
		
		new Expectations() {
            {
            	TextResource.localize("Msg_2290","30分");
            	result =  "30分の単位で入力してください。";
            }
        };
		
		
		ValueCheckResult dataResult = dailyResultInputUnit.checkInputUnit(inputValue, optionalItemAtr);
		assertThat(dataResult.isCheckResult()).isFalse();
		assertThat(dataResult.getErrorContent().get()).isEqualTo(TextResource.localize("Msg_2290","30分"));

	}
	
	/**
	 * 任意項目の属性 = 回数
	 * INPUT「入力値」 %  返ってきた結果 == 0  && 回数項目の入力単位  == 0.5回
	 */
	@Test
	public void testCheckInputUnit_3() {
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(Optional.empty(),
				Optional.of(NumberItemInputUnit.ONE_HALF), Optional.empty());
		BigDecimal inputValue = new BigDecimal("3.5");
		OptionalItemAtr optionalItemAtr = OptionalItemAtr.NUMBER;
		
		ValueCheckResult dataResult = dailyResultInputUnit.checkInputUnit(inputValue, optionalItemAtr);
		assertThat(dataResult.isCheckResult()).isTrue();
		assertThat(dataResult.getErrorContent()).isEmpty();

	}
	
	/**
	 * 任意項目の属性 = 回数
	 * INPUT「入力値」 %  返ってきた結果 != 0  && 回数項目の入力単位  == 0.5回
	 */
	@Test
	public void testCheckInputUnit_4(@Mocked final TextResource tr) {
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(Optional.empty(),
				Optional.of(NumberItemInputUnit.ONE_HALF), Optional.empty());
		BigDecimal inputValue = new BigDecimal("3.6");
		OptionalItemAtr optionalItemAtr = OptionalItemAtr.NUMBER;

		new Expectations() {
            {
            	TextResource.localize("Msg_2290","0.5回");
            	result =  "0.5回の単位で入力してください。";
            }
        };
		
		
		ValueCheckResult dataResult = dailyResultInputUnit.checkInputUnit(inputValue, optionalItemAtr);
		assertThat(dataResult.isCheckResult()).isFalse();
		assertThat(dataResult.getErrorContent().get()).isEqualTo(TextResource.localize("Msg_2290","0.5回"));

	}
	
	/**
	 * 任意項目の属性 = 金額
	 * INPUT「入力値」 %  返ってきた結果 == 0  && 金額項目の入力単位  == 100
	 */
	@Test
	public void testCheckInputUnit_5() {
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(Optional.empty(),
				Optional.empty(), Optional.of(AmountItemInputUnit.ONE_HUNDRED));
		BigDecimal inputValue = new BigDecimal(500);
		OptionalItemAtr optionalItemAtr = OptionalItemAtr.AMOUNT;
		
		ValueCheckResult dataResult = dailyResultInputUnit.checkInputUnit(inputValue, optionalItemAtr);
		assertThat(dataResult.isCheckResult()).isTrue();
		assertThat(dataResult.getErrorContent()).isEmpty();

	}
	
	/**
	 * 任意項目の属性 = 金額
	 * INPUT「入力値」 %  返ってきた結果 != 0  && 金額項目の入力単位  == 100
	 */
	@Test
	public void testCheckInputUnit_6(@Mocked final TextResource tr) {
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(Optional.empty(),
				Optional.empty(), Optional.of(AmountItemInputUnit.ONE_HUNDRED));
		BigDecimal inputValue = new BigDecimal(566);
		OptionalItemAtr optionalItemAtr = OptionalItemAtr.AMOUNT;

		new Expectations() {
            {
            	TextResource.localize("Msg_2290","100");
            	result =  "100の単位で入力してください。";
            }
        };
		
		
		ValueCheckResult dataResult = dailyResultInputUnit.checkInputUnit(inputValue, optionalItemAtr);
		assertThat(dataResult.isCheckResult()).isFalse();
		assertThat(dataResult.getErrorContent().get()).isEqualTo(TextResource.localize("Msg_2290","100"));

	}

}
