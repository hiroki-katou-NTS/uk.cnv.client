package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@Getter
public class DatePeriodCommand {
	/** 開始日 */
	private GeneralDate start;

	/** 終了日 */
	private GeneralDate end;

	public DatePeriod toDomain() {
		return new DatePeriod(start, end);
	}
}
