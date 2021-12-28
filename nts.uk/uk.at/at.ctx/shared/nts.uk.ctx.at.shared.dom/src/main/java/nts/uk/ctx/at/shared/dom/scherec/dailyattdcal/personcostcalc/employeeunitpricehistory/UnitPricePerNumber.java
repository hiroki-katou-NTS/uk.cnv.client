package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

/** 割増率毎の単価 */
@Getter
@AllArgsConstructor
public class UnitPricePerNumber {

	/** 単価NO */
	private final UnitPrice no;

	/** 単価 */
	private final WorkingHoursUnitPrice unitPrice;
}
