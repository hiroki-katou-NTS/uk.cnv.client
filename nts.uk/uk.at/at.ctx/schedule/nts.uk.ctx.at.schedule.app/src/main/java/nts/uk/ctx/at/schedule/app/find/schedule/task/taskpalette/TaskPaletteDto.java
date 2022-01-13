package nts.uk.ctx.at.schedule.app.find.schedule.task.taskpalette;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganization;

/**
 * 
 * @author quytb
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskPaletteDto {	
	private String displayName;
	private Integer page;		
	private String name;
	
	
	public static TaskPaletteDto setData(TaskPaletteOrganization domain, String displayName) {				
		return new TaskPaletteDto(displayName, domain.getPage(), domain.getDisplayInfo().getName().v());
	}
}
