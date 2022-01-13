package nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto;

import java.util.List;
import java.util.Optional;

import lombok.Value;

@Value
public class WorkPaletteDisplayInforDto {

	private List<TaskPaletteOrganizationDto> lstTaskPaletteOrganizationDto;
	
	private TaskPaletteDto taskPalette;
}
