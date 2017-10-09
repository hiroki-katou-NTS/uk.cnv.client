/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.repository.employeeinfo;

import nts.uk.ctx.at.schedule.dom.employeeinfo.MonthlyPatternWorkScheduleCre;
import nts.uk.ctx.at.schedule.dom.employeeinfo.PersonalWorkScheduleCreSetGetMemento;
import nts.uk.ctx.at.schedule.dom.employeeinfo.TimeZoneScheduledMasterAtr;
import nts.uk.ctx.at.schedule.dom.employeeinfo.WorkScheduleBasicCreMethod;
import nts.uk.ctx.at.schedule.dom.employeeinfo.WorkScheduleBusCal;
import nts.uk.ctx.at.schedule.dom.employeeinfo.WorkScheduleMasterReferenceAtr;
import nts.uk.ctx.at.schedule.infra.entity.employeeinfo.KscmtPewScheduleCreset;

/**
 * The Class JpaPersonalWorkScheduleCreSetGetMemento.
 */
public class JpaPersonalWorkScheduleCreSetGetMemento
		implements PersonalWorkScheduleCreSetGetMemento {
	
	/** The entity. */
	private KscmtPewScheduleCreset entity;

	/**
	 * Instantiates a new jpa personal work schedule cre set get memento.
	 *
	 * @param entity the entity
	 */
	public JpaPersonalWorkScheduleCreSetGetMemento(KscmtPewScheduleCreset entity) {
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.employeeinfo.
	 * PersonalWorkScheduleCreSetGetMemento#getBasicCreateMethod()
	 */
	@Override
	public WorkScheduleBasicCreMethod getBasicCreateMethod() {
		return WorkScheduleBasicCreMethod.valueOf(this.entity.getBasicCreMethod());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.employeeinfo.
	 * PersonalWorkScheduleCreSetGetMemento#getEmployeeId()
	 */
	@Override
	public String getEmployeeId() {
		return this.entity.getSid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.employeeinfo.
	 * PersonalWorkScheduleCreSetGetMemento#getMonthlyPatternWorkScheduleCre()
	 */
	@Override
	public MonthlyPatternWorkScheduleCre getMonthlyPatternWorkScheduleCre() {
		return new MonthlyPatternWorkScheduleCre(
				TimeZoneScheduledMasterAtr.valueOf(this.entity.getMpWorkscheduleCre()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.employeeinfo.
	 * PersonalWorkScheduleCreSetGetMemento#getWorkScheduleBusCal()
	 */
	@Override
	public WorkScheduleBusCal getWorkScheduleBusCal() {
		return new WorkScheduleBusCal(
				WorkScheduleMasterReferenceAtr.valueOf(this.entity.getReferBusDayCal()),
				WorkScheduleMasterReferenceAtr.valueOf(this.entity.getReferBasicWork()),
				TimeZoneScheduledMasterAtr.valueOf(this.entity.getReferWorkingHour()));
	}

}
