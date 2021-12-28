package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.PersonCostHelper;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;

public class PersonCostCalculationTest {

	@Test
	public void getters() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		NtsAssert.invokeGetters(target);
	}
	
	@Test
	public void isIncludeTotalTimeTest_true() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		assertThat(target.isIncludeTotalTime(ExtraTimeItemNo.ONE)).isTrue();
	}
	
	@Test
	public void isIncludeTotalTimeTest_false() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		assertThat(target.isIncludeTotalTime(ExtraTimeItemNo.FOUR)).isFalse();
	}

	@Test
	public void getPremiumUnitPriceTest_success() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		WorkingHoursUnitPrice result = target.getPremiumUnitPrice(ExtraTimeItemNo.ONE, PersonCostHelper.createEmployeeUnitPriceHistoryItem());
		assertThat(result).isEqualTo(new WorkingHoursUnitPrice(1000));
	}
	
	@Test
	public void getPremiumUnitPriceTest_emptyExtraTimeItemNo() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		WorkingHoursUnitPrice result = target.getPremiumUnitPrice(ExtraTimeItemNo.FOUR, PersonCostHelper.createEmployeeUnitPriceHistoryItem());
		assertThat(result).isEqualTo(WorkingHoursUnitPrice.ZERO);
	}
	
	@Test
	public void getPremiumUnitPriceTest_emptyUnitPriceNo() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		WorkingHoursUnitPrice result = target.getPremiumUnitPrice(ExtraTimeItemNo.THREE, PersonCostHelper.createEmployeeUnitPriceHistoryItem());
		assertThat(result).isEqualTo(WorkingHoursUnitPrice.ZERO);
	}

	@Test
	public void getWorkTimeUnitPriceTest_success() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		WorkingHoursUnitPrice result = target.getWorkTimeUnitPrice(PersonCostHelper.createEmployeeUnitPriceHistoryItem());
		assertThat(result).isEqualTo(new WorkingHoursUnitPrice(1000));
	}

	@Test
	public void getWorkTimeUnitPriceTest_empty() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_3);
		WorkingHoursUnitPrice result = target.getWorkTimeUnitPrice(PersonCostHelper.createEmployeeUnitPriceHistoryItem());
		assertThat(result).isEqualTo(WorkingHoursUnitPrice.ZERO);
	}

	@Test
	public void getPremiumSettingTest_success() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		Optional<PremiumSetting> result = target.getPremiumSetting(ExtraTimeItemNo.TWO);
		assertThat(result.get().getUnitPrice()).isEqualTo(UnitPrice.Price_2);
	}

	@Test
	public void getPremiumSettingTest_empty() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(), Optional.empty(), HowToSetUnitPrice.SET_UNIT_PRICE, UnitPrice.Price_1);
		Optional<PremiumSetting> result = target.getPremiumSetting(ExtraTimeItemNo.FOUR);
		assertThat(result.isPresent()).isFalse();
	}

	@Test
	public void getUnitPriceAsJudgedTest_setPremiumRate() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetPremiumRate(),
				Optional.of(UnitPrice.Price_3),
				HowToSetUnitPrice.SET_PREMIUM_RATE,
				UnitPrice.Price_1);
		Optional<UnitPrice> result = target.getUnitPriceAsJudged(ExtraTimeItemNo.TWO);
		assertThat(result.get()).isEqualTo(UnitPrice.Price_3);
	}

	@Test
	public void getUnitPriceAsJudgedTest_setUnitPrice() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(),
				Optional.of(UnitPrice.Price_3),
				HowToSetUnitPrice.SET_UNIT_PRICE,
				UnitPrice.Price_1);
		Optional<UnitPrice> result = target.getUnitPriceAsJudged(ExtraTimeItemNo.TWO);
		assertThat(result.get()).isEqualTo(UnitPrice.Price_2);
	}

	@Test
	public void getUnitPriceAsJudgedTest_empty() {
		PersonCostCalculation target = PersonCostHelper.createPersonCostCalc(
				PersonCostHelper.createSetUnitPrice(),
				Optional.of(UnitPrice.Price_3),
				HowToSetUnitPrice.SET_UNIT_PRICE,
				UnitPrice.Price_1);
		Optional<UnitPrice> result = target.getUnitPriceAsJudged(ExtraTimeItemNo.FOUR);
		assertThat(result.isPresent()).isFalse();
	}
}
