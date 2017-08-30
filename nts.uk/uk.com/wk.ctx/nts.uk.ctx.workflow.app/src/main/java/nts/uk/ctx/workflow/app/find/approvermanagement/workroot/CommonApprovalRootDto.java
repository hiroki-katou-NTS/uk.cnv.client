package nts.uk.ctx.workflow.app.find.approvermanagement.workroot;

import java.util.List;

import lombok.Value;
@Value
public class CommonApprovalRootDto {
	List<CompanyAppRootDto> lstCompanyRoot;
	List<WorkPlaceAppRootDto> lstWorkplaceRoot;
	List<PersonAppRootDto> lstPersonRoot;
}
