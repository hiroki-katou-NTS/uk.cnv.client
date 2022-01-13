/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;

/**
 * The Interface RetentionYearlySettingSetMemento.
 */
public interface RetentionYearlySettingSetMemento {
	
	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	void setCompanyId(String companyId);
	
	/**
	 * Sets the upper limit setting.
	 *
	 * @param upperLimitSetting the new upper limit setting
	 */
	void setUpperLimitSetting(UpperLimitSetting upperLimitSetting);
	
	/**
	 * Sets the management category.
	 *
	 * @param managementCategory the new management category
	 */
	void setManagementCategory(ManageDistinct managementCategory);
}
