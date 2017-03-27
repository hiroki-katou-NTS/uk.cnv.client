/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.insurance.labor.businesstype;

import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.dom.insurance.BusinessName;

/**
 * The Interface InsuranceBusinessTypeMemento.
 */
public interface InsuranceBusinessTypeSetMemento {

	/**
	 * Gets the company code.
	 *
	 * @return the company code
	 */
	void setCompanyCode(CompanyCode companyCode);

	/**
	 * Gets the biz order.
	 *
	 * @return the biz order
	 */
	void setBizOrder(BusinessTypeEnum businessTypeEnum );

	/**
	 * Gets the biz name.
	 *
	 * @return the biz name
	 */
	void setBizName(BusinessName name);

}
