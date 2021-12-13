package nts.uk.screen.at.app.ksu003.getlistempworkhours;

import java.util.List;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.app.query.task.TaskData;

@Value
public class TaskInfoDto {
	//年月日
		public GeneralDate date; 
		//社員ID
		public String empID;
		//  <<Optional>> OrderedList<作業予定詳細、作業>: OrderedList<作業予定詳細、作業>
		//作業予定詳細
		private List<TaskScheduleDetailDto> taskScheduleDetail;

		public TaskInfoDto(GeneralDate date, String empID, List<TaskScheduleDetailDto> taskScheduleDetail) {
			super();
			this.date = date;
			this.empID = empID;
			this.taskScheduleDetail = taskScheduleDetail;
		}
}
