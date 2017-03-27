/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.rule.employment.unitprice;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.core.dom.company.CompanyCode;

/**
 * The Interface UnitPriceRepository.
 */
public interface UnitPriceRepository {

	/**
	 * Adds the.
	 *
	 * @param unitPrice the unit price
	 */
    void add(UnitPrice unitPrice);

	/**
	 * Update.
	 *
	 * @param unitPrice the unit price
	 */
    void update(UnitPrice unitPrice);

	/**
	 * Removes the.
	 *
	 * @param id the id
	 * @param version the version
	 */
    void remove(CompanyCode companyCode, UnitPriceCode cUnitpriceCd);

	/**
	 * Find all.
	 *
	 * @param companyCode the company code
	 * @param contractCode the contract code
	 * @return the list
	 */
	List<UnitPrice> findAll(CompanyCode companyCode);

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the unit price
	 */
	Optional<UnitPrice> findByCode(CompanyCode companyCode, UnitPriceCode cUnitpriceCd);

	/**
	 * Check duplicate code.
	 *
	 * @param code the code
	 * @return true, if successful
	 */
	boolean isDuplicateCode(CompanyCode companyCode, UnitPriceCode code);
}
