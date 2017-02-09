/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.dom.insurance.labor.businesstype;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.core.dom.company.CompanyCode;
import nts.uk.ctx.pr.core.dom.insurance.BusinessName;

/**
 * The Class InsuranceBusinessType.
 */
@Getter
public class InsuranceBusinessType extends AggregateRoot {

	/** The company code. */
	private CompanyCode companyCode;

	/** The biz order. */
	private BusinessTypeEnum bizOrder;

	/** The biz name. */
	private BusinessName bizName;

	/**
	 * Instantiates a new insurance business type.
	 */
	public InsuranceBusinessType() {
		super();
	}

	// =================== Memento State Support Method ===================
	/**
	 * Instantiates a new labor insurance office.
	 *
	 * @param memento
	 *            the memento
	 */
	public InsuranceBusinessType(InsuranceBusinessTypeGetMemento memento) {
		this.companyCode = memento.getCompanyCode();
		this.bizOrder = memento.getBizOrder();
		this.bizName = memento.getBizName();
		this.setVersion(memento.getVersion());
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(InsuranceBusinessTypeSetMemento memento) {
		memento.setCompanyCode(this.companyCode);
		memento.setBizOrder(this.bizOrder);
		memento.setBizName(this.bizName);
		memento.setVersion(this.getVersion());
	}

}
