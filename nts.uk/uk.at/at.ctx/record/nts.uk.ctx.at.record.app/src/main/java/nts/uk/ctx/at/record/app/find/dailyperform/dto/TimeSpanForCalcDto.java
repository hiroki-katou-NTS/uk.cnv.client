package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;

/**
 * 計算用時間帯
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSpanForCalcDto {

	/** 開始時刻 */
//	@AttendanceItemLayout(layout = "A")
//	@AttendanceItemValue(itemId = -1, type = ValueType.TIME_WITH_DAY)
	private Integer start;

	/** 終了時刻 */
//	@AttendanceItemLayout(layout = "B")
//	@AttendanceItemValue(itemId = -1, type = ValueType.TIME_WITH_DAY)
	private Integer end;
	
	@Override
	public TimeSpanForCalcDto clone() {
		return new TimeSpanForCalcDto(start, end);
	}

	public static TimeSpanForCalcDto fromDomain(Optional<TimeSpanForCalc> domain) {
		return domain.map(ts -> new TimeSpanForCalcDto(ts.getStart().v(), ts.getEnd().v())).orElse(null);

	}
}