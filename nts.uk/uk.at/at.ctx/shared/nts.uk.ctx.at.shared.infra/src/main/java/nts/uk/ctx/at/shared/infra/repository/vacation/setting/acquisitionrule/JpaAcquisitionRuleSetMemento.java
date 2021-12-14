/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.acquisitionrule;

import nts.uk.ctx.at.shared.dom.vacation.setting.SettingDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.AcquisitionRuleSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.AnnualHoliday;
import nts.uk.ctx.at.shared.dom.vacation.setting.acquisitionrule.HoursHoliday;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.acquisitionrule.KarstAcquisitionRule;

/**
 * The Class JpaAcquisitionRuleSetMemento.
 */
public class JpaAcquisitionRuleSetMemento implements AcquisitionRuleSetMemento {

	/** The type value. */
	private KarstAcquisitionRule typeValue;

	/**
	 * Instantiates a new jpa acquisition rule set memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaAcquisitionRuleSetMemento(KarstAcquisitionRule typeValue) {
		this.typeValue = typeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.vacation.setting.acquisitionrule.
	 * VaAcRuleSetMemento#setCompanyId(java.lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.typeValue.setCid(companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.pr.core.dom.vacation.setting.acquisitionrule.
	 * VaAcRuleSetMemento#setSettingclassification(nts.uk.ctx.pr.core.dom.
	 * vacation.setting.acquisitionrule.Settingclassification)
	 */
	@Override
	public void setCategory(SettingDistinct category) {
		this.typeValue.setCategory(category.value);
	}

	@Override
	public void setAnnualHoliday(AnnualHoliday annualHoliday) {		
		if(!annualHoliday.isPriorityPause()) {
			this.typeValue.setCompensatoryDayOff(0);
		} else {
			this.typeValue.setCompensatoryDayOff(1);
		}
		
		if(!annualHoliday.isPrioritySubstitute()) {
			this.typeValue.setSabstituteHoliday(0);
		} else {
			this.typeValue.setSabstituteHoliday(1);
		}
		
		if(!annualHoliday.isSixtyHoursOverrideHoliday()) {
			this.typeValue.setFundedPaidHoliday(0);
		} else {
			this.typeValue.setFundedPaidHoliday(1);
		}
	}

	
}
