/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.otreferset;

import nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetGetMemento;
import nts.uk.ctx.sys.auth.infra.entity.otreferset.SacmtOtRefer;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaOvertimeReferSetGetMemento.
 */
public class JpaOvertimeReferSetGetMemento implements OvertimeReferSetGetMemento{

	/** The sacmt ot refer set. */
	private SacmtOtRefer sacmtOtReferSet;
	
	/**
	 * Instantiates a new jpa overtime refer set get memento.
	 *
	 * @param entity the entity
	 */
	public JpaOvertimeReferSetGetMemento(SacmtOtRefer entity) {
		this.sacmtOtReferSet = entity;
		if (entity.getCid() == null) {
			entity.setCid(AppContexts.user().companyId());
		}
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetGetMemento#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.sacmtOtReferSet.getCid();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetGetMemento#getReferWkpAdmin()
	 */
	@Override
	public boolean getReferWkpAdmin() {
		return this.sacmtOtReferSet.isReferWkpAdmin(); 
	}

}

