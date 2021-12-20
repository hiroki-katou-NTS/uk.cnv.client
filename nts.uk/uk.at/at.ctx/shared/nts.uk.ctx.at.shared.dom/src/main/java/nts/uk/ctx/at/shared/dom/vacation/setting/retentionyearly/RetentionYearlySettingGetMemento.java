/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;

/**
 * The Interface RetentionYearlySettingGetMemento.
 */
public interface RetentionYearlySettingGetMemento {
	
	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	String getCompanyId();
	
	/**
	 * Gets the upper limit setting.
	 *
	 * @return the upper limit setting
	 */
	UpperLimitSetting getUpperLimitSetting();
	
	/**
	 * Gets the management category.
	 *
	 * @return the management category
	 */
	ManageDistinct getManagementCategory();
}
