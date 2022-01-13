/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.vacation.setting.retentionyearly.dto;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.UpperLimitSetting;

@Getter
@Setter
public class RetentionYearlyDto {
	
	/** The upper limit setting dto. */
	private UpperLimitSettingDto upperLimitSettingDto;
	
	/** The leave as work days. */
	private Boolean leaveAsWorkDays;
	
	/** The management category. */
	private Integer managementCategory;
	
	/**
	 * To domain.
	 *
	 * @param companyId the company id
	 * @return the retention yearly setting
	 */
	public RetentionYearlySetting toDomain(String companyId) {
		return new RetentionYearlySetting(new GetMementoImpl(companyId, this));
	}
	
	/**
	 * The Class GetMementoImpl.
	 */
	private class GetMementoImpl implements RetentionYearlySettingGetMemento {
		
		/** The dto. */
		private RetentionYearlyDto dto;
		
		/** The company id. */
		private String companyId;
		
		/**
		 * Instantiates a new gets the memento impl.
		 *
		 * @param companyId the company id
		 * @param dto the dto
		 */
		public GetMementoImpl(String companyId, RetentionYearlyDto dto) {
			super();
			this.companyId = companyId;
			this.dto = dto;
		}
		
		/*
		 * (non-Javadoc)
		 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
		 * RetentionYearlySettingGetMemento#getCompanyId()
		 */
		@Override
		public String getCompanyId() {
			return this.companyId;
		}
		
		/*
		 * (non-Javadoc)
		 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
		 * RetentionYearlySettingGetMemento#getUpperLimitSetting()
		 */
		@Override
		public UpperLimitSetting getUpperLimitSetting() {
			return dto.upperLimitSettingDto.toDomain();
		}
		
		/*
		 * (non-Javadoc)
		 * @see nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.
		 * RetentionYearlySettingGetMemento#getManagementCategory()
		 */
		@Override
		public ManageDistinct getManagementCategory() {
			return ManageDistinct.valueOf(dto.managementCategory);
		}		
	}
}
