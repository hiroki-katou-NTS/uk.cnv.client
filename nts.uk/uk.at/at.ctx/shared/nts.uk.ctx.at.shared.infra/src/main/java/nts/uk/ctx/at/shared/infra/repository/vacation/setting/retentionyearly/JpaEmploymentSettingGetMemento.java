/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.retentionyearly;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmptYearlyRetentionGetMemento;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.KmfmtRetentionEmpCtr;

/**
 * The Class JpaEmploymentSettingGetMemento.
 */
public class JpaEmploymentSettingGetMemento implements EmptYearlyRetentionGetMemento {
	
	/** The type value. */
	private KmfmtRetentionEmpCtr typeValue;
	
	/**
	 * Instantiates a new jpa employment setting get memento.
	 *
	 * @param typeValue the type value
	 */
	public JpaEmploymentSettingGetMemento(KmfmtRetentionEmpCtr typeValue) {
		this.typeValue = typeValue;
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * EmptYearlyRetentionGetMemento#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.typeValue.getKmfmtRetentionEmpCtrPK().getCid();
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * EmptYearlyRetentionGetMemento#getEmploymentCode()
	 */
	@Override
	public String getEmploymentCode() {
		return this.typeValue.getKmfmtRetentionEmpCtrPK().getEmpCtrCd();
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * EmptYearlyRetentionGetMemento#getManagementCategory()
	 */
	@Override
	public ManageDistinct getManagementCategory() {
		return ManageDistinct.valueOf(BooleanUtils.toInteger(this.typeValue.isManagementCtrAtr()));
	}

}