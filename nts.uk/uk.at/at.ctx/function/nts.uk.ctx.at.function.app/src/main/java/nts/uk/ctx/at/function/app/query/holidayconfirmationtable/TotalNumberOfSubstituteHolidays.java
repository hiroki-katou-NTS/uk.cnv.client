package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.MonthlyVacationDays;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.MonthlyVacationRemainingTime;

import java.util.Optional;


/**
 * 代休発生数合計
 */
@AllArgsConstructor
@Getter
@Setter
public class TotalNumberOfSubstituteHolidays {
    //日数 : 月別休暇付与日数;
    private MonthlyVacationDays numberOfDate;

    //時間 : 月別休暇残時間;
    private Optional<MonthlyVacationRemainingTime> time;
}
