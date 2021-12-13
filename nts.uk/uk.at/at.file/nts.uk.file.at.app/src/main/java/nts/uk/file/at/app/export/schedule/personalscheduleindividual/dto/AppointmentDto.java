package nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.WeeklyAgreegateResult;

import java.util.List;

@AllArgsConstructor
@Data
public class AppointmentDto<T> {
    //List<勤務予定（勤務情報）dto>
    private List<WorkScheduleWorkInforDto> workInforDtoList;

    //List<週目, 労働時間, 休日日数>
    private List<WeeklyAgreegateResult> agreegateResults;

    //週合計の期間リスト
    private List<DatePeriod> periodItem;

    private DatePeriod period;
}