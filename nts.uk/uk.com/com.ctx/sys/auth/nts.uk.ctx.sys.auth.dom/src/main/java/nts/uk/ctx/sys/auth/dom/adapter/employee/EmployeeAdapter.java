package nts.uk.ctx.sys.auth.dom.adapter.employee;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.sys.auth.dom.employee.dto.EmployeeImport;

public interface EmployeeAdapter {
	
	Optional<EmployeeImport> findByEmpId(String empId);
	
	List<EmployeeImport> findByEmployeeId(String employeeId);
	
	Optional<EmployeeImport> getEmpInfo(String cid, String pid);
	
	/**
	 * 個人の社員情報を取得する
	 * @param personIds 個人IDリスト
	 * @return List<個人社員情報Imported>
	 */
	List<PersonalEmployeeInfoImport> getPersonalEmployeeInfo(List<String> personIds);

}
