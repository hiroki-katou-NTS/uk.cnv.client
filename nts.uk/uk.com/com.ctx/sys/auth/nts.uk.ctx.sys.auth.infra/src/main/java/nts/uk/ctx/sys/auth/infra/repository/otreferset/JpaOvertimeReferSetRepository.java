/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.otreferset;

import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSet;
import nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetRepository;
import nts.uk.ctx.sys.auth.infra.entity.otreferset.SacmtOtReferSet;

/**
 * The Class JpaOvertimeReferSetRepository.
 */
@Stateless
public class JpaOvertimeReferSetRepository extends JpaRepository implements OvertimeReferSetRepository{

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.otreferset.OvertimeReferSetRepository#getOvertimeReferSet(java.lang.String)
	 */
	@Override
	public Optional<OvertimeReferSet> getOvertimeReferSet(String companyID) {
		return this.queryProxy().find(companyID, SacmtOtReferSet.class).map(entity -> this.toDomain(entity));
	}
	
	/**
	 * To domain.
	 *
	 * @param entity the entity
	 * @return the overtime refer set
	 */
	private OvertimeReferSet toDomain(SacmtOtReferSet entity) {
		return new OvertimeReferSet(new JpaOvertimeReferSetGetMemento(entity));
	}

	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @return the sacmt ot refer set
	 */
	private SacmtOtReferSet toEntity(OvertimeReferSet domain) {
		SacmtOtReferSet entity = new SacmtOtReferSet();
		domain.saveToMemento(new JpaOvertimeReferSetSetMemento(entity));
		return entity;
	}
}

