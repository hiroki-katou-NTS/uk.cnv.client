package nts.uk.ctx.at.request.dom.service;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * @author thanh_nx
 * 
 *         申請休出残業時間
 */
@AllArgsConstructor
@Getter
public class CalculationOfTransferTimeResult {
	
	//就業時間帯コード
	private Optional<String> workTimeCode;

	// 申請休出時間合計
	private Optional<AttendanceTime> holidayTransTime;

	// 申請残業時間合計
	private Optional<AttendanceTime> overTransTime;

	public static CalculationOfTransferTimeResult create (Optional<String> workTimeCode, int holidayTransTime, int overTransTime) {
		return new CalculationOfTransferTimeResult(workTimeCode, Optional.of(new AttendanceTime(holidayTransTime)),
				Optional.of(new AttendanceTime(overTransTime)));
	}

}
