package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.app.find.worklocation.WorkLocationDto;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class StartManHourInputResultDto {
	
	/** 作業枠利用設定 */
	private TaskFrameUsageSettingDto taskFrameUsageSetting;
	
	/** List<作業>*/
	private List<TaskDto> tasks;
	
	/** List<勤務場所> */
	private List<WorkLocationDto> workLocations;

}
