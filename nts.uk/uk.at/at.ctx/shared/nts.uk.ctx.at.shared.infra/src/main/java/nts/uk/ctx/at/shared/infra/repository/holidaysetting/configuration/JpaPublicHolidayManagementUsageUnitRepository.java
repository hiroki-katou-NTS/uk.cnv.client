/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.holidaysetting.configuration;

import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayManagementUsageUnit;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayManagementUsageUnitRepository;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration.KshmtHdpubUnitSet;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaPublicHolidayManagementUsageUnitRepository.
 */
@Stateless
public class JpaPublicHolidayManagementUsageUnitRepository extends JpaRepository implements PublicHolidayManagementUsageUnitRepository{

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidayManagementUsageUnitRepository#findByCID(java.lang.String)
	 */
	@Override
	public Optional<PublicHolidayManagementUsageUnit> get(String companyId) {
		return this.queryProxy().find(companyId, KshmtHdpubUnitSet.class).map(e -> this.toDomain(e));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidayManagementUsageUnitRepository#update(nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidayManagementUsageUnit)
	 */
	@Override
	public void update(PublicHolidayManagementUsageUnit domain) {
		this.commandProxy().update(this.toEntity(domain));
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidayManagementUsageUnitRepository#add(nts.uk.ctx.bs.employee.dom.holidaysetting.configuration.PublicHolidayManagementUsageUnit)
	 */
	@Override
	public void insert(PublicHolidayManagementUsageUnit domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}
	
	/**
	 * To domain.
	 *
	 * @param entity the entity
	 * @return the public holiday management usage unit
	 */
	private PublicHolidayManagementUsageUnit toDomain(KshmtHdpubUnitSet entity){
		PublicHolidayManagementUsageUnit domain = new PublicHolidayManagementUsageUnit(
															BooleanUtils.toInteger(entity.isManageSPubHd()),
															BooleanUtils.toInteger(entity.isManageWkpPubHd()),
															BooleanUtils.toInteger(entity.isManageEmpPubHd())
														);
		
		return domain;
	}
	
	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @return the kshmt pub hd mng unit set
	 */
	private KshmtHdpubUnitSet toEntity(PublicHolidayManagementUsageUnit domain){
		KshmtHdpubUnitSet e = new KshmtHdpubUnitSet();
		e.setManageEmpPubHd(BooleanUtils.toBoolean(domain.getIsManageEmpPublicHd()));
		e.setManageSPubHd(BooleanUtils.toBoolean(domain.getIsManageEmployeePublicHd()));
		e.setManageWkpPubHd(BooleanUtils.toBoolean(domain.getIsManageWkpPublicHd()));
		e.setCid(AppContexts.user().companyId());
		return e;
	}
}
