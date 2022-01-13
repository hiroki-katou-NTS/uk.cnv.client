package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TimeDivergenceWithCalculationCommand {
	private Integer time;
	//計算時間
	private Integer calcTime;
	//乖離時間
	private Integer divergenceTime;
}
