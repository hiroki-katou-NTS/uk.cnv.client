package nts.uk.screen.at.app.kdw013.a.deletetimezoneattendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.AttendanceByTimezoneDeletion;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.AttendanceDeletionStatusEnum;

/**
 * 
 * @author sonnlb
 */
@AllArgsConstructor
@Getter
public class DeleteAttendanceByTimeZoneCommand {
	// 枠NO
	private int supNo;
	// 状態
	private int status;

	public AttendanceByTimezoneDeletion toDomain() {
		return new AttendanceByTimezoneDeletion(new SupportFrameNo(supNo),
				EnumAdaptor.valueOf(status, AttendanceDeletionStatusEnum.class));
	}
}
