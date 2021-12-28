package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class WithinStatutoryTimeOfDailyCommand {
	// 就業時間
	@Setter
	private Integer workTime;
	// 実働就業時間
	@Setter
	private Integer actualWorkTime;
	// 所定内割増時間
	private Integer withinPrescribedPremiumTime;
	/** 実働所定内割増時間 */
	private Integer actualWithinPremiumTime;
	// 所定内深夜時間
	@Setter
	private WithinStatutoryMidNightTimeCommand withinStatutoryMidNightTime;
	/** 就業時間金額 **/
	private Integer withinWorkTimeAmount;
}
