/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/**
 * The Class WorkSchedulePersonFee.
 */
// 勤務予定人件費
@Getter
public class WorkSchedulePersonFee extends DomainObject{
	
	/** The no. */
	// NO
	private ExtraTimeItemNo no;

	/** The personal fee amount. */
	// 人件費金額
	private PersonalFeeAmount personalFeeAmount; 
	
	
	/**
	 * Instantiates a new work schedule person fee.
	 *
	 * @param memento the memento
	 */
	public WorkSchedulePersonFee(WorkSchedulePersonFeeGetMemento memento) {
		this.no = memento.getNo();
		this.personalFeeAmount = memento.getPersonalFeeAmount();
	}
	
	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(WorkSchedulePersonFeeSetMemento memento){
		memento.setNo(this.no);
		memento.setPersonalFeeAmount(this.personalFeeAmount);
	}
}
