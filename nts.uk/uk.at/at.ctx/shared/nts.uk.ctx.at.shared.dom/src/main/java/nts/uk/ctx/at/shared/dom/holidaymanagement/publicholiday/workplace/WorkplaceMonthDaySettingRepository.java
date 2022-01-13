/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.workplace;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;

/**
 * The Interface WorkplaceMonthDaySettingRepository.
 */
public interface WorkplaceMonthDaySettingRepository {
	
	/**
	 * Find by year.
	 *
	 * @param companyId the company id
	 * @param workplaceId the workplace id
	 * @param year the year
	 * @return the optional
	 */
	Optional<WorkplaceMonthDaySetting> findByYear(CompanyId companyId, String workplaceId, Year year);
	
	/**
	 * Find by year.
	 *
	 * @param companyId the company id
	 * @param year the year
	 * @return the list
	 */
	List<WorkplaceMonthDaySetting> findByYear(CompanyId companyId, Year year);
	
	/**
	 * Find by WorkplaceIds.
	 *
	 * @param companyId the company id
	 * @param workplaceId the workplace id
	 * @return the optional
	 */
	List<WorkplaceMonthDaySetting> findByWorkplaceIds(CompanyId companyId, List<String> workplaceId);
	
	/**
	 * Find all by year.
	 *
	 * @param companyId the company id
	 * @param year the year
	 * @return the workplace month day setting
	 */
	List<String> findWkpRegisterByYear(CompanyId companyId, Year year);
	
	/**
	 * Find by years.
	 * @param companyId the company id
	 * @param workplaceId the workplace id
	 * @param yearList the list year
	 * @return
	 */
	List<WorkplaceMonthDaySetting> findByYears(CompanyId companyId, String workplaceId, List<Year> yearList);
	
	/**
	 * Adds the.
	 *
	 * @param domain the domain
	 */
	void add(WorkplaceMonthDaySetting domain);
	
	/**
	 * Update.
	 *
	 * @param domain the domain
	 */
	void update(WorkplaceMonthDaySetting domain);
	
	/**
	 * Removes the.
	 *
	 * @param companyId the company id
	 * @param workplaceId the workplace id
	 * @param year the year
	 */
	void remove(CompanyId companyId, String workplaceId, Year year, Integer startMonth);
	
	void remove(WorkplaceMonthDaySetting domain);
}
