package nts.uk.file.at.app.export.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.MngDataStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.CompensatoryDayoffDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;

import java.util.Optional;

/**
 * 発生取得明細
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OccurrenceAcquisitionDetail {
    // 年月日
    private CompensatoryDayoffDate date;

    // 発生消化区分
    private OccurrenceDigClass occurrenceDigCls;

    // 発生使用数
    private AccumulationAbsenceDetail.NumberConsecuVacation occurrencesUseNumber;

    // 状態
    private MngDataStatus status;

    // 期限日
    private Optional<GeneralDate> deadline;

    private Optional<Boolean> expiringThisMonth;
}
