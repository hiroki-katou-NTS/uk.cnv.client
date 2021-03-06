/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration;

import lombok.Getter;
import lombok.Setter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.DomainObject;
import nts.arc.time.GeneralDate;

/**
 * The Class PublicHoliday.
 */
// 公休起算日
@Getter
@Setter
public class PublicHoliday extends DomainObject
				implements PublicHolidayManagementStartDate
{
	/** The date. */
	// 年月日
	private GeneralDate date;
	
	/** The day month. */
	// 月日
	private Integer dayMonth;
	
	/** The determine start date. */
	// 起算日指定方法
	private DayOfPublicHoliday determineStartDate;
	
	/**
	 * Instantiates a new public holiday.
	 *
	 * @param date the date
	 * @param dayMonth the day month
	 * @param determineStartDate the determine start date
	 */
	public PublicHoliday(GeneralDate date, Integer dayMonth, DayOfPublicHoliday determineStartDate) {
		this.date = date;
		this.dayMonth = dayMonth;
		this.determineStartDate = determineStartDate;
		
		if ((determineStartDate == DayOfPublicHoliday.DESIGNATE_BY_YEAR_MONTH_DAY && date == null)
				|| (determineStartDate == DayOfPublicHoliday.DESIGNATE_BY_MONTH_DAY && dayMonth == null)) {
			throw new BusinessException("Msg_995");
		}
	}
}
