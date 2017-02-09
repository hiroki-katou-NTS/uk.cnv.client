/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.report.dom.wageledger.aggregate;

import nts.uk.ctx.pr.report.dom.company.CompanyCode;

/**
 * The Interface WageLedgerAggregateItemRepository.
 */
public interface WLAggregateItemRepository {
	
	/**
	 * Save.
	 *
	 * @param aggregateItem the aggregate item
	 */
	void save(WLAggregateItem aggregateItem);
	
	/**
	 * Removes the.
	 *
	 * @param aggregateItem the aggregate item
	 */
	void remove(WLAggregateItem aggregateItem);
	
	/**
	 * Find.
	 *
	 * @param code the code
	 * @param companyCode the company code
	 * @return the wage ledger aggregate item
	 */
	WLAggregateItem find(WLAggregateItemCode code, CompanyCode companyCode);
}
