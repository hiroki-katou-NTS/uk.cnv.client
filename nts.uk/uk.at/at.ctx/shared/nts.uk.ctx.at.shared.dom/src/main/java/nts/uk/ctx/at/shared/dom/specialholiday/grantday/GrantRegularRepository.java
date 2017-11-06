package nts.uk.ctx.at.shared.dom.specialholiday.grantday;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.specialholiday.grantdate.GrantDateCom;
import nts.uk.ctx.at.shared.dom.specialholiday.grantdate.GrantDatePer;
import nts.uk.ctx.at.shared.dom.specialholiday.grantdate.GrantDatePerSet;
import nts.uk.ctx.at.shared.dom.specialholiday.grantdate.GrantDateSet;

public interface GrantRegularRepository {

	/**
	 * Find all Grant Regular
	 * 
	 * @param companyId
	 * @param specialHolidayCode
	 * @return
	 */
	List<GrantRegular> findAll(String companyId, String specialHolidayCode);

	/**
	 * Find all Com
	 * 
	 * @param companyId
	 * @param specialHolidayCode
	 * @return
	 */
	List<GrantDateCom> findAllCom(String companyId, String specialHolidayCode);
	
	/**
	 * Find Com by code
	 * 
	 * @param companyId
	 * @param specialHolidayCode
	 * @return
	 */
	Optional<GrantDateCom> getComByCode(String companyId, String specialHolidayCode);
	
	/**
	 * Find Set by code
	 * 
	 * @param companyId
	 * @param specialHolidayCode
	 * @return
	 */
	List<GrantDateSet> getSetByCode(String companyId, String specialHolidayCode);

	/**
	 * Add new Grant Date Com
	 * 
	 */
	void addGrantDateCom(GrantDateCom grantDateCom);

	/**
	 * Update Grant Date Com
	 * 
	 */
	void updateGrantDateCom(GrantDateCom grantDateCom);
	
	/**
	 * Find Per by code
	 * 
	 * @param companyId
	 * @param specialHolidayCode
	 * @param personalGrantDateCode
	 * @return
	 */
	Optional<GrantDatePer> getPerByCode(String companyId, String specialHolidayCode, String personalGrantDateCode);
	
	/**
	 * Find Per Set by code
	 * 
	 * @param companyId
	 * @param specialHolidayCode
	 * @param personalGrantDateCode
	 * @return
	 */
	List<GrantDatePerSet> getPerSetByCode(String companyId, String specialHolidayCode, String personalGrantDateCode);

	/**
	 * Add new Grant date per
	 * 
	 */
	void addPer(GrantDatePer grantDatePer);

	/**
	 * Update Grant date per
	 * 
	 */
	void updatePer(GrantDatePer grantDatePer);
	
	/**
	 * Find all Per
	 * 
	 * @param companyId
	 * @param specialHolidayCode
	 * @return
	 */
	List<GrantDatePer> findAllPer(String companyId, String specialHolidayCode);
	
	/**
	 * Remove Grant date per
	 * 
	 */
	void removePer(String companyId, String specialHolidayCode, String personalGrantDateCode);

	/**
	 * Remove Grant date per set
	 * 
	 */
	void removePerSet(String companyId, String specialHolidayCode, String personalGrantDateCode);
}
