package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.ManHrInputDisplayFormat;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;

/**
 * 
 * @author tutt
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class StartManHourInput {
	
	/** 作業枠利用設定 */
	private TaskFrameUsageSetting taskFrameUsageSetting;
	
	/** List<作業>*/
	private List<Task> tasks;
	
	/** Optional<工数入力表示フォーマット> */
	private Optional<ManHrInputDisplayFormat> manHrInputDisplayFormat;
	
}
