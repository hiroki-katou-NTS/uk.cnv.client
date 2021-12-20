package nts.uk.screen.com.app.find.cmm015;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.ctx.workflow.app.query.approvermanagement.ApprovalRouteSpStatusQuery;
import nts.uk.query.pub.employee.EmployeeInformationExport;

@AllArgsConstructor
@Getter
public class TransfereeOnApproved {

	// List<承認ルート状況>
	private List<ApprovalRouteSpStatusQuery> approvalRoutes;
	
	// List<社員情報>
	private List<EmployeeInformationExport> empInfors;
	
	// List<職場情報一覧>
	private List<WorkplaceInforParam> wkpListInfo;
}
