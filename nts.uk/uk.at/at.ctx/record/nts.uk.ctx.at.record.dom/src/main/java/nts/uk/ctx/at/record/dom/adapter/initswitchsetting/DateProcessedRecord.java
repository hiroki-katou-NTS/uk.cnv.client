package nts.uk.ctx.at.record.dom.adapter.initswitchsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author sonnh1
 *
 */
@Data
@AllArgsConstructor
public class DateProcessedRecord {
	private Integer closureID;
	private YearMonth targetDate;
	private DatePeriod datePeriod;
}
