package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.MonthlyVacationDays;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.MonthlyVacationUsageTime;

import java.util.Optional;


/**
 * 代休使用数合計
 */
@AllArgsConstructor
@Getter
@Setter
public class TotalNumberOfSubstituteHolidaysUsed {
    // 日数 : 月別休暇使用日数
    private MonthlyVacationDays numOfDate;
    // 時間 : 月別休暇残時間
    private Optional<MonthlyVacationUsageTime> time;
}
