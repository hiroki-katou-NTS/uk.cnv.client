package nts.uk.screen.at.app.ksu003.getlistempworkhours;

import java.util.List;
import java.util.Optional;

import lombok.Data;
import nts.uk.ctx.at.shared.app.query.task.TaskData;

@Data
public class AllTaskScheduleDetail {
	//作業予定詳細
	private List<TaskScheduleDetailDto> taskScheduleDetail;
	//作業
	private List<Optional<TaskData>> task;
	
	private List<TaskData> taskInfo;
	public AllTaskScheduleDetail(List<TaskScheduleDetailDto> taskScheduleDetail, List<TaskData> taskInfo) {
		super();
		this.taskScheduleDetail = taskScheduleDetail;
		this.taskInfo = taskInfo;
	}
}
