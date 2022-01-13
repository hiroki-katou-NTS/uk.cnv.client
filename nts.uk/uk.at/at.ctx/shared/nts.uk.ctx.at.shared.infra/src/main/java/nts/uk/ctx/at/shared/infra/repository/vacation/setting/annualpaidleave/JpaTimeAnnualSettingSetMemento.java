/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.vacation.setting.annualpaidleave;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeAnnualRoundProcesCla;
import nts.uk.ctx.at.shared.dom.vacation.setting.TimeDigestiveUnit;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualSettingSetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualLeaveTimeDay;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.TimeAnnualMaxDay;
import nts.uk.ctx.at.shared.infra.entity.vacation.setting.annualpaidleave.KshmtHdpaidTimeSet;

/**
 * The Class JpaTimeVacationSettingSetMemento.
 */
public class JpaTimeAnnualSettingSetMemento implements TimeAnnualSettingSetMemento {

    /** The entity. */
    private KshmtHdpaidTimeSet entity;

    /**
     * Instantiates a new jpa time vacation setting set memento.
     *
     * @param entity the entity
     */
    public JpaTimeAnnualSettingSetMemento(KshmtHdpaidTimeSet entity) {
        this.entity = entity;
    }
    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * TimeVacationSettingSetMemento#setCompanyId(java.lang.String)
     */
    @Override
    public void setCompanyId(String companyId) {
        this.entity.setCid(companyId);
    }

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * TimeVacationSettingSetMemento#setTimeManageType(nts.uk.ctx.at.shared.dom.
     * vacation.setting.ManageDistinct)
     */
    
    @Override
    public void setTimeManageType(ManageDistinct timeManageType) {
        this.entity.setTimeManageAtr(timeManageType.value);
    }

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * TimeVacationSettingSetMemento#setTimeUnit(nts.uk.ctx.at.shared.dom.
     * vacation.setting.TimeVacationDigestiveUnit)
     */
    @Override
    public void setTimeUnit(TimeDigestiveUnit timeUnit) {
        this.entity.setTimeUnit(timeUnit.value);
    }

    /*
     * (non-Javadoc)
     *
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * TimeVacationSettingSetMemento#setMaxYearDayLeave(nts.uk.ctx.at.shared.dom
     * .vacation.setting.annualpaidleave.YearVacationTimeMaxDay)
     */
    @Override
    public void setMaxYearDayLeave(TimeAnnualMaxDay maxYearDayLeave) {
        this.entity.setTimeMaxDayManageAtr(maxYearDayLeave.manageType.value);
        this.entity.setTimeMaxDayReference(maxYearDayLeave.reference.value);
        this.entity.setTimeMaxDayUnifComp(maxYearDayLeave.maxNumberUniformCompany.v());
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
//     * TimeVacationSettingSetMemento#setEnoughTimeOneDay(boolean)
//     */
//    @Override
//    public void setEnoughTimeOneDay(boolean isEnoughTimeOneDay) {
//        this.entity.setIsEnoughTimeOneDay(isEnoughTimeOneDay == true ? 1 : 0);
//    }

    /*
     * (non-Javadoc)
     * @see nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.
     * TimeAnnualSettingSetMemento#setRoundProcessClassific
     * (nts.uk.ctx.at.shared.dom.vacation.setting.TimeAnnualRoundProcesCla)
     */
	@Override
	public void setRoundProcessClassific(TimeAnnualRoundProcesCla timeAnnualRoundProcesCla) {
		this.entity.setRoundProcessCla(BooleanUtils.toBoolean(timeAnnualRoundProcesCla.value));
	}

	@Override
	public void setTimeAnnualLeaveTimeDay(TimeAnnualLeaveTimeDay timeAnnualLeaveTimeDay) {
		this.entity.setTimeOfDayRef(timeAnnualLeaveTimeDay.getTimeOfDayReference().value);
		this.entity.setUniformTime(timeAnnualLeaveTimeDay.getUniformTime().isPresent() ? timeAnnualLeaveTimeDay.getUniformTime().get().v() : 0 );
		this.entity.setContractTimeRound(timeAnnualLeaveTimeDay.getContractTimeRound().isPresent() ? timeAnnualLeaveTimeDay.getContractTimeRound().get().value : 0 );
		
	}
}
