/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.compensatoryleave;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUse;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryDigestiveTimeUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.SubstituteHolidaySetting;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.compensatoryleave.KclmtCompensLeaveCom;

/**
 * The Class JpaCompensLeaveComGetMemento.
 */
public class JpaCompensLeaveComGetMemento implements CompensatoryLeaveComGetMemento {
    
    /** The entity. */
    private KclmtCompensLeaveCom entity;
    
    /**
     * Instantiates a new jpa compens leave com get memento.
     *
     * @param entity the entity
     */
    public JpaCompensLeaveComGetMemento(KclmtCompensLeaveCom entity) {
        this.entity = entity;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComGetMemento#getCompanyId()
     */
    @Override
    public String getCompanyId() {
        return this.entity.getCid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComGetMemento#getIsManaged()
     */
    @Override
    public ManageDistinct getIsManaged() {
        return ManageDistinct.valueOf(BooleanUtils.toInteger(this.entity.isManageAtr()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComGetMemento#getCompensatoryAcquisitionUse()
     */
    @Override
    public CompensatoryAcquisitionUse getCompensatoryAcquisitionUse() {
        return null;        
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComGetMemento#getCompensatoryDigestiveTimeUnit()
     */
    @Override
    public CompensatoryDigestiveTimeUnit getCompensatoryDigestiveTimeUnit() {
    	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.
     * CompensatoryLeaveComGetMemento#getCompensatoryOccurrenceSetting()
     */
    @Override
    public List<CompensatoryOccurrenceSetting> getCompensatoryOccurrenceSetting() {
        List<CompensatoryOccurrenceSetting> listSetting = new ArrayList<>();
//        for (KocmtOccurrenceSet entity : this.entity.getListOccurrence()) {
//            listSetting.add(new CompensatoryOccurrenceSetting(new JpaCompensOccurrenceSettingGetMemento(entity)));
//        }
        return listSetting;
    }

	@Override
	public SubstituteHolidaySetting getSubstituteHolidaySetting() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ManageDistinct getLinkingManagementATR() {
		// TODO Auto-generated method stub
		return null;
	}

}
