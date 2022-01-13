/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.holidaysetting.configuration;

import java.util.Optional;

import javax.ejb.Stateless;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayCarryOverDeadline;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidayPeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySetting;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.PublicHolidaySettingRepository;
import nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration.KshmtHdPublicMgt;

/**
 * The Class JpaPublicHolidaySettingRepository.
 */
@Stateless
public class JpaPublicHolidaySettingRepository extends JpaRepository implements PublicHolidaySettingRepository{

	@Override
	public Optional<PublicHolidaySetting> get(String companyId) {
		return this.queryProxy().find(companyId, KshmtHdPublicMgt.class).map(e -> this.toDomain(e));
	}

	@Override
	public void update(PublicHolidaySetting domain) {
		this.commandProxy().update(this.toEntity(domain, true));
	}

	@Override
	public void insert(PublicHolidaySetting domain) {
		this.commandProxy().insert(this.toEntity(domain, false));
	}
	
	
	private KshmtHdPublicMgt toEntity(PublicHolidaySetting domain, boolean isUpdate){
		KshmtHdPublicMgt entity;
		if (isUpdate) {
			entity = this.queryProxy().find(domain.getCompanyID(), KshmtHdPublicMgt.class).get();
		} else {
			entity = new KshmtHdPublicMgt();
		}
		entity.setCid(domain.getCompanyID());
		entity.setMgtAtr(domain.getIsManagePublicHoliday());
		entity.setMgtPeriodAtr(domain.getPublicHolidayPeriod().value);
		entity.setCarryOverDeadline(domain.getPublicHolidayCarryOverDeadline().value);
		entity.setCarryFwdMinusArt(BooleanUtils.toBoolean(domain.getCarryOverNumberOfPublicHolidayIsNegative()));
		return entity;
	}	
	
	private PublicHolidaySetting toDomain(KshmtHdPublicMgt entity){
		PublicHolidaySetting domain = new PublicHolidaySetting(
				entity.getCid(), 
				entity.getMgtAtr(), 
				PublicHolidayPeriod.valueOf(entity.getMgtPeriodAtr()),
				PublicHolidayCarryOverDeadline.valueOf(entity.getCarryOverDeadline()),
				BooleanUtils.toInteger(entity.isCarryFwdMinusArt()));
		return domain;
	}
}
