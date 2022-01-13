package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGateOfDailyAttd;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
public class AttendanceLeavingGateOfDailyAttdCommand {
	/** 入退門: 入退門 */
	private List<AttendanceLeavingGateCommand> attendanceLeavingGates;

	public AttendanceLeavingGateOfDailyAttd toDomain() {

		return new AttendanceLeavingGateOfDailyAttd(
				this.getAttendanceLeavingGates().stream().map(x -> x.toDomain()).collect(Collectors.toList()));
	}
}
