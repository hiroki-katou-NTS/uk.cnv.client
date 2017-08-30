/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.pub.employee;

import java.util.List;

import nts.arc.time.GeneralDate;

/**
 * The Interface EmployeePub.
 */
public interface EmployeePub {

	/**
	 * Find wpk ids by S code.
	 *
	 * @param companyId the company id
	 * @param employeeId the employee code
	 * @param date the date
	 * @return the list
	 */
	// RequestList #65
	List<String> findWpkIdsBySid(String companyId, String employeeId, GeneralDate date);

	/**
	 * Gets the workplace id.
	 *
	 * @param companyId the company id
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 * @return the workplace id
	 */
	String getWorkplaceId(String companyId,String employeeId, GeneralDate baseDate);

	/**
	 * Gets the employment code.
	 *
	 * @param companyId the company id
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 * @return the employment code
	 */
	String getEmploymentCode(String companyId, String employeeId, GeneralDate baseDate);

	/**
	 * Find by wpk ids.
	 *
	 * @param companyId the company id
	 * @param workplaceIds the workplace ids
	 * @param baseDate the base date
	 * @return the list
	 */
	List<EmployeeDto> findByWpkIds(String companyId, List<String> workplaceIds, GeneralDate baseDate);

	/**
	 * Find job title by sid.
	 *
	 * @param employeeId the employee id
	 * @return the list
	 */
	// RequestList #17
	List<PubJobTitleDto> findJobTitleBySid(String employeeId);

	/**
	 * Find job title by sid.
	 *
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 * @return the list
	 */
	// RequestList #33
	List<PubJobTitleDto> findJobTitleBySid(String employeeId, GeneralDate baseDate);
}
