package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPricePerNumber;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.HowToSetUnitPrice;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PersonCostRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumRate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.Remarks;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

public class PersonCostHelper {
	public static PersonCostCalculation createPersonCostCalc(List<PremiumSetting> premiumSetting, Optional<UnitPrice> unitPrice,
			HowToSetUnitPrice howToSetUnitPrice, UnitPrice workingHoursUnitPrice) {
		return new PersonCostCalculation(
				PersonCostRoundingSetting.defaultValue(),
				"testCompanyId",
				new Remarks("testRemarks"),
				premiumSetting,
				unitPrice,
				howToSetUnitPrice,
				workingHoursUnitPrice,
				"testHystoryId");
	}
	
	public static List<PremiumSetting> createSetUnitPrice() {
		List<PremiumSetting> settings = new ArrayList<>();
		settings.add(createPremiumSetting(ExtraTimeItemNo.ONE, new PremiumRate(100), UnitPrice.Price_1));
		settings.add(createPremiumSetting(ExtraTimeItemNo.TWO, new PremiumRate(100), UnitPrice.Price_2));
		settings.add(createPremiumSetting(ExtraTimeItemNo.THREE, new PremiumRate(100), UnitPrice.Price_3));
		return settings;
	}
	
	public static List<PremiumSetting> createSetPremiumRate() {
		List<PremiumSetting> settings = new ArrayList<>();
		settings.add(createPremiumSetting(ExtraTimeItemNo.ONE, new PremiumRate(100), UnitPrice.Price_1));
		settings.add(createPremiumSetting(ExtraTimeItemNo.TWO, new PremiumRate(125), UnitPrice.Price_1));
		settings.add(createPremiumSetting(ExtraTimeItemNo.THREE, new PremiumRate(135), UnitPrice.Price_1));
		return settings;
	}
	
	public static PremiumSetting createPremiumSetting(ExtraTimeItemNo no, PremiumRate rate, UnitPrice unitPrice) {
		return new PremiumSetting("testCompanyId", "testHystoryId", no, rate, unitPrice, new ArrayList<>());
	}
	
	public static EmployeeUnitPriceHistoryItem createEmployeeUnitPriceHistoryItem() {
		return new EmployeeUnitPriceHistoryItem("testEmployeeId", "testHystoryId", createUnitPricePerNumberList());
	}
	
	public static List<UnitPricePerNumber> createUnitPricePerNumberList() {
		List<UnitPricePerNumber> settings = new ArrayList<>();
		settings.add(new UnitPricePerNumber(UnitPrice.Price_1, new WorkingHoursUnitPrice(1000)));
		settings.add(new UnitPricePerNumber(UnitPrice.Price_2, new WorkingHoursUnitPrice(1100)));
		return settings;
	}
}
