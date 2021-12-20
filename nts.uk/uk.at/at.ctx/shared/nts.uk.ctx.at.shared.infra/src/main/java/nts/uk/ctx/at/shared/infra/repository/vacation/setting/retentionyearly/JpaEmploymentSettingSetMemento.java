/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.retentionyearly;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmptYearlyRetentionSetMemento;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.KmfmtRetentionEmpCtr;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.KmfmtRetentionEmpCtrPK;

/**
 * The Class JpaEmploymentSettingSetMemento.
 */
public class JpaEmploymentSettingSetMemento implements EmptYearlyRetentionSetMemento {

	/** The type value. */
	private KmfmtRetentionEmpCtr typeValue;

	/**
	 * Instantiates a new jpa employment setting set memento.
	 *
	 * @param typeValue the type value
	 */
	public JpaEmploymentSettingSetMemento(KmfmtRetentionEmpCtr typeValue) {
		this.typeValue = typeValue;
		if(this.typeValue.getKmfmtRetentionEmpCtrPK() == null) {
			this.typeValue.setKmfmtRetentionEmpCtrPK(new KmfmtRetentionEmpCtrPK());
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * EmptYearlyRetentionSetMemento#setCompanyId(java.lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.typeValue.getKmfmtRetentionEmpCtrPK().setCid(companyId);
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * EmptYearlyRetentionSetMemento#setEmploymentCode(java.lang.String)
	 */
	@Override
	public void setEmploymentCode(String employmentCode) {
		this.typeValue.getKmfmtRetentionEmpCtrPK().setEmpCtrCd(employmentCode);
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * EmptYearlyRetentionSetMemento#setManagementCategory(nts.uk.ctx.at.shared.
	 * dom.vacation.setting.retentionyearly.ManagementCategory)
	 */
	@Override
	public void setManagementCategory(ManageDistinct managementCategory) {
		this.typeValue.setManagementCtrAtr(BooleanUtils.toBoolean(managementCategory.value));
	}

}