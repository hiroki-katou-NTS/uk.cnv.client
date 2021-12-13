/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.compensatoryleave;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUse;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryDigestiveTimeUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.SubstituteHolidaySetting;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave.KclmtCompensLeaveCom;

/**
 * The Class JpaCompensLeaveComSetMemento.
 */
public class JpaCompensLeaveComSetMemento implements CompensatoryLeaveComSetMemento {
    
    /** The entity. */
    private KclmtCompensLeaveCom entity;
    
    /**
     * Instantiates a new jpa compens leave com set memento.
     *
     * @param entity the entity
     */
    public JpaCompensLeaveComSetMemento(KclmtCompensLeaveCom entity) {
        this.entity = entity;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComSetMemento#setCompanyId(java.lang.String)
     */
    @Override
    public void setCompanyId(String companyId) {
        this.entity.setCid(companyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComSetMemento#setIsManaged(nts.uk.ctx.at.shared.dom.
     * vacation.setting.ManageDistinct)
     */
    @Override
    public void setIsManaged(ManageDistinct managed) {
        this.entity.setManageAtr(BooleanUtils.toBoolean(managed.value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComSetMemento#setCompensatoryAcquisitionUse(nts.uk.ctx.
     * at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryAcquisitionUse)
     */
    @Override
    public void setCompensatoryAcquisitionUse(CompensatoryAcquisitionUse compensatoryAcquisitionUse) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComSetMemento#setCompensatoryDigestiveTimeUnit(nts.uk.
     * ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryDigestiveTimeUnit)
     */
    @Override
    public void setCompensatoryDigestiveTimeUnit(CompensatoryDigestiveTimeUnit compensatoryDigestiveTimeUnit) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComSetMemento#setCompensatoryOccurrenceSetting(java.util
     * .List)
     */
    @Override
    public void setCompensatoryOccurrenceSetting(List<CompensatoryOccurrenceSetting> occurrenceSetting) {
//        List<KocmtOccurrenceSet> listEntity = this.entity.getListOccurrence();
//        if (CollectionUtil.isEmpty(listEntity)) {
//            listEntity = new ArrayList<>();
//        }
//        for (CompensatoryOccurrenceSetting setting : occurrenceSetting) {
//            KocmtOccurrenceSet entityOccurrence = listEntity.stream()
//                    .filter(entity -> entity.getKocmtOccurrenceSetPK().getOccurrType() == setting.getOccurrenceType()
//                        .value)
//                    .findFirst()
//                    .orElse(new KocmtOccurrenceSet());
//            JpaCompensOccurrenceSettingSetMemento memento = new JpaCompensOccurrenceSettingSetMemento(
//                    this.entity.getCid(), entityOccurrence);
//            setting.saveToMemento(memento);
//            
//            listEntity.add(entityOccurrence);
//        }
//        this.entity.setListOccurrence(listEntity);
    }

	@Override
	public void setSubstituteHolidaySetting(SubstituteHolidaySetting substituteHolidaySetting) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLinkingManagementATR(ManageDistinct linkingManagementATR) {
		// TODO Auto-generated method stub
		
	}

}
