package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;

public interface HolidayRemainMergeAdapter {

	/**
	 * Mer RQ255,258,259,260,263
	 * @param employeeId
	 * @param period
	 * @return
	 */
	public HolidayRemainMerEx getRemainMer(String employeeId, YearMonthPeriod period);
	/**
	 * Mer RQ265,268,269,363,364,369
	 * @param employeeId
	 * @param currentMonth
	 * @param baseDate
	 * @param period
	 * @return
	 */
	public HdRemainDetailMerEx getRemainDetailMer(String employeeId, YearMonth currentMonth, 
			GeneralDate baseDate, DatePeriod period, CheckCallRequest checkCall);
}
