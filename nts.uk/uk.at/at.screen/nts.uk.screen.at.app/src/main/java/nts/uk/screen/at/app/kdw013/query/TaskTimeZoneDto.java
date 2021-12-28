package nts.uk.screen.at.app.kdw013.query;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeZone;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.TimeSpanForCalcDto;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskTimeZoneDto {

	/** 時間帯 */
	private TimeSpanForCalcDto caltimeSpan;

	/** 対象応援勤務枠 */
	private List<Integer> supNos;

	public static TaskTimeZoneDto fromDomain(TaskTimeZone domain) {
		return new TaskTimeZoneDto(
				new TimeSpanForCalcDto(domain.getCaltimeSpan().start(), domain.getCaltimeSpan().end()),
				CollectionUtil.isEmpty(domain.getSupNos()) ? Collections.emptyList()
						: domain.getSupNos().stream().map(x -> x.v()).collect(Collectors.toList()));
	}

}
