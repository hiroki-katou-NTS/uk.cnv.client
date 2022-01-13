package nts.uk.ctx.at.shared.dom.scherec.optitem;

import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.CompanyId;

public class OptionalItemTestHelper {

	public static OptionalItem createDefault() {
		CalcResultRange calcResultRange = new CalcResultRange(CalcRangeCheck.NOT_SET, CalcRangeCheck.NOT_SET, Optional.empty(), Optional.empty(),Optional.empty());
		DailyResultInputUnit dailyResultInputUnit = new DailyResultInputUnit(Optional.of(TimeItemInputUnit.TEN_MINUTES),
				Optional.empty(), Optional.empty());
		InputControlSetting inputControlSetting = new InputControlSetting(true, calcResultRange, Optional.of(dailyResultInputUnit));
		OptionalItem optionalItem = new OptionalItem(
				new CompanyId("companyId"),
				new OptionalItemNo(1),
				new OptionalItemName("optionalItemName"),
				OptionalItemUsageAtr.USE,
				EmpConditionAtr.WITH_CONDITION, 
				PerformanceAtr.DAILY_PERFORMANCE, 
				OptionalItemAtr.AMOUNT, 
				inputControlSetting, 
				Optional.empty(), 
				CalculationClassification.CALC,
				Optional.empty(),
				Optional.empty());
		return optionalItem;
	}
}
