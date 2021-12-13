package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author HieuLt
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddWorkScheduleCommand {
	public List<TaskScheduleDetailEmp> lstTaskScheduleDetailEmp;
	public String ymd;
	
}
