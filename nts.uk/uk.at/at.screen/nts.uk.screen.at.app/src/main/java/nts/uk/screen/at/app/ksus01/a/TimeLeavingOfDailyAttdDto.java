package nts.uk.screen.at.app.ksus01.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeLeavingOfDailyAttdDto {

	private List<TimeLeavingWorkDto> timeLeavingWorks;

	private int workTimes;

	public static TimeLeavingOfDailyAttdDto fromDomain(Optional<TimeLeavingOfDailyAttd> domain) {
		return domain.map(x -> new TimeLeavingOfDailyAttdDto(

				x.getTimeLeavingWorks().stream().map(tl -> TimeLeavingWorkDto.fromDomain(tl))
						.collect(Collectors.toList()),
				x.getWorkTimes().v())).orElse(null);
	}
}
