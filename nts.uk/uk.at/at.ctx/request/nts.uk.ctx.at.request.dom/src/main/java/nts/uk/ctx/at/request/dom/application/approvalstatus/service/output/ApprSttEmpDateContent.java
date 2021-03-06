package nts.uk.ctx.at.request.dom.application.approvalstatus.service.output;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.applist.service.ApplicationTypeDisplay;

/**
 * refactor 5
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@Getter
public class ApprSttEmpDateContent {
	private Application application;
	
	private String content;
	
	private ReflectedState reflectedState;
	
	private List<PhaseApproverStt> phaseApproverSttLst;
	
	private Optional<ApplicationTypeDisplay> opAppTypeDisplay;
}
