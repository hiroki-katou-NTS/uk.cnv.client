package nts.uk.ctx.workflow.app.find.approvermanagement.workroot;

import java.util.List;

import lombok.Value;
@Value
public class DataFullDto {
	private List<DataDisplayComDto> lstCompany;
	private List<WorkPlaceAppRootDto> lstWorkplaceRoot;
	private List<PersonAppRootDto> lstPersonRoot;

}
