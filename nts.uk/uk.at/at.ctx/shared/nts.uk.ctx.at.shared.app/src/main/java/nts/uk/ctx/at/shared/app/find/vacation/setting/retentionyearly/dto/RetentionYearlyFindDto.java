/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.find.vacation.setting.retentionyearly.dto;

import lombok.Getter;
import lombok.Setter;
//import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.ManagementDistinction;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.UpperLimitSetting;


/**
 * The Class RetentionYearlyFindDto.
 */
@Getter
@Setter
public class RetentionYearlyFindDto implements RetentionYearlySettingSetMemento {
	
	/** The company id. */
	private String companyId;
	
	/** The upper limit setting. */
	private UpperLimitSettingFindDto upperLimitSetting;
	
	/** The leave as work days. */
	private Boolean leaveAsWorkDays;
	
	/** The management category. */
	private Integer managementCategory;

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * RetentionYearlySettingSetMemento#setCompanyId(java.lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * RetentionYearlySettingSetMemento#setUpperLimitSetting(nts.uk.ctx.at.
	 * shared.dom.vacation.setting.retentionyearly.UpperLimitSetting)
	 */
	@Override
	public void setUpperLimitSetting(UpperLimitSetting upperLimitSetting) {
		this.upperLimitSetting = new UpperLimitSettingFindDto();
		this.upperLimitSetting.setMaxDaysCumulation(upperLimitSetting.getMaxDaysCumulation().v());
		this.upperLimitSetting
				.setRetentionYearsAmount(upperLimitSetting.getRetentionYearsAmount().v());
	}

	/*
	 * (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
	 * RetentionYearlySettingSetMemento#setManagementCategory
	 * (nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct)
	 */
	@Override
	public void setManagementCategory(ManageDistinct managementCategory) {
		this.managementCategory = managementCategory.value;
	}
}
