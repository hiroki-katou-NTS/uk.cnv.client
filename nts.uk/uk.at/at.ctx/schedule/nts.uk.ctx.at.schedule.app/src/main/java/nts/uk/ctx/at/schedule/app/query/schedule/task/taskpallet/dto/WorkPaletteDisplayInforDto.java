package nts.uk.ctx.at.schedule.app.query.schedule.task.taskpallet.dto;

import java.util.List;

import lombok.Value;

@Value
public class WorkPaletteDisplayInforDto {

	private List<TaskPaletteOrganizationDto> lstTaskPaletteOrganizationDto;

	private TaskPaletteDto taskPalette;
}
