package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskScheduleDetailEmp {
	
	public String empId; 
	
	public List<TaskScheduleDetailDto> taskScheduleDetail;

}
