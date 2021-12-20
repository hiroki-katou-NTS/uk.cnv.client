package nts.uk.ctx.at.shared.app.command.dailyattdcal.empunitpricehistory;

import lombok.Getter;
import nts.uk.shr.pereg.app.PeregEmployeeId;
import nts.uk.shr.pereg.app.PeregRecordId;

@Getter
public class DeleteEmployeeUnitPriceCommand {

	@PeregRecordId
	private String histId;
	
	@PeregEmployeeId
	private String employeeId;
}
