package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.MonthlyVacationDays;

/**
 * 紐付け情報
 */
@AllArgsConstructor
@Getter
@Setter
public class LinkingInformation {
    // 年月日  : 年月日
    private GeneralDate ymd;
    // 使用日数 : 使用日数
    private MonthlyVacationDays dateOfUse;
    // 発生日  :  年月日
    private GeneralDate occurrenceDate;
}
