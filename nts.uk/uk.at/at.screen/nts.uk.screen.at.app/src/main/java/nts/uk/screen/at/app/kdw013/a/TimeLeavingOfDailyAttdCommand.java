package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;

@AllArgsConstructor
@Getter
public class TimeLeavingOfDailyAttdCommand {

	private List<TimeLeavingWorkCommand> timeLeavingWorks;

	private int workTimes;

	public TimeLeavingOfDailyAttd toDomain() {
		return new TimeLeavingOfDailyAttd(
				timeLeavingWorks.stream().map(lw -> lw.toDomain()).collect(Collectors.toList()),
				new WorkTimes(this.getWorkTimes()));
	}
}