/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employment;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;

/**
 * The Interface EmploymentMonthDaySettingRepository.
 */
public interface EmploymentMonthDaySettingRepository {

	/**
	 * Find by year.
	 *
	 * @param companyId the company id
	 * @param employmentCode the employment code
	 * @param year the year
	 * @return the optional
	 */
	Optional<EmploymentMonthDaySetting> findByYear(CompanyId companyId, String employmentCode, Year year);
	
	/**
	 * Find by year.
	 *
	 * @param companyId the company id
	 * @param year the year
	 * @return the List
	 */
	List<EmploymentMonthDaySetting> findByCompany(CompanyId companyId);
	
	
	/**
	 * Find by year.
	 *
	 * @param companyId the company id
	 * @param employmentCode the employment code
	 * @param year the List<year>
	 * @return the List
	 */
	List<EmploymentMonthDaySetting> findByYears(CompanyId companyId, String employmentCode, List<Year> year);
	
	/**
	 * Find all emp register.
	 *
	 * @param companyId the company id
	 * @return the list
	 */
	List<String> findAllEmpRegister(CompanyId companyId, Year year);
	
	/**
	 * Adds the.
	 *
	 * @param domain the domain
	 */
	void add(EmploymentMonthDaySetting domain);
	
	/**
	 * Update.
	 *
	 * @param domain the domain
	 */
	void update(EmploymentMonthDaySetting domain);
	
	/**
	 * Removes the.
	 *
	 * @param companyId the company id
	 * @param employmentCode the employment code
	 * @param year the year
	 */
	void remove(CompanyId companyId, String employmentCode, Year year, Integer startMonth);
	
	void remove(EmploymentMonthDaySetting domain);
}
