package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.MonthlyVacationRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;

import java.util.Optional;

/**
 * 代休未消化数
 */
@AllArgsConstructor
@Getter
public class NumberOfUndigestedSubstitutes {

    //日数 : 月別休暇残日数
    private LeaveRemainingDayNumber numOfDate;

    //時間 : 月別休暇残時間
    private Optional<MonthlyVacationRemainingTime> time;
}
