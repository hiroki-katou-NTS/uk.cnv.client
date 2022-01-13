package nts.uk.ctx.at.request.dom.application.annualholiday;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

@Data
@AllArgsConstructor
/**
 * 年休管理表_次回年休付与日
 */
public class GrantPeriodDto {
	
	// 期間
	private DatePeriod period;
	
	// 次回年休付与日
	private Optional<GeneralDate> nextGrantDate;
}
