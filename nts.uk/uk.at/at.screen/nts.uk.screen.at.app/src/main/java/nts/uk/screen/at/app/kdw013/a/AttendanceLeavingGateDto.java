package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AttendanceLeavingGateDto {
	/** 勤務NO: 勤務NO */
	private Integer workNo;

	/** 入門: 勤怠打刻 */
	private WorkStampDto attendance;

	/** 退門: 勤怠打刻 */
	private WorkStampDto leaving;

	public static AttendanceLeavingGateDto fromDomain(AttendanceLeavingGate domain) {

		return new AttendanceLeavingGateDto(domain.getWorkNo().v(),
				domain.getAttendance().map(x-> WorkStampDto.fromDomain(x)).orElse(null),
				domain.getLeaving().map(x-> WorkStampDto.fromDomain(x)).orElse(null)
				);
	}
}
