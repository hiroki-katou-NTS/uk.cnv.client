/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.repository.plannedyearholiday.frame;

import java.math.BigDecimal;

import nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.NotUseAtr;
import nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.PlanYearHolidayFrameGetMemento;
import nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.PlanYearHolidayFrameName;
import nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.PlanYearHolidayFrameNo;
import nts.uk.ctx.at.schedule.infra.entity.plannedyearholiday.frame.KscstPlanYearHdFrame;
import nts.uk.ctx.at.shared.dom.common.CompanyId;

/**
 * The Class JpaPlanYearHolidayFrameGetMemento.
 */
public class JpaPlanYearHolidayFrameGetMemento implements PlanYearHolidayFrameGetMemento{
	
	/** The kscst plan year hd frame. */
	private KscstPlanYearHdFrame kscstPlanYearHdFrame;
	
	/**
	 * Instantiates a new jpa plan year holiday frame get memento.
	 *
	 * @param kscstPlanYearHdFrame the kscst plan year hd frame
	 */
	public JpaPlanYearHolidayFrameGetMemento(KscstPlanYearHdFrame kscstPlanYearHdFrame) {
		this.kscstPlanYearHdFrame = kscstPlanYearHdFrame;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.PlanYearHolidayFrameGetMemento#getCompanyId()
	 */
	@Override
	public CompanyId getCompanyId() {
		return new CompanyId(this.kscstPlanYearHdFrame.getKscstPlanYearHdFramePK().getCid());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.PlanYearHolidayFrameGetMemento#getPlanYearHolidayFrameNo()
	 */
	@Override
	public PlanYearHolidayFrameNo getPlanYearHolidayFrameNo() {
		return new PlanYearHolidayFrameNo(BigDecimal.valueOf(this.kscstPlanYearHdFrame.getKscstPlanYearHdFramePK().getPlanYearHdNo()));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.PlanYearHolidayFrameGetMemento#getUseClassification()
	 */
	@Override
	public NotUseAtr getUseClassification() {
		return NotUseAtr.valueOf(this.kscstPlanYearHdFrame.isUseAtr());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.schedule.dom.plannedyearholiday.frame.PlanYearHolidayFrameGetMemento#getPlanYearHolidayFrameName()
	 */
	@Override
	public PlanYearHolidayFrameName getPlanYearHolidayFrameName() {
		return new PlanYearHolidayFrameName(this.kscstPlanYearHdFrame.getPlanYearHdName());
	}
	
}
