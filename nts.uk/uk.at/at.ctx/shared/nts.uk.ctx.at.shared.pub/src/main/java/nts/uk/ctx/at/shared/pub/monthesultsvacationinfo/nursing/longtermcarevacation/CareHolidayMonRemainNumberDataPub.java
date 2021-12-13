package nts.uk.ctx.at.shared.pub.monthesultsvacationinfo.nursing.longtermcarevacation;

import nts.arc.time.calendar.period.YearMonthPeriod;

public interface CareHolidayMonRemainNumberDataPub {
    /**
     *
     * @param sid
     * @param yearMonthPeriod
     * @return
     */
    public Object getAMonthlyConfirmedCareForEmployees(String sid, YearMonthPeriod yearMonthPeriod);
}
