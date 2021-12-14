/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.acquisitionrule;

import nts.uk.ctx.at.shared.dom.vacation.setting.SettingDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.AcquisitionRuleGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.AnnualHoliday;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.HoursHoliday;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.acquisitionrule.KarstAcquisitionRule;

/**
 * The Class JpaAcquisitionRuleGetMemento.
 */
public class JpaAcquisitionRuleGetMemento implements AcquisitionRuleGetMemento {

	/** The type value. */
	private KarstAcquisitionRule typeValue;

	/**
	 * Instantiates a new jpa acquisition rule get memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaAcquisitionRuleGetMemento(KarstAcquisitionRule typeValue) {
		super();
		this.typeValue = typeValue;

	}

	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.vacation.setting.acquisitionrule.
	 * VaAcRuleGetMemento#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.typeValue.getCid();
	}

	/**
	 * Gets the settingclassification.
	 *
	 * @return the settingclassification
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.vacation.setting.acquisitionrule.
	 * VaAcRuleGetMemento#getSettingclassification()
	 */
	@Override
	public SettingDistinct getCategory() {
		return SettingDistinct.valueOf(this.typeValue.getCategory());
	}

	@Override
	public AnnualHoliday getAnnualHoliday() {
		
		AnnualHoliday annualHoliday = new AnnualHoliday();
		
		if(this.typeValue.getCompensatoryDayOff() == 0) {
			annualHoliday.setPriorityPause(false);
		} else {
			annualHoliday.setPriorityPause(true);
		}
		
		if(this.typeValue.getSabstituteHoliday() == 0) {
			annualHoliday.setPrioritySubstitute(false);
		} else {
			annualHoliday.setPrioritySubstitute(true);
		}
		
		if(this.typeValue.getFundedPaidHoliday() == 0) {
			annualHoliday.setSixtyHoursOverrideHoliday(false);
		} else {
			annualHoliday.setSixtyHoursOverrideHoliday(true);
		}
		
		return annualHoliday;
	}

}
