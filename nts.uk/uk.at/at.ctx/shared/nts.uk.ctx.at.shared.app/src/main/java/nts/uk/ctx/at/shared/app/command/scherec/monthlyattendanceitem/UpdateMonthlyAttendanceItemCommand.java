package nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem;

import lombok.Value;

/**
 * 
 * @author xuannt
 *
 */
@Value
public class UpdateMonthlyAttendanceItemCommand {
	
	Integer attendanceItemId;
	String displayName;
	Integer nameLineFeedPosition;

}
