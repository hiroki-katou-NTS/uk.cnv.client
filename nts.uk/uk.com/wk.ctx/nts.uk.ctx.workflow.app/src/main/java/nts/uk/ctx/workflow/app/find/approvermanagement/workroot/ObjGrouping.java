package nts.uk.ctx.workflow.app.find.approvermanagement.workroot;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ObjGrouping {
	private String startDate;
	private String endDate;
	private List<String> lstApprovalId;
}
