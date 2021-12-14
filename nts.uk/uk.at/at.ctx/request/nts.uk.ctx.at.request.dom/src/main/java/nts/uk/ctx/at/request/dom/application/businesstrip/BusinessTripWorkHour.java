package nts.uk.ctx.at.request.dom.application.businesstrip;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * @author anhnm
 * 出張勤務時刻
 *
 */
@Getter
@AllArgsConstructor
public class BusinessTripWorkHour {

    // 勤務NO
    private WorkNo workNo;

    // 開始時刻
    private Optional<TimeWithDayAttr> startDate;

    // 終了時刻
    private Optional<TimeWithDayAttr> endDate;
}
