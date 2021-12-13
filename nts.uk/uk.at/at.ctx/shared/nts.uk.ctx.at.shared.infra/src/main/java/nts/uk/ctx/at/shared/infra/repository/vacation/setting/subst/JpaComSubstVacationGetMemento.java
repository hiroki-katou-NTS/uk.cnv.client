/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.subst;

import org.apache.commons.lang3.BooleanUtils;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.SubstVacationSetting;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstComSubstVacation;

/**
 * The Class JpaComSubstVacationGetMemento.
 */
public class JpaComSubstVacationGetMemento implements ComSubstVacationGetMemento {

	/** The type value. */
	private KsvstComSubstVacation typeValue;

	/**
	 * Instantiates a new jpa com subst vacation get memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaComSubstVacationGetMemento(KsvstComSubstVacation typeValue) {
		this.typeValue = typeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.subst.
	 * ComSubstVacationGetMemento#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.typeValue.getCid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.subst.
	 * ComSubstVacationGetMemento#getSetting()
	 */
	@Override
	public SubstVacationSetting getSetting() {
		return new SubstVacationSetting(
				new JpaSubstVacationSettingGetMemento<KsvstComSubstVacation>(this.typeValue));
	}

	@Override
	public ManageDistinct getManageDistinct() {

		return EnumAdaptor.valueOf(BooleanUtils.toInteger(this.typeValue.isManageAtr()), ManageDistinct.class);
	}

	@Override
	public ManageDistinct getLinkingManagementATR() {

		return EnumAdaptor.valueOf(BooleanUtils.toInteger(this.typeValue.isLinkMngAtr()), ManageDistinct.class);
	}

}
