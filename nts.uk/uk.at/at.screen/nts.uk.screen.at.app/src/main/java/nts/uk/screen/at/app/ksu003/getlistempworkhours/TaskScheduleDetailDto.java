package nts.uk.screen.at.app.ksu003.getlistempworkhours;

import lombok.Value;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.TimeSpanForCalcDto;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskScheduleDetail;
import nts.uk.ctx.at.shared.app.query.task.TaskData;

@Value
public class TaskScheduleDetailDto {
	//作業コード
	public String taskCode;
	//時間帯
	public TimeSpanForCalcDto timeSpanForCalcDto;

	public TaskData taskData;

	
	public static TaskScheduleDetailDto toDto(TaskScheduleDetail domain, TaskData taskData){
		TimeSpanForCalcDto timeSpanForCalcDto = new TimeSpanForCalcDto(domain.getTimeSpan().start(), domain.getTimeSpan().end());
		TaskScheduleDetailDto dto = new TaskScheduleDetailDto(domain.getTaskCode().v(), timeSpanForCalcDto, taskData);
		return dto;
	}

	public TaskScheduleDetailDto(String taskCode, TimeSpanForCalcDto timeSpanForCalcDto, TaskData taskData) {
		super();
		this.taskCode = taskCode;
		this.timeSpanForCalcDto = timeSpanForCalcDto;
		this.taskData = taskData;
	}
}
