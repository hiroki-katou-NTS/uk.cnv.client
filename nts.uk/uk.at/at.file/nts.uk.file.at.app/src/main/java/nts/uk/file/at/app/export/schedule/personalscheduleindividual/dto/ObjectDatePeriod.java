package nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;

@Data
@AllArgsConstructor
public class ObjectDatePeriod {

    private String startDate;

    private String endDate;
}