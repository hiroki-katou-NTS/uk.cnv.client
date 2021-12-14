/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.subst;

import nts.uk.ctx.at.shared.dom.vacation.setting.ApplyPermission;
import nts.uk.ctx.at.shared.dom.vacation.setting.ExpirationTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ManageDeadline;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.SubstVacationSettingGetMemento;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstComSubstVacation;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstSubstVacationSetting;

/**
 * The Class JpaSubstVacationSettingGetMemento.
 */
public class JpaSubstVacationSettingGetMemento<T extends KsvstComSubstVacation>
		implements SubstVacationSettingGetMemento {

	/** The type value. */
	private T typeValue;

	/**
	 * Instantiates a new jpa subst vacation setting get memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaSubstVacationSettingGetMemento(T typeValue) {
		this.typeValue = typeValue;
	}

	@Override
	public ManageDeadline manageDeadline() {
		
		return ManageDeadline.valueOf(this.typeValue.getExpDateMngMethod().intValue());
	}

	@Override
	public ExpirationTime getExpirationDate() {
		return ExpirationTime.valueOf(this.typeValue.getExpitationDateSet());
	}

	/**
	 * Gets the allow prepaid leave.
	 *
	 * @return the allow prepaid leave
	 */
	@Override
	public ApplyPermission getAllowPrepaidLeave() {
		return ApplyPermission.valueOf(this.typeValue.getAllowPrepaidLeave());
	}

	@Override
	public ManageDistinct getManageDistinct() {
		
		return ManageDistinct.valueOf(this.typeValue.getManageAtr());
	}

	@Override
	public ManageDistinct getLinkingManagementATR() {
		// TODO Auto-generated method stub
		return ManageDistinct.valueOf(this.typeValue.getExpDateMngMethod());
	}


	/**
	 * Gets the expiration date.
	 *
	 * @return the expiration date
	 */
	

}
