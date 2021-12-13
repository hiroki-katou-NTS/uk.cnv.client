/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.compensatoryleave;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUse;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSettingGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave.KclmtCompensLeaveEmp;

/**
 * The Class JpaCompensLeaveEmSettingGetMemento.
 */
public class JpaCompensLeaveEmSettingGetMemento implements CompensatoryLeaveEmSettingGetMemento {
    
    /** The entity. */
    private KclmtCompensLeaveEmp entity;
    
    /**
     * Instantiates a new jpa compens leave em setting get memento.
     *
     * @param entity the entity
     */
    public JpaCompensLeaveEmSettingGetMemento(KclmtCompensLeaveEmp entity) {
        this.entity = entity;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveEmSettingGetMemento#getCompanyId()
     */
    @Override
    public String getCompanyId() {
        return this.entity.getKclmtCompensLeaveEmpPK().getCid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveEmSettingGetMemento#getEmploymentCode()
     */
    @Override
    public EmploymentCode getEmploymentCode() {
        return new EmploymentCode(this.entity.getKclmtCompensLeaveEmpPK().getEmpcd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveEmSettingGetMemento#getIsManaged()
     */
    @Override
    public ManageDistinct getIsManaged() {
        return ManageDistinct.valueOf(BooleanUtils.toInteger(this.entity.isManageAtr()));
    }

	@Override
	public CompensatoryAcquisitionUse getCompensatoryAcquisitionUse() {
		// TODO Auto-generated method stub
		return null;
	}
}
