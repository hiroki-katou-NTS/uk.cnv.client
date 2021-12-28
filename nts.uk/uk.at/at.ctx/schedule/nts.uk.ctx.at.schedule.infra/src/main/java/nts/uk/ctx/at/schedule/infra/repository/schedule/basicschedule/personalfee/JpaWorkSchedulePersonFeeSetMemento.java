/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.repository.schedule.basicschedule.personalfee;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.PersonalFeeAmount;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.WorkSchedulePersonFeeSetMemento;
import nts.uk.ctx.at.schedule.infra.entity.schedule.basicschedule.personalfee.KscdtScheFee;
import nts.uk.ctx.at.schedule.infra.entity.schedule.basicschedule.personalfee.KscdtScheFeePK;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/**
 * The Class JpaWorkSchedulePersonFeeSetMemento.
 */
public class JpaWorkSchedulePersonFeeSetMemento implements WorkSchedulePersonFeeSetMemento {

	/** The entity. */
	private KscdtScheFee entity;
	
	
	/**
	 * Instantiates a new jpa work schedule person fee set memento.
	 *
	 * @param entity the entity
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 */
	public JpaWorkSchedulePersonFeeSetMemento(KscdtScheFee entity, String employeeId,
			GeneralDate baseDate) {
		if (entity.getKscdtScheFeePK() == null) {
			entity.setKscdtScheFeePK(new KscdtScheFeePK());
		}
		this.entity = entity;
		this.entity.getKscdtScheFeePK().setSid(employeeId);
		this.entity.getKscdtScheFeePK().setYmd(baseDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.
	 * WorkSchedulePersonFeeSetMemento#setNo(nts.uk.ctx.at.schedule.dom.schedule
	 * .basicschedule.personalfee.ExtraTimeItemNo)
	 */
	@Override
	public void setNo(ExtraTimeItemNo no) {
		this.entity.getKscdtScheFeePK().setNo(no.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.
	 * WorkSchedulePersonFeeSetMemento#setPersonalFeeAmount(nts.uk.ctx.at.
	 * schedule.dom.schedule.basicschedule.personalfee.PersonalFeeAmount)
	 */
	@Override
	public void setPersonalFeeAmount(PersonalFeeAmount personalFeeAmount) {
		this.entity.setPersonalFeeAmount(personalFeeAmount.v());
	}

}
