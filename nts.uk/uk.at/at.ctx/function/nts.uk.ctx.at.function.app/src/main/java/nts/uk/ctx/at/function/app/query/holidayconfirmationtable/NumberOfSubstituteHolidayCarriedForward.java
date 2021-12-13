package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.MonthlyVacationRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;

import java.util.Optional;

/**
 * 代休繰越数
 */
@AllArgsConstructor
@Setter
@Getter
public class NumberOfSubstituteHolidayCarriedForward {
    //日数 : 月別休暇残日数
    private LeaveRemainingDayNumber leaveRemainingDayNumber;
    //時間 : 月別休暇残時間
    private Optional<MonthlyVacationRemainingTime> time;
}
