/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.otreferset;

import java.math.BigDecimal;

import nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetSetMemento;
import nts.uk.ctx.sys.auth.infra.entity.otreferset.SacmtOtRefer;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaOvertimeReferSetSetMemento.
 */
public class JpaOvertimeReferSetSetMemento implements OvertimeReferSetSetMemento{

	/** The sacmt ot refer set. */
	private SacmtOtRefer sacmtOtReferSet;
	
	/** The Constant DO. */
	private static final Integer DO = 1;
	
	/** The Constant NOT_DO. */
	private static final Integer NOT_DO = 0;
	
	/**
	 * Instantiates a new jpa overtime refer set set memento.
	 *
	 * @param entity the entity
	 */
	public JpaOvertimeReferSetSetMemento(SacmtOtRefer entity) {
		this.sacmtOtReferSet = entity;
		if (this.sacmtOtReferSet.getCid() == null) {
			this.sacmtOtReferSet.setCid(AppContexts.user().companyId());
		}
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetSetMemento#setCompanyId(java.lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.sacmtOtReferSet.setCid(companyId);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetSetMemento#setReferWkpAdmin(boolean)
	 */
	@Override
	public void setReferWkpAdmin(boolean referWkpAdmin) {
		this.sacmtOtReferSet.setReferWkpAdmin(BigDecimal.valueOf(referWkpAdmin == true ? DO : NOT_DO));
	}

}

