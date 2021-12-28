/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.repository.schedule.basicschedule.personalfee;

import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.PersonalFeeAmount;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.WorkSchedulePersonFeeGetMemento;
import nts.uk.ctx.at.schedule.infra.entity.schedule.basicschedule.personalfee.KscdtScheFee;
import nts.uk.ctx.at.schedule.infra.entity.schedule.basicschedule.personalfee.KscdtScheFeePK;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/**
 * The Class JpaWorkSchedulePersonFeeGetMemento.
 */
public class JpaWorkSchedulePersonFeeGetMemento implements WorkSchedulePersonFeeGetMemento {

	/** The entity. */
	private KscdtScheFee entity;
	
	
	/**
	 * Instantiates a new jpa work schedule person fee get memento.
	 *
	 * @param entity the entity
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 */
	public JpaWorkSchedulePersonFeeGetMemento(KscdtScheFee entity) {
		if (entity.getKscdtScheFeePK() == null) {
			entity.setKscdtScheFeePK(new KscdtScheFeePK());
		}
		
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.
	 * WorkSchedulePersonFeeGetMemento#getNo()
	 */
	@Override
	public ExtraTimeItemNo getNo() {
		return ExtraTimeItemNo.valueOf(this.entity.getKscdtScheFeePK().getNo());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee.
	 * WorkSchedulePersonFeeGetMemento#getPersonalFeeAmount()
	 */
	@Override
	public PersonalFeeAmount getPersonalFeeAmount() {
		return new PersonalFeeAmount(this.entity.getPersonalFeeAmount());
	}

}
