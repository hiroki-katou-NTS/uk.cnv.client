package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;

@AllArgsConstructor
@Getter
public class DivergenceTimeOfDailyCommand {
	private List<DivergenceTimeCommand> divergenceTime;

	public DivergenceTimeOfDaily toDomain() {
		return new DivergenceTimeOfDaily(
				this.divergenceTime.stream().map(x -> x.toDomain()).collect(Collectors.toList()));
	}
}
