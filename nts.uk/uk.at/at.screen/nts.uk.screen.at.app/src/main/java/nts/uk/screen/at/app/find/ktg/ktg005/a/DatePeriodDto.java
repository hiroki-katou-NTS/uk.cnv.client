package nts.uk.screen.at.app.find.ktg.ktg005.a;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatePeriodDto {
    private String startDate;

    private String endDate;

    public DatePeriod convertToDate(String format){
        return new DatePeriod(GeneralDate.fromString(this.startDate,format), GeneralDate.fromString(this.endDate,format));
    }
}
