package nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;

@Data
@AllArgsConstructor
public class WorkPlaceHistoryDto {
    private DatePeriod datePeriod;
    private AffWorkplaceHistoryItem historyItem;
}
