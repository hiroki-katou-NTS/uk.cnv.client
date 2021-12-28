/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/**
 * The Interface WorkSchedulePersonFeeGetMemento.
 */
public interface WorkSchedulePersonFeeSetMemento {

	/**
	 * Sets the no.
	 *
	 * @param no the new no
	 */
	public void setNo(ExtraTimeItemNo no);
	
	
	/**
	 * Sets the personal fee amount.
	 *
	 * @param personalFeeAmount the new personal fee amount
	 */
	public void setPersonalFeeAmount(PersonalFeeAmount personalFeeAmount);
}
