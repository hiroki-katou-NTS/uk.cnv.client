package nts.uk.screen.at.app.kdw013.query;

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
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskTimeGroupDto {

	/** 社員ID */
	private  String sId;

	/** 年月日 */
	private  GeneralDate date;

	/** 時間帯リスト */
	private List<TaskTimeZoneDto> timezones;

	public static TaskTimeGroupDto fromDomain(TaskTimeGroup domain) {
		return new TaskTimeGroupDto(domain.getSId(), domain.getDate(),
				domain.getTimezones().stream().map(x -> TaskTimeZoneDto.fromDomain(x)).collect(Collectors.toList()));
	}

}
