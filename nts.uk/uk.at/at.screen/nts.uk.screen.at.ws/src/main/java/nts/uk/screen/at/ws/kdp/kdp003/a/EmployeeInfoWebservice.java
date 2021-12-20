package nts.uk.screen.at.ws.kdp.kdp003.a;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.at.app.query.kdp.kdp003.a.EmployeeStampData;
import nts.uk.screen.at.app.query.kdp.kdp003.a.GetListEmployeeHaveBeenStamped;
import nts.uk.screen.at.app.query.kdp.kdp003.a.GetStampInputSystemOutage;
import nts.uk.screen.at.app.query.kdp.kdp003.a.GetStampInputSystemOutageDto;

@Path("at/record/stamp/employment")
@Produces("application/json")
public class EmployeeInfoWebservice extends WebService {
	@Inject
	private GetListEmployeeHaveBeenStamped employeeStamped;
	
	@Inject
	private GetStampInputSystemOutage getStampInputSystemOutage;

	@POST
	@Path("in-workplace")
	public List<EmployeeStampData> listEmployee(@Context HttpServletRequest request, EmployeeQuery query) {
		return employeeStamped.getListEmployee(query.getCompanyId(), query.getWorkplaceIds(), query.getBaseDate());
	}
	
	@POST
	@Path("get-stamp-system-outage")
	public GetStampInputSystemOutageDto getStampInputSystemOutage() {
		return getStampInputSystemOutage.getStampInputSystemOutage();
	}
}