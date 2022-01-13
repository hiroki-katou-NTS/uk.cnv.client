package nts.uk.ctx.at.function.dom.adapter.child;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface GetRemainingNumberCareNurseAdapter {
    ChildNursingLeaveThisMonthFutureSituation getChildCareRemNumWithinPeriod(
            String companyId,
            String employeeId,
            DatePeriod period,
            GeneralDate criteriaDate);

    NursingCareLeaveThisMonthFutureSituation getNursingCareLeaveThisMonthFutureSituation(
            String companyId,
            String employeeId,
            DatePeriod period,
            GeneralDate criteriaDate);
}
