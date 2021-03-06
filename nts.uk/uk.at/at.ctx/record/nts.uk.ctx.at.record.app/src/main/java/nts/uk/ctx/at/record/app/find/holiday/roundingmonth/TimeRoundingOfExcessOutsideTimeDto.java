package nts.uk.ctx.at.record.app.find.holiday.roundingmonth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Time rounding of Excess outside time DTO.
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeRoundingOfExcessOutsideTimeDto {

	/** The rounding unit. */
	// 単位
	private int roundingUnit;
	
	/** The rounding process. */
	// 時間外超過の端数処理
	private int roundingProcess;
}
