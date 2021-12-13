package nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;

import java.util.Optional;

/**
 * @author rafiqul.islam
 * 勤務予定（勤務情報）dto
 */
@Data
@Builder
@AllArgsConstructor
public class WorkScheduleWorkInforDto {
    // 年月日
    public GeneralDate date;
    // 出勤休日区分
    Optional<WorkStyle> workHolidayCls;

    // 勤務種類コード
    Optional<String> workTypeCode;

    // 勤務種類名
    Optional<String> workTypeName;

    // 就業時間帯コード
    Optional<String> workingHoursCode;

    // 就業時間帯名
    Optional<String> workingHoursName;

    // 開始時刻
    Optional<Integer> startTime;

    // 終了時刻
    Optional<Integer> endTime;
}