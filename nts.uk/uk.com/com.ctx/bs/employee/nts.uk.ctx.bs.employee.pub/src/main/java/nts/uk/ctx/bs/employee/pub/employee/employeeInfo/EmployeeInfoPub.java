/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.pub.employee.employeeInfo;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * The Interface EmployeePub.
 */
public interface EmployeeInfoPub {

	/**RequestList18
	 * Find Employee by companyId,employeeCode For RequestList18
	 *
	 */
	Optional<EmployeeInfoDtoExport> getEmployeeInfo(String companyId, String employeeCode);

	/**
	 * Get List Employee by companyId,baseDate For RequestList60
	 * ログイン者の会社IDの一致する全社員を取得する
	 */

	List<EmployeeInfoDtoExport> getEmployeesAtWorkByBaseDate(String companyId, GeneralDate baseDate);

	/**
	 * Get List Employee Infomation For request No.126 param : sid : employeeId
	 *
	 */
	//List<EmpBasicInfoExport> getListEmpBasicInfo(List<String> sid);

	/**RequestList124
	 * Get Employee Info By Pid.
	 * Requets List No.124
	 * @param pid
	 * @return
	 */
	List<EmpInfoExport> getEmpInfoByPid(String pid);
	
	
	/**
	 * Find Employee by companyId,Pid
	 * RequestList No. 
	 */
	Optional<EmployeeInfoDto> getEmployeeInfoByCidPid(String companyId, String personId);

	List<EmployeeInfoDto> findEmployeesMatchingName(List<String> pid, String companyId);

}
