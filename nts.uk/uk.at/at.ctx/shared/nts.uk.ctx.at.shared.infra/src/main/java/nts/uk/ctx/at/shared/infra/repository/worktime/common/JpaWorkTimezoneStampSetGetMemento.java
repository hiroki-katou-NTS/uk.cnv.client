/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.worktime.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.worktime.common.FontRearSection;
import nts.uk.ctx.at.shared.dom.worktime.common.InstantRounding;
import nts.uk.ctx.at.shared.dom.worktime.common.MultiStampTimePiorityAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.PrioritySetting;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingSet;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingTime;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingTimeUnit;
import nts.uk.ctx.at.shared.dom.worktime.common.StampPiorityAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.Superiority;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSetGetMemento;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtComStmp;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * The Class JpaWorkTimezoneStampSetGetMemento.
 */
public class JpaWorkTimezoneStampSetGetMemento implements WorkTimezoneStampSetGetMemento {

	private KshmtWtComStmp kshmtWtComStmp;

	public JpaWorkTimezoneStampSetGetMemento(KshmtWtComStmp kshmtWtComStmp) {
		super();
		this.kshmtWtComStmp = kshmtWtComStmp;
	}
	
	
	@Override
	public RoundingTime getRoundingTime() {
		if (this.kshmtWtComStmp == null) {
			return null;
		}
		
		List<RoundingSet> roundingSet = new ArrayList<>();
		roundingSet.add(new RoundingSet(new InstantRounding
					(FontRearSection.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isFrontRearAtrAttendance())),
					RoundingTimeUnit.valueOf(this.kshmtWtComStmp.getRoundingTimeUnitAttendance())),
				Superiority.ATTENDANCE));
		
		roundingSet.add(new RoundingSet(new InstantRounding
					(FontRearSection.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isFrontRearAtrLeave())),
					RoundingTimeUnit.valueOf(this.kshmtWtComStmp.getRoundingTimeUnitLeave())),
				Superiority.OFFICE_WORK));
		
		roundingSet.add(new RoundingSet(new InstantRounding
				(FontRearSection.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isFrontRearAtrGoout())),
				RoundingTimeUnit.valueOf(this.kshmtWtComStmp.getRoundingTimeUnitGoout())),
			Superiority.GO_OUT));
		
		roundingSet.add(new RoundingSet(new InstantRounding
				(FontRearSection.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isFrontRearAtrTurnback())),
				RoundingTimeUnit.valueOf(this.kshmtWtComStmp.getRoundingTimeUnitTurnback())),
			Superiority.TURN_BACK));
		
		
		return new RoundingTime(
				NotUseAtr.valueOf(this.kshmtWtComStmp.getAttendanceMinuteLater())
				,NotUseAtr.valueOf(this.kshmtWtComStmp.getLeaveMinuteAgo())
				,roundingSet);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSetGetMemento#
	 * getPrioritySet()
	 */
	@Override
	public List<PrioritySetting> getPrioritySet() {
		if (this.kshmtWtComStmp == null) {
			return null;
		}
		
		List<PrioritySetting> prioritySetting = new ArrayList<>();
		
		prioritySetting.add(new PrioritySetting(
				MultiStampTimePiorityAtr.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isPiorityAtrAttendance())),
				StampPiorityAtr.GOING_WORK));
		
		prioritySetting.add(new PrioritySetting(
				MultiStampTimePiorityAtr.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isPiorityAtrLeave())),
				StampPiorityAtr.LEAVE_WORK));
		
		prioritySetting.add(new PrioritySetting(
				MultiStampTimePiorityAtr.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isPiorityAtrAttendanceGate())),
				StampPiorityAtr.ENTERING));
		
		prioritySetting.add(new PrioritySetting(
				MultiStampTimePiorityAtr.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isPiorityAtrLeaveGate())),
				StampPiorityAtr.EXIT));
		
		prioritySetting.add(new PrioritySetting(
				MultiStampTimePiorityAtr.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isPiorityAtrLogOn())),
				StampPiorityAtr.PCLOGIN));
		
		prioritySetting.add(new PrioritySetting(
				MultiStampTimePiorityAtr.valueOf(BooleanUtils.toInteger(this.kshmtWtComStmp.isPiorityAtrLogOff())),
				StampPiorityAtr.PC_LOGOUT));
		
		return prioritySetting;
	}

}
