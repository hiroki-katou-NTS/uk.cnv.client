package nts.uk.screen.at.ws.schedule.scheduleteam;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.at.app.schedule.scheduleteam.EmployeeOrganizationInfoDto;
import nts.uk.screen.at.app.schedule.scheduleteam.Ksu001LaScreenQuery;

/**
 * 
 * @author quytb
 *
 */

@Path("screen/at/schedule/scheduleteam")
@Produces(MediaType.APPLICATION_JSON)
public class Ksu001LaWS extends WebService{
	@Inject
	Ksu001LaScreenQuery ksu001LaScreenQuery;
	
	@POST
	@Path("empOrgInfo")
	public List<EmployeeOrganizationInfoDto> getEmpOrgInfo(Ksu001LaRequest request){
		return ksu001LaScreenQuery.getEmployeesOrganizationInfo(request.toDate(), request.getWorkplaceGroupId());
	}
}
