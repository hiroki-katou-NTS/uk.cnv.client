package nts.uk.screen.at.app.kdw013.command;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.TaskBlockDetailContent;
import nts.uk.shr.com.time.TimeWithDayAttr;
import java.util.stream.Collectors;

/**
 * 
 * @author tutt
 *
 */
@Data
@NoArgsConstructor
public class TaskBlockDetailContentCommand {

	/** 開始時刻 */
	private int startTime;

	/** 終了時刻 */
	private int endTime;

	/** 作業詳細 */
	private List<TaskContentForEachSupportFrameCommand> taskContents;

	public TaskBlockDetailContent toDomain() {
		return new TaskBlockDetailContent(new TimeWithDayAttr(this.startTime), new TimeWithDayAttr(this.endTime),
				taskContents.stream().map(m -> m.toDomain()).collect(Collectors.toList()));
	}
}
