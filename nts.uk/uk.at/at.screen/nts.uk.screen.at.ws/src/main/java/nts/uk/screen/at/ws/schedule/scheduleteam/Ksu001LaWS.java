package nts.uk.screen.at.ws.schedule.scheduleteam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
		List<EmployeeInfoRequest> empInfoRequest = request.getEmpInfoRequest();
		List<EmployeeOrganizationInfoDto> data = ksu001LaScreenQuery.getEmployeesOrganizationInfo(empInfoRequest.stream().map(i -> i.getId()).collect(Collectors.toList()));
		if(data.isEmpty()) return new ArrayList<>();
		return data.stream().map(e -> {
			EmployeeOrganizationInfoDto dto = new EmployeeOrganizationInfoDto();
			dto.setEmployeeId(e.getEmployeeId());
			dto.setTeamCd(e.getTeamCd());
			dto.setTeamName( e.getTeamName());
			EmployeeInfoRequest emp = empInfoRequest.stream().filter(i -> i.getId().equals(e.getEmployeeId())).findFirst().get();
			dto.setBusinessName(emp.getName());
			dto.setEmployeeCd(emp.getCode());
			return dto;
		}).collect(Collectors.toList());
	}
}
