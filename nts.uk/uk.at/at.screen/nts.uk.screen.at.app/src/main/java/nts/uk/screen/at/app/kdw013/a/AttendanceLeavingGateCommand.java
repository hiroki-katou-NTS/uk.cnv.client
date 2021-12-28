package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGate;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

@AllArgsConstructor
@Getter
public class AttendanceLeavingGateCommand {
	/** 勤務NO: 勤務NO */
	private Integer workNo;

	/** 入門: 勤怠打刻 */
	private WorkStampCommand attendance;

	/** 退門: 勤怠打刻 */
	private WorkStampCommand leaving;

	public AttendanceLeavingGate toDomain() {

		return new AttendanceLeavingGate(
				new WorkNo(this.getWorkNo()), 
				this.getAttendance().toDomain(),
				this.getAttendance().toDomain());
	}
}
