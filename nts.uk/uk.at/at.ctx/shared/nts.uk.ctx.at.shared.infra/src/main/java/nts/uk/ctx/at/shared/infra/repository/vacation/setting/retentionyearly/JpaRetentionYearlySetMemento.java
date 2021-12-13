/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.retentionyearly;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.UpperLimitSetting;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.KmfmtRetentionYearly;

/**
 * The Class JpaRetentionYearlySetMemento.
 */
public class JpaRetentionYearlySetMemento implements RetentionYearlySettingSetMemento {

	/** The type value. */
	private KmfmtRetentionYearly typeValue;
	
	/**
	 * Instantiates a new jpa retention yearly set memento.
	 *
	 * @param typeValue the type value
	 */
	public JpaRetentionYearlySetMemento(KmfmtRetentionYearly typeValue) {
		this.typeValue = typeValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * RetentionYearlySettingSetMemento#setCompanyId(java.lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.typeValue.setCid(companyId);
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * RetentionYearlySettingSetMemento#setUpperLimitSetting(nts.uk.ctx.at.
	 * shared.dom.vacation.setting.retentionyearly.UpperLimitSetting)
	 */
	@Override
	public void setUpperLimitSetting(UpperLimitSetting upperLimitSetting) {
		this.typeValue.setMaxDaysRetention(upperLimitSetting.getMaxDaysCumulation().v().shortValue());
		this.typeValue.setYearAmount(upperLimitSetting.getRetentionYearsAmount().v().shortValue());
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * RetentionYearlySettingSetMemento#setManagementCategory
	 * (nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct)
	 */
	@Override
	public void setManagementCategory(ManageDistinct managementCategory) {
		this.typeValue.setManagementYearlyAtr(BooleanUtils.toBoolean(managementCategory.value));		
	}
}