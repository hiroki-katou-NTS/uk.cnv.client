package nts.uk.screen.at.app.ksu003.getworkselectioninfor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class GetWorkSelectionInforDto {
	
	private  List<TaskData> lstTaskDto;
	
	private WorkPaletteDisplayInforDto workPaletteDisplayInforDto;

}
