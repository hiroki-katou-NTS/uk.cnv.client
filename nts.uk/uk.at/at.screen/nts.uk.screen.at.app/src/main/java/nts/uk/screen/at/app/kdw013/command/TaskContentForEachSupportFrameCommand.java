package nts.uk.screen.at.app.kdw013.command;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.TaskContentForEachSupportFrame;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * 
 * @author tutt
 *
 */
@Data
@NoArgsConstructor
public class TaskContentForEachSupportFrameCommand {

	/** 応援勤務枠No */
	private int frameNo;

	/** 作業内容 */
	private List<TaskContentCommand> taskContents;
	
	/** 作業時間*/
	private Integer attendanceTime;

	public TaskContentForEachSupportFrame toDomain() {
		return new TaskContentForEachSupportFrame(new SupportFrameNo(this.frameNo), 
				this.taskContents.stream().map(m -> m.toDomain()).collect(Collectors.toList()), 
				Optional.of(new AttendanceTime(this.attendanceTime)));
	}
}
