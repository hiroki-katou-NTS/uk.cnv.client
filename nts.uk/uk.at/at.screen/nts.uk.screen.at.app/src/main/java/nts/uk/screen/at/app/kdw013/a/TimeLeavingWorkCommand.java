package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

@AllArgsConstructor
@Getter
public class TimeLeavingWorkCommand {
	private int workNo;

	private TimeActualStampCommand attendanceStamp;

	private TimeActualStampCommand leaveStamp;

	private boolean canceledLate = false;

	private boolean CanceledEarlyLeave = false;

	public TimeLeavingWork toDomain() {
		return new TimeLeavingWork(new WorkNo(this.getWorkNo()),
				Optional.ofNullable(this.getAttendanceStamp().toDomain()),
				Optional.ofNullable(this.getLeaveStamp().toDomain()), 
				this.isCanceledLate(),
				this.isCanceledEarlyLeave());
	}
}
