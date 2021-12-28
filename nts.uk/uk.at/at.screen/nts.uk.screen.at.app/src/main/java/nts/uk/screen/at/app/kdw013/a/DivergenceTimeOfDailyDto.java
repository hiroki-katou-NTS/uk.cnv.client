package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.deviationtime.DivergenceTimeOfDaily;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DivergenceTimeOfDailyDto {

	private List<DivergenceTimeDto> divergenceTime;

	public static DivergenceTimeOfDailyDto fromDomain(DivergenceTimeOfDaily domain) {
		return new DivergenceTimeOfDailyDto(domain.getDivergenceTime().stream()
				.map(x -> DivergenceTimeDto.fromDomain(x)).collect(Collectors.toList()));
	}

}
