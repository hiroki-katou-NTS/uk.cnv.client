package nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto;

import lombok.Getter;
import lombok.Value;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOneFrameDisplayInfo;

@Value
@Getter
public class TaskPaletteOneFrameDisplayInfoDto {
	/** 作業コード **/
	public String taskCode;
	/** 作業状態**/
	public int taskStatus;
	/** 作業名称 **/
	public String taskName;
	/**  作業略名**/
	public String taskAbName;
	
	public static TaskPaletteOneFrameDisplayInfoDto toDto(TaskPaletteOneFrameDisplayInfo domain){
		TaskPaletteOneFrameDisplayInfoDto dto  = new TaskPaletteOneFrameDisplayInfoDto(
				domain.getTaskCode().v(),
				domain.getTaskStatus().value,
				domain.getTaskName().isPresent() ? domain.getTaskName().get().v() : "" ,
				domain.getTaskAbName().isPresent() ? domain.getTaskAbName().get().v() : ""); 
		return dto;
	}

}
