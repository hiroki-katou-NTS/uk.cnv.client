package nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DatePeriodListDto<T> {
    //$取得期間
    private DatePeriod period;

    //週合計の期間リスト
    private List<String> periodList;
    //週合計の期間リスト
    private List<DatePeriod> periodItem;
}