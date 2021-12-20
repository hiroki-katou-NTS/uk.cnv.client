package nts.uk.ctx.at.schedule.app.command.task.taskschedule;

import java.util.List;

import lombok.Value;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author quytb
 *
 */

@Value
public class TaskScheduleCommand {
	private String date;
	private List<String> employeeIds;
	private Integer startTime;
	private Integer endTime;
	private String taskCode;
	private Integer mode;
	
	public GeneralDate toDate() {
		return GeneralDate.fromString(this.date, "yyyy/MM/dd");
	}

}
