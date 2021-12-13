package nts.uk.ctx.at.shared.pub.monthesultsvacationinfo.nursing.monthlyremainingnumberdata;

import nts.arc.time.calendar.period.YearMonthPeriod;

public interface ChildNursingVacationMonRemainNumberDataPub {
    /**
     * [No.342]社員の月毎の確定済み子の看護を取得する
     * [RQ] 342
     * @param sid
     * @param yearMonthPeriod
     * @return
     */
    public Object getNursingOfEmployeeMonthlyProbatedChildren(String sid, YearMonthPeriod yearMonthPeriod);
}
