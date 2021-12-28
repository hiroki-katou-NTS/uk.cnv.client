package nts.uk.screen.at.app.kdw013.command;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroup;

/**
 * 
 * @author sonnlb
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTaskTimeGroupCommand {

	/** 社員ID */
	private String sId;

	/** 年月日 */
	private GeneralDate date;

	/** 時間帯リスト */
	private List<TaskTimeZoneCommand> timezones;

	public TaskTimeGroup toDomain() {
		return new TaskTimeGroup(sId, date,
				timezones.stream().map(x -> TaskTimeZoneCommand.toDomain(x)).collect(Collectors.toList()));
	}

}
