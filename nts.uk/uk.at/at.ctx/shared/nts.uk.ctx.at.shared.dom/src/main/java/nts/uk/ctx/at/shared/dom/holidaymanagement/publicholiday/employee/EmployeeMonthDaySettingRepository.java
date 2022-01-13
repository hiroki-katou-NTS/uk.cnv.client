/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;


/**
 * The Interface EmployeeMonthDaySettingRepository.
 */
public interface EmployeeMonthDaySettingRepository {
	
	/**
	 * Find by year.
	 *
	 * @param companyId the company id
	 * @param employee the employee
	 * @param year the year
	 * @return the optional
	 */
	Optional<EmployeeMonthDaySetting> findByYear(CompanyId companyId, String employee, Year year);
	
	/**
	 * Find by year.
	 *
	 * @param companyId the company id
	 * @param employee the list employee
	 * @param year the year
	 * @return the List
	 */
	List<EmployeeMonthDaySetting> findByYear(CompanyId companyId, List<String> employee, Year year);
	
	/**
	 * Find all employee register.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<String> findAllEmployeeRegister(CompanyId companyId, Year year);
	
	/**
	 *  Find by years.
	 * @param companyId the company id
	 * @param employee the employee
	 * @param yearList  the list year
	 * @return the List
	 */
	List<EmployeeMonthDaySetting> findByYears(CompanyId companyId, String employee, List<Year> yearList);

	
	/**
	 * Adds the.
	 *
	 * @param domain the domain
	 */
	void add(EmployeeMonthDaySetting domain);
	
	/**
	 * Update.
	 *
	 * @param domain the domain
	 */
	void update(EmployeeMonthDaySetting domain);
	
	/**
	 * Removes the.
	 *
	 * @param companyId the company id
	 * @param employee the employee
	 * @param year the year
	 */
	void remove(CompanyId companyId, String employee, Year year, Integer startMonth);
	
	void remove(EmployeeMonthDaySetting domain);
}
