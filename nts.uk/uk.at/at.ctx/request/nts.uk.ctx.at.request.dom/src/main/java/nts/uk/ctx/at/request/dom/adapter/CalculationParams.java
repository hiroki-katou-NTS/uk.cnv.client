package nts.uk.ctx.at.request.dom.adapter;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.dailyattendancetime.ChildCareTimeZoneExport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.dailyattendancetime.OutingTimeZoneExport;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

import java.util.List;

/**
 * 計算のパラメータ
 */
@Value
public class CalculationParams {
    // 社員ID
    private String employeeId;

    // 年月日
    private GeneralDate date;

    // 勤務種類コード
    private WorkTypeCode workTypeCode;

    // 就業時間帯コード
    private WorkTimeCode workTimeCode;

    // 時間帯リスト
    private List<TimeZoneWithWorkNo> workTimeZones;

    // 休憩時間帯
    private List<TimeZoneWithWorkNo> breakTimeZones;

    // 外出時間帯
    private List<OutingTimeZoneExport> outingTimeZones;

    // 育児時間帯
    private List<ChildCareTimeZoneExport> childCareTimeZones;
}
