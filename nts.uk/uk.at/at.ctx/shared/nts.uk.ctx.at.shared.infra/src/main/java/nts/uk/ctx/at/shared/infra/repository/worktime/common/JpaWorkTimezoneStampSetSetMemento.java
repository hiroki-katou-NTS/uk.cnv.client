/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.worktime.common;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.shared.dom.worktime.common.PrioritySetting;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingTime;
import nts.uk.ctx.at.shared.dom.worktime.common.StampPiorityAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.Superiority;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSetSetMemento;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtCom;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtComStmp;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtComStmpPK;

/**
 * The Class JpaWorkTimezoneStampSetSetMemento.
 */
public class JpaWorkTimezoneStampSetSetMemento implements WorkTimezoneStampSetSetMemento {

	/** The parent entity. */
	private KshmtWtComStmp kshmtWtComStmp;
	
	/**
	 * Instantiates a new jpa work timezone stamp set set memento.
	 *
	 * @param parentEntity the parent entity
	 */
	public JpaWorkTimezoneStampSetSetMemento(KshmtWtCom parentEntity) {
		super();
		this.initialEntity(parentEntity);
	}
	
	/**
	 * 
	 * @param set
	 */
	public void setWorkTimezoneStampSet(WorkTimezoneStampSet set) {
		this.kshmtWtComStmp.setPiorityAtrAttendance(set.getPrioritySets().stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.GOING_WORK )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLeave(set.getPrioritySets().stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.LEAVE_WORK )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrAttendanceGate(set.getPrioritySets().stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.ENTERING )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLeaveGate(set.getPrioritySets().stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.EXIT )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLogOn(set.getPrioritySets().stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.PCLOGIN )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLogOff(set.getPrioritySets().stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.PC_LOGOUT )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setAttendanceMinuteLater(set.getRoundingTime()
				.getAttendanceMinuteLaterCalculate().value);
		
		this.kshmtWtComStmp.setLeaveMinuteAgo(set.getRoundingTime() 
				.getLeaveWorkMinuteAgoCalculate().value);
		
		this.kshmtWtComStmp.setFrontRearAtrAttendance(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.ATTENDANCE)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitAttendance(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.ATTENDANCE)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
		
		this.kshmtWtComStmp.setFrontRearAtrLeave(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.OFFICE_WORK)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitLeave(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.OFFICE_WORK)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
		
		this.kshmtWtComStmp.setFrontRearAtrGoout(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.GO_OUT)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitGoout(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.GO_OUT)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
		
		this.kshmtWtComStmp.setFrontRearAtrTurnback(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.TURN_BACK)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitTurnback(set.getRoundingTime().getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.TURN_BACK)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
		
	}
	
	/**
	 * 
	 * @param parentEntity
	 */
	private void initialEntity(KshmtWtCom parentEntity) {
		if(parentEntity.getKshmtWtComStmp() == null) {
			parentEntity.setKshmtWtComStmp(new KshmtWtComStmp());
		}
		
		if(parentEntity.getKshmtWtComStmp().getKshmtWtComStmpPK() == null) {
			KshmtWtComStmpPK pk = new KshmtWtComStmpPK();
			pk.setCid(parentEntity.getKshmtWorktimeCommonSetPK().getCid());
			pk.setWorktimeCd(parentEntity.getKshmtWorktimeCommonSetPK().getWorktimeCd());
			pk.setWorkFormAtr(parentEntity.getKshmtWorktimeCommonSetPK().getWorkFormAtr());
			pk.setWorkTimeSetMethod(parentEntity.getKshmtWorktimeCommonSetPK().getWorktimeSetMethod());
			parentEntity.getKshmtWtComStmp().setKshmtWtComStmpPK(pk);
		}
		
		this.kshmtWtComStmp = parentEntity.getKshmtWtComStmp();
	}


	@Override
	public void setRoundingTime(RoundingTime rdSet) {
		// TODO 自動生成されたメソッド・スタブ
		this.kshmtWtComStmp.setAttendanceMinuteLater(rdSet
				.getAttendanceMinuteLaterCalculate().value);
		
		this.kshmtWtComStmp.setLeaveMinuteAgo(rdSet 
				.getLeaveWorkMinuteAgoCalculate().value);
		
		this.kshmtWtComStmp.setFrontRearAtrAttendance(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.ATTENDANCE)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitAttendance(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.ATTENDANCE)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
		
		this.kshmtWtComStmp.setFrontRearAtrLeave(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.OFFICE_WORK)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitLeave(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.OFFICE_WORK)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
		
		this.kshmtWtComStmp.setFrontRearAtrGoout(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.GO_OUT)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitGoout(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.GO_OUT)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
		
		this.kshmtWtComStmp.setFrontRearAtrTurnback(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.TURN_BACK)
				.map(p -> BooleanUtils.toBoolean(p.getRoundingSet().getFontRearSection().value))
				.findFirst().orElse(false));
		
		this.kshmtWtComStmp.setRoundingTimeUnitTurnback(rdSet.getRoundingSets().stream()
				.filter(p -> p.getSection() == Superiority.TURN_BACK)
				.map(p -> p.getRoundingSet().getRoundingTimeUnit().value)
				.findFirst().orElse(0));
	}


	@Override
	public void setPrioritySet(List<PrioritySetting> prSet) {
		// TODO 自動生成されたメソッド・スタブ
		this.kshmtWtComStmp.setPiorityAtrAttendance(prSet.stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.GOING_WORK )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLeave(prSet.stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.LEAVE_WORK )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrAttendanceGate(prSet.stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.ENTERING )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLeaveGate(prSet.stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.EXIT )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLogOn(prSet.stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.PCLOGIN )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
		this.kshmtWtComStmp.setPiorityAtrLogOff(prSet.stream()
				.filter(p -> p.getStampAtr() == StampPiorityAtr.PC_LOGOUT )
				.map(p -> BooleanUtils.toBoolean(p.getPriorityAtr().value))
				.findFirst().orElse(false));
	}
	
}
