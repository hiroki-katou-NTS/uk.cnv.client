/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.compensatoryleave;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSettingSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave.KclmtCompensLeaveEmp;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave.KclmtCompensLeaveEmpPK;

/**
 * The Class JpaCompensLeaveEmSettingSetMemento.
 */
public class JpaCompensLeaveEmSettingSetMemento implements CompensatoryLeaveEmSettingSetMemento {
    
    /** The entity. */
    private KclmtCompensLeaveEmp entity;
    
    /**
     * Instantiates a new jpa compens leave em setting set memento.
     *
     * @param entity the entity
     */
    public JpaCompensLeaveEmSettingSetMemento(KclmtCompensLeaveEmp entity) {
        // check exist primary key
        if (entity.getKclmtCompensLeaveEmpPK() == null) {
            entity.setKclmtCompensLeaveEmpPK(new KclmtCompensLeaveEmpPK());
        }
        this.entity = entity;
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveEmSettingSetMemento#setCompanyId(java.lang.String)
     */
    @Override
    public void setCompanyId(String companyId) {
        this.entity.getKclmtCompensLeaveEmpPK().setCid(companyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveEmSettingSetMemento#setEmploymentCode(nts.uk.ctx.at.
     * shared.dom.vacation.setting.compensatoryleave.EmploymentCode)
     */
    @Override
    public void setEmploymentCode(EmploymentCode employmentCode) {
        this.entity.getKclmtCompensLeaveEmpPK().setEmpcd(employmentCode.v());
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveEmSettingSetMemento#setIsManaged(nts.uk.ctx.at.shared.
     * dom.vacation.setting.ManageDistinct)
     */
    @Override
    public void setIsManaged(ManageDistinct isManaged) {
        this.entity.setManageAtr(BooleanUtils.toBoolean(isManaged.value));
    }



}
