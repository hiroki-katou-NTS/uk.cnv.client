package nts.uk.ctx.sys.auth.dom.adapter.employee;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.sys.auth.dom.employee.dto.EmployeeImport;

public interface EmployeeAdapter {
	
	Optional<EmployeeImport> findByEmpId(String empId);
	
	List<EmployeeImport> findByEmployeeId(String employeeId);
	
	Optional<EmployeeImport> getEmpInfo(String cid, String pid);
}
