package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare;


import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.care.CareRemNumEachMonth;

import java.util.List;

public interface IGetChildcareRemNumEachMonth {
    /**
     * RequestList 342
     * [No.342]社員の月毎の確定済み子の看護を取得する
     * @param sid
     * @param yearMonths
     * @return
     */
    List<ChildNursingLeaveStatus> getMonthlyConfirmedCareForEmployees(String sid, List<YearMonth> yearMonths);
    /**
     * RequestList 344
     * [No.344]社員の月毎の確定済み介護を取得する
     * @param sid
     * @param yearMonths
     * @return
     */
    List<NursingCareLeaveMonthlyRemaining> getObtainMonthlyConfirmedCareForEmployees(String sid, List<YearMonth> yearMonths);
}
