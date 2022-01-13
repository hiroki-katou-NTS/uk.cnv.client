package nts.uk.ctx.at.shared.app.command.scherec.dailyattendanceitem;

import lombok.Value;

/**
 * 
 * @author xuannt
 *
 */
@Value
public class UpdateDailyAttendanceItemCommand {
	Integer attendanceItemId;
	String displayName;
	Integer nameLineFeedPosition;
}
