package nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteDisplayInfo;

@Value
@AllArgsConstructor
public class TaskPaletteDisplayInfoDto {
	/**名称 **/
	private String taskPaletteName;
	/**備考**/
	private String remark;
	public static TaskPaletteDisplayInfoDto toDto(TaskPaletteDisplayInfo domain){
		return new TaskPaletteDisplayInfoDto(
				domain.getName().v(), 
				domain.getRemark().isPresent() ? domain.getRemark().get().v() : ""	);
				
	}
}
