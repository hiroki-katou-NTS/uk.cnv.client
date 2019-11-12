package nts.uk.query.ws.employee;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.query.app.employee.ccg029.Ccg029EmployeeInforDto;
import nts.uk.query.app.employee.ccg029.Ccg029Employeefinder;
import nts.uk.query.app.employee.ccg029.Ccg029QueryParam;
import nts.uk.query.app.employee.ccg029.paramjcm007.Jcm007Param;

@Path("query/ccg029employee")
@Produces(MediaType.APPLICATION_JSON)
public class Ccg029SearchEmployeeWS {

	@Inject
	private Ccg029Employeefinder finder;
	
	@POST
	@Path("find")
	public List<Ccg029EmployeeInforDto> Ccg029SearchEmployee(Ccg029QueryParam query) {
		return finder.employeeKeywordSearch(query);
	}
	
	
	// Get list Employee Info (JCM007)
	@POST
	@Path("getEmpInfo")
	public List<Ccg029EmployeeInforDto> getEmployeeInfo(Jcm007Param query) {
		return finder.getEmployeeInfoByJcm007(query);
	}
}
