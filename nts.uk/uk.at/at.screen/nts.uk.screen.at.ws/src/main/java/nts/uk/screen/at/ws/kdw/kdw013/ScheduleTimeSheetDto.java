package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Data
public class ScheduleTimeSheetDto {

	private Integer workNo;

	private int attendance;

	private int leaveWork;

	public static ScheduleTimeSheetDto toDto(ScheduleTimeSheet domain) {

		return new ScheduleTimeSheetDto(domain.getWorkNo().v(), domain.getAttendance().v(), domain.getLeaveWork().v());
	}
}
