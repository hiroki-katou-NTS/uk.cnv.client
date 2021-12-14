/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.subst;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.SubstVacationSetting;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstEmpSubstVacation;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstEmpSubstVacationPK;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.subst.KsvstSubstVacationSetting;

/**
 * The Class JpaEmpSubstVacationSetMemento.
 */
public class JpaEmpSubstVacationSetMemento implements EmpSubstVacationSetMemento {

	/** The type value. */
	private KsvstEmpSubstVacation typeValue;

	/**
	 * Instantiates a new jpa emp subst vacation set memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaEmpSubstVacationSetMemento(KsvstEmpSubstVacation typeValue) {

		KsvstEmpSubstVacationPK empSubstVacationPK = typeValue.getKclstEmpSubstVacationPK();

		// Check PK exist
		if (empSubstVacationPK == null) {
			empSubstVacationPK = new KsvstEmpSubstVacationPK();
		}

		typeValue.setKclstEmpSubstVacationPK(empSubstVacationPK);

		this.typeValue = typeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.subst.
	 * EmpSubstVacationSetMemento#setCompanyId(java.lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		KsvstEmpSubstVacationPK empSubstVacationPK = this.typeValue.getKclstEmpSubstVacationPK();
		empSubstVacationPK.setCid(companyId);
		this.typeValue.setKclstEmpSubstVacationPK(empSubstVacationPK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.vacation.setting.subst.
	 * EmpSubstVacationSetMemento#setEmpContractTypeCode(java.lang.String)
	 */
	@Override
	public void setEmpContractTypeCode(String contractTypeCode) {
		KsvstEmpSubstVacationPK empSubstVacationPK = this.typeValue.getKclstEmpSubstVacationPK();
		empSubstVacationPK.setContractTypeCd(contractTypeCode);
		this.typeValue.setKclstEmpSubstVacationPK(empSubstVacationPK);
	}

	@Override
	public void setManageDistinct(ManageDistinct manageDistinct) {
		this.typeValue.setManageAtr(manageDistinct.value);
		
	}

	
	
}
