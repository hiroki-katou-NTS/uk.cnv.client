package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.company;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.common.Year;

/**
 * The Interface CompanyMonthDaySettingRepository.
 */
public interface CompanyMonthDaySettingRepository {
	
	
	/**
	 * Findby key.
	 *
	 * @param companyId the company id
	 * @param year the year
	 * @return the optional
	 */
	Optional<CompanyMonthDaySetting> findByYear(CompanyId companyId, Year year);
		
	/**
	 * 検索　年リスト
	 * @param companyId
	 * @param year
	 * @return
	 */
	List<CompanyMonthDaySetting> findByYears(CompanyId companyId, List<Year> year);
	
	/**
	 * Adds the.
	 *
	 * @param domain the domain
	 */
	void add(CompanyMonthDaySetting domain);
	
	/**
	 * Update.
	 *
	 * @param domain the domain
	 */
	void update(CompanyMonthDaySetting domain);
	
	
	/**
	 * Removes the.
	 *
	 * @param companyId the company id
	 * @param year the year
	 */
	void remove(CompanyId companyId, Year year, Integer startMonth);
	
	void remove(CompanyMonthDaySetting domain);
}
