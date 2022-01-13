package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGateOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendanceLeavingGateOfDailyAttdDto {
	/** 入退門: 入退門 */
	private List<AttendanceLeavingGateDto> attendanceLeavingGates;

	public static AttendanceLeavingGateOfDailyAttdDto fromDomain(Optional<AttendanceLeavingGateOfDailyAttd> domain) {
		return domain
				.map(x -> new AttendanceLeavingGateOfDailyAttdDto(x.getAttendanceLeavingGates().stream()
						.map(alg -> AttendanceLeavingGateDto.fromDomain(alg)).collect(Collectors.toList())))
				.orElse(null);
	}
}
