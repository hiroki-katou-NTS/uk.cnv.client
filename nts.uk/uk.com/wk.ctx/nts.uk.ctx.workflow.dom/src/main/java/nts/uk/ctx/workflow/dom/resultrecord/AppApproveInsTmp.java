package nts.uk.ctx.workflow.dom.resultrecord;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppApproveInsTmp {
	
	private String rootID;

	private Integer phaseOrder;

	private Integer frameOrder;
	
	private String approverChildID;
	
	public boolean equalOther(AppApproveInsTmp appApproveInsTmp) {
		if(phaseOrder==appApproveInsTmp.getPhaseOrder() &&
			frameOrder==appApproveInsTmp.getFrameOrder() &&
			approverChildID.equals(appApproveInsTmp.getApproverChildID())) {
			return true;
		}
		return false;
	}
}
