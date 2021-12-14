/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.subst;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.SubstVacationSetting;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstComSubstVacation;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstSubstVacationSetting;

/**
 * The Class JpaComSubstVacationSetMemento.
 */
public class JpaComSubstVacationSetMemento implements ComSubstVacationSetMemento {

	/** The type value. */
	private KsvstComSubstVacation typeValue;

	/**
	 * Instantiates a new jpa com subst vacation set memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaComSubstVacationSetMemento(KsvstComSubstVacation typeValue) {
		this.typeValue = typeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.subst.
	 * ComSubstVacationSetMemento#setCompanyId(java.lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.typeValue.setCid(companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.subst.
	 * ComSubstVacationSetMemento#setSetting(nts.uk.ctx.at.shared.dom.vacation.
	 * setting.subst.SubstVacationSetting)
	 */
	@Override
	public void setSetting(SubstVacationSetting setting) {
		setting.saveToMemento(
				new JpaSubstVacationSettingSetMemento<KsvstComSubstVacation>(this.typeValue));
	}

	@Override
	public void setManageDistinct(ManageDistinct manageDistinct) {
		this.typeValue.setManageAtr(manageDistinct.value);
		
	}

	@Override
	public void setLinkingManagementATR(ManageDistinct linkingManagementATR) {
		this.typeValue.setLinkMngAtr(linkingManagementATR.value);
		
	}

}
