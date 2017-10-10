/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.repository.workplace.config.info;

import java.util.List;

import nts.uk.ctx.bs.employee.dom.workplace.HistoryId;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfoSetMemento;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceHierarchy;
import nts.uk.ctx.bs.employee.infra.entity.workplace.BsymtWkpConfigInfo;

/**
 * The Class JpaWorkplaceConfigInfoSetMemento.
 */
public class JpaWorkplaceConfigInfoSetMemento implements WorkplaceConfigInfoSetMemento {

    /** The lst entity. */
    private List<BsymtWkpConfigInfo> lstEntity;

    /**
     * Instantiates a new jpa workplace config info set memento.
     *
     * @param lstEntity
     *            the lst entity
     */
    public JpaWorkplaceConfigInfoSetMemento(List<BsymtWkpConfigInfo> lstEntity) {
        this.lstEntity = lstEntity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.bs.employee.dom.workplace.config.info.
     * WorkplaceConfigInfoSetMemento#setCompanyId(java.lang.String)
     */
    @Override
    public void setCompanyId(String companyId) {
        this.lstEntity.forEach(entity -> {
            entity.getBsymtWkpConfigInfoPK().setCid(companyId);
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.bs.employee.dom.workplace.config.info.
     * WorkplaceConfigInfoSetMemento#setHistoryId(nts.uk.ctx.bs.employee.dom.
     * workplace.HistoryId)
     */
    @Override
    public void setHistoryId(HistoryId historyId) {
        this.lstEntity.forEach(entity -> {
            entity.getBsymtWkpConfigInfoPK().setCid(historyId.v());
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.bs.employee.dom.workplace.config.info.
     * WorkplaceConfigInfoSetMemento#setWkpHierarchy(java.util.List)
     */
    @Override
    public void setWkpHierarchy(List<WorkplaceHierarchy> wkpHierarchy) {
        this.lstEntity.forEach(entity -> new JpaWorkplaceHierarchySetMemento(entity));
    }

}
