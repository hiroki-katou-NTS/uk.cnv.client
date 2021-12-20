package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskScheduleDetail;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Value
@AllArgsConstructor
public class TaskScheduleDetailDto {
	public String taskCode;
	//時間帯
	public TimeSpanForCalcDto timeSpanForCalcDto;
	
	public static TaskScheduleDetailDto toDto(TaskScheduleDetail domain){
		TimeSpanForCalcDto timeSpanForCalcDto = new TimeSpanForCalcDto(domain.getTimeSpan().start(), domain.getTimeSpan().end());
		TaskScheduleDetailDto dto = new TaskScheduleDetailDto(domain.getTaskCode().v(), timeSpanForCalcDto);
		return dto;
	}
	
	public static TaskScheduleDetail toDomain(TaskScheduleDetailDto dto){
		TimeSpanForCalc timeSpanForCalc = new TimeSpanForCalc(new TimeWithDayAttr(dto.getTimeSpanForCalcDto().getStart()), new TimeWithDayAttr(dto.getTimeSpanForCalcDto().getEnd()));
		TaskScheduleDetail domain = new TaskScheduleDetail(new TaskCode(dto.getTaskCode()), timeSpanForCalc);
		return domain;
	}
}
