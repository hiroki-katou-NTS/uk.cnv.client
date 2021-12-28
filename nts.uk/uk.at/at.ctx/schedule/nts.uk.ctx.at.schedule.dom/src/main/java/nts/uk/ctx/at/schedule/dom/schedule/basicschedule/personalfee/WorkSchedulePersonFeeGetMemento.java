/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.dom.schedule.basicschedule.personalfee;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;

/**
 * The Interface WorkSchedulePersonFeeGetMemento.
 */
public interface WorkSchedulePersonFeeGetMemento {

	/**
	 * Gets the no.
	 *
	 * @return the no
	 */
	public ExtraTimeItemNo getNo();
	
	
	/**
	 * Gets the personal fee amount.
	 *
	 * @return the personal fee amount
	 */
	public PersonalFeeAmount getPersonalFeeAmount();
}
