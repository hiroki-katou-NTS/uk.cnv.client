package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;

/**
 * 
 * @author tutt
 *
 */
@Getter
@Setter
public class StartManHourInput {
	
	/** 作業枠利用設定 */
	private TaskFrameUsageSetting taskFrameUsageSetting;
	
	/** List<作業>*/
	private List<Task> tasks;
	
	/** List<勤務場所> */
	private List<WorkLocation> workLocations;
	
}
