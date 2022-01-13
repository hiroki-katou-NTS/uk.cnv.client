package nts.uk.screen.at.app.kdw013.query;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.TaskBlockDetailContent;
import java.util.stream.Collectors;

/**
 * 
 * @author tutt
 *
 */
@Getter
@NoArgsConstructor
public class TaskBlockDetailContentDto {

	/** 開始時刻 */
	public int startTime;

	/** 終了時刻 */
	public int endTime;

	/** 作業詳細 */
	public List<TaskContentForEachSupportFrameDto> taskContents;

	public TaskBlockDetailContentDto(TaskBlockDetailContent domain) {
		this.startTime = domain.getStartTime().v();
		this.endTime = domain.getEndTime().v();
		this.taskContents = domain.getTaskContents().stream().map(m -> new TaskContentForEachSupportFrameDto(m))
				.collect(Collectors.toList());
	}
}
