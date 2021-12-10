package nts.uk.ctx.at.shared.dom.worktime.common;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TranferDayTime {
	/**	振替時間 */
	private Integer tranferTime;
	/**	日数 */
	private Optional<Double> days;
}
