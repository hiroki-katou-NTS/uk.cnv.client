package nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Value;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPalette;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOneFrameDisplayInfo;

@Value
@Getter
public class TaskPaletteDto {
	
	/**ページ **/
	public int page;
	/**名称 **/
	public String taskPaletteName;
	/**作業パレットの作業 **/
	public Map<Integer, TaskPaletteOneFrameDisplayInfoDto> tasks;
	/**備考**/ 
	public String remark;
	
	public static TaskPaletteDto toDto(TaskPalette domain){
		
		Map<Integer , TaskPaletteOneFrameDisplayInfoDto> tasksMap = new HashMap<>();
		for (Entry<Integer, TaskPaletteOneFrameDisplayInfo> map : domain.getTasks().entrySet()){
			TaskPaletteOneFrameDisplayInfoDto value =  TaskPaletteOneFrameDisplayInfoDto.toDto(map.getValue());
			tasksMap.put(map.getKey(), value);
		}
		TaskPaletteDto dto  = new TaskPaletteDto(domain.getPage(), domain.getName().v(), tasksMap, domain.getRemark().isPresent() ? domain.getRemark().get().v() : "");
		return dto;
	}
}
