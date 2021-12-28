package nts.uk.screen.at.app.kdw013.command;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeZone;

/**
 * 
 * @author sonnlb
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTimeZoneCommand {

	/** 時間帯 */
	private TimeSpanForCalcCommand caltimeSpan;

	/** 対象応援勤務枠 */
	private List<Integer> supNos;

	public static TaskTimeZone toDomain(TaskTimeZoneCommand cmd) {
		return new TaskTimeZone(TimeSpanForCalcCommand.toDomain(cmd.getCaltimeSpan()),
				cmd.getSupNos().stream().map(x -> new SupportFrameNo(x)).collect(Collectors.toList()));
	}

}
