package nts.uk.ctx.bs.employee.ws.employee.data.management.contact;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.bs.employee.app.command.employee.data.management.EmployeeManagementInfor;
import nts.uk.ctx.bs.employee.app.command.employee.data.management.EmployeeManagementInforDto;
import nts.uk.ctx.bs.employee.app.command.employee.data.management.EmployeeManagementInforInput;

/**
 * 
 * @author chungnt
 *
 */

@Path("ctx/sys/employee/login")
@Produces("application/json")
public class CheckEmployeeLoginWs extends WebService {

	@Inject
	private EmployeeManagementInfor employeeManagementInfor;
	
	
	@POST
	@Path("verify_employeecode")
	public EmployeeManagementInforDto verifiLoginNotPassword(EmployeeManagementInforInput param) {
		return this.employeeManagementInfor.getEmployeeManagementInfor(param.getCid().toString(), param.getEmployeeCode().toString());
	}
}
