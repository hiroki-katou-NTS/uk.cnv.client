package nts.uk.ctx.pereg.ws.reginfo.addemployee;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.bs.employee.app.find.employee.EmpInfoDto;
import nts.uk.ctx.bs.employee.app.find.employee.EmployeeFinder;
import nts.uk.ctx.bs.employee.app.find.employee.validateEmpInfoResultDto;
import nts.uk.ctx.pereg.app.command.reginfo.addemployee.AddEmployeeCommand;
import nts.uk.ctx.pereg.app.command.reginfo.addemployee.AddEmployeeCommandHandler;

public class AddEmployeeWebService extends WebService {
	@Inject
	AddEmployeeCommandHandler addEmpHandler;

	@Inject
	private EmployeeFinder employeeFinder;

	// sonnlb code start

	@POST
	@Path("getGenerateEmplCodeAndComId")
	public JavaTypeResult<String> getGenerateEmplCodeAndComId(String startLetters) {
		return new JavaTypeResult<String>(this.employeeFinder.getGenerateEmplCodeAndComId(startLetters));
	}

	@POST
	@Path("validateEmpInfo")
	public validateEmpInfoResultDto validateEmpInfo(EmpInfoDto empInfo) {

		return this.employeeFinder.validateEmpInfo(empInfo);
	}

	// sonnlb code end

	// sonnlb start
	@POST
	@Path("addNewEmployee")
	public void addNewEmployee(AddEmployeeCommand command) {
		this.addEmpHandler.handle(command);
	}

	// sonnlb end
}
