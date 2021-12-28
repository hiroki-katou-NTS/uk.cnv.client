package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TemporaryTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;

@AllArgsConstructor
@Getter
public class TemporaryTimeOfDailyAttdCommand {
	// 勤務回数
	private Integer workTimes;

	// 1 ~ 3
	// 出退勤
	private List<TimeLeavingWorkCommand> timeLeavingWorks;

	public TemporaryTimeOfDailyAttd toDomain() {

		return new TemporaryTimeOfDailyAttd(new WorkTimes(this.getWorkTimes()),
				this.getTimeLeavingWorks().stream().map(x -> x.toDomain()).collect(Collectors.toList()));

	}
}
