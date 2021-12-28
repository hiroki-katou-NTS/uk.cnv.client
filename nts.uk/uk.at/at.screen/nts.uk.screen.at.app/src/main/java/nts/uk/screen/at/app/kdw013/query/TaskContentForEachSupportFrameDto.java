package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.TaskContentForEachSupportFrame;

/**
 * 
 * @author tutt
 *
 */
@Getter
@NoArgsConstructor
public class TaskContentForEachSupportFrameDto {

	/** 応援勤務枠No */
	public int frameNo;

	/** 作業内容 */
	public List<TaskContentDto> taskContent;
	
	/** 作業時間*/
	private Integer attendanceTime;

	public TaskContentForEachSupportFrameDto(TaskContentForEachSupportFrame domain) {
		this.frameNo = domain.getFrameNo().v();
		this.taskContent = domain.getTaskContent().stream().map(m -> new TaskContentDto(m)).collect(Collectors.toList());
		this.attendanceTime = domain.getAttendanceTime().isPresent() ? domain.getAttendanceTime().get().v() : null;
	}
}
