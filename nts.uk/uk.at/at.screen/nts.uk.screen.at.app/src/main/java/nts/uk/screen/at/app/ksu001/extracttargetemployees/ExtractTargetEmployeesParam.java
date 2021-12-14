package nts.uk.screen.at.app.ksu001.extracttargetemployees;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

@Value
public class ExtractTargetEmployeesParam {

	public GeneralDate baseDate;
	public TargetOrgIdenInfor targetOrgIdenInfor;
}
