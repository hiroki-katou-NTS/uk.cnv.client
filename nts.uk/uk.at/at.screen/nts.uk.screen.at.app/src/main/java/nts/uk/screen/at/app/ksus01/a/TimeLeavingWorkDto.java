package nts.uk.screen.at.app.ksus01.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeLeavingWorkDto {

	private int workNo;

	private TimeActualStampDto attendanceStamp;

	private TimeActualStampDto leaveStamp;

	private boolean canceledLate = false;

	private boolean CanceledEarlyLeave = false;

	public static TimeLeavingWorkDto toDto() {
		return new TimeLeavingWorkDto();
	}

	public static TimeLeavingWorkDto fromDomain(TimeLeavingWork domain) {

		return new TimeLeavingWorkDto(domain.getWorkNo().v(),
				TimeActualStampDto.fromDomain(domain.getAttendanceStamp()),
				TimeActualStampDto.fromDomain(domain.getLeaveStamp()), 
				domain.isCanceledLate(),
				domain.isCanceledEarlyLeave());
	}
}
