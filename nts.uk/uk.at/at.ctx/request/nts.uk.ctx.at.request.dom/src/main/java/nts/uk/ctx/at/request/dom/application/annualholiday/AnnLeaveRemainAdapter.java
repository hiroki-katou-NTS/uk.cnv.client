package nts.uk.ctx.at.request.dom.application.annualholiday;

import nts.arc.time.GeneralDate;

public interface AnnLeaveRemainAdapter {
	ReNumAnnLeaReferenceDateExport getReferDateAnnualLeaveRemainNumber(String employeeID,GeneralDate date);
}
