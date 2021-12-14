/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.subst;

import nts.uk.ctx.at.shared.dom.vacation.setting.ApplyPermission;
import nts.uk.ctx.at.shared.dom.vacation.setting.ExpirationTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ManageDeadline;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.SubstVacationSettingSetMemento;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstComSubstVacation;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstSubstVacationSetting;

/**
 * The Class JpaSubstVacationSettingSetMemento.
 */
public class JpaSubstVacationSettingSetMemento<T extends KsvstComSubstVacation>
		implements SubstVacationSettingSetMemento {

	/** The type value. */
	private T typeValue;

	/**
	 * Instantiates a new jpa subst vacation setting set memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaSubstVacationSettingSetMemento(T typeValue) {
		this.typeValue = typeValue;
	}

	@Override
	public void setManageDeadline(ManageDeadline manageDeadline) {
		this.typeValue.setExpDateMngMethod(manageDeadline.value);
		
	}

	@Override
	public void setExpirationDate(ExpirationTime expirationDate) {
		this.typeValue.setExpitationDateSet(expirationDate.value);
		
	}

	@Override
	public void setAllowPrepaidLeave(ApplyPermission allowPrepaidLeave) {
		this.typeValue.setAllowPrepaidLeave(allowPrepaidLeave.value);	
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
