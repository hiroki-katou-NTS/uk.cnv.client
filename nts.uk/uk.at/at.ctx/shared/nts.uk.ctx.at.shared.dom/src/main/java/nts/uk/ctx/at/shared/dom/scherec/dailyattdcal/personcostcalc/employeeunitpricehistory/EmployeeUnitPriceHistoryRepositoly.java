package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.util.Optional;

import nts.arc.time.GeneralDate;

public interface EmployeeUnitPriceHistoryRepositoly {

	public Optional<EmployeeUnitPriceHistoryItem> get(String sId, GeneralDate baseDate);
}
