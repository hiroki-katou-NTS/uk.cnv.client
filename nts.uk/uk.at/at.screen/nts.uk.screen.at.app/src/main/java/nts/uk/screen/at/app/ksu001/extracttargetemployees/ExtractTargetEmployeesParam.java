package nts.uk.screen.at.app.ksu001.extracttargetemployees;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

@Value
public class ExtractTargetEmployeesParam {

	public GeneralDate systemDate; // update ver5.11
	public DatePeriod period; // update ver5.11
	public TargetOrgIdenInfor targetOrgIdenInfor;
}
