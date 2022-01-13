package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WeeklyAgreegateResult {
    //week
    private int week;
    //労働時間
    private BigDecimal workingHours;

    //休日日数
    private BigDecimal holidays;
}