package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.CompensatoryDayoffDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.MngHistDataAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;

import java.util.Optional;

/**
 * 発生取得明細
 */
@AllArgsConstructor
@Setter
@Getter
public class OccurrenceAcquisitionDetails {

    // 年月日 :発生消化年月日
    private CompensatoryDayoffDate date;
    // 状態  : 管理データ状態区分
    private MngHistDataAtr status;
    // 発生使用数 : 逐次発生の休暇数
    private AccumulationAbsenceDetail.NumberConsecuVacation numberConsecuVacation;
    // 発生消化区分 : 発生消化区分
    private OccurrenceDigClass occurrenceDigClass;
    // 当月で期限切れ
    private Optional<Boolean> isExpiredInCurrentMonth;
    //期限日 : 年月日
    private GeneralDate deadline;
}
