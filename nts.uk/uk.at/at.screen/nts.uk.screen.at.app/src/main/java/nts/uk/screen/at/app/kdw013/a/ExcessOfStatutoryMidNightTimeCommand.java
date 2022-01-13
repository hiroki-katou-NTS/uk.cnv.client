package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class ExcessOfStatutoryMidNightTimeCommand {
	// 時間
	@Setter
	private TimeDivergenceWithCalculationCommand time;
	// 事前時間
	@Setter
	private Integer beforeApplicationTime;
}
