package nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Value;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganization;
import nts.uk.ctx.at.shared.app.find.workrule.shiftmaster.TargetOrgIdenInforDto;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;



@Value
@Getter
public class TaskPaletteOrganizationDto {
	/* 対象組織*/
	public TargetOrgIdenInforDto  targetOrgIdenInforDto;
	/* ページ */
	public int page;
	/*表示情報*/
	public TaskPaletteDisplayInfoDto taskPaletteDisplayInfoDto;
	
	public Map<Integer , String> tasks;

	public TaskPaletteOrganizationDto(TargetOrgIdenInforDto targetOrgIdenInforDto, int page,
			TaskPaletteDisplayInfoDto taskPaletteDisplayInfoDto, Map<Integer, String> tasks) {
		super();
		this.targetOrgIdenInforDto = targetOrgIdenInforDto;
		this.page = page;
		this.taskPaletteDisplayInfoDto = taskPaletteDisplayInfoDto;
		this.tasks = tasks;
	}
	public static TaskPaletteOrganizationDto toDto(TaskPaletteOrganization domain){
		TargetOrgIdenInforDto idenInforDto = TargetOrgIdenInforDto.toDto(domain.getTargetOrg());
		int pages = domain.getPage();
		
		Map<Integer , String> tasksMap = new HashMap<>();
		for (Map.Entry<Integer, TaskCode> map : domain.getTasks().entrySet()){
			tasksMap.put(map.getKey(), map.getValue().v());
		}
		TaskPaletteDisplayInfoDto displayInfoDto = TaskPaletteDisplayInfoDto.toDto(domain.getDisplayInfo());
		TaskPaletteOrganizationDto dto = new TaskPaletteOrganizationDto(idenInforDto, pages, displayInfoDto, tasksMap);
		return dto;
	}
}
