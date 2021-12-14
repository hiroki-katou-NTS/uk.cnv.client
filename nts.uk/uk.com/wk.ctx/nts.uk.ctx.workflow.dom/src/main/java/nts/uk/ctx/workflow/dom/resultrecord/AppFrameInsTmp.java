package nts.uk.ctx.workflow.dom.resultrecord;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppFrameInsTmp {
	
	private String rootID;
	
	private Integer phaseOrder;
	
	private Integer frameOrder;
	
	private Integer confirmAtr;
	
	public boolean equalOther(AppFrameInsTmp appFrameInsTmp) {
		if(phaseOrder==appFrameInsTmp.getPhaseOrder() &&
			frameOrder==appFrameInsTmp.getFrameOrder() &&
			confirmAtr==appFrameInsTmp.getConfirmAtr()) {
			return true;
		}
		return false;
	}
}
