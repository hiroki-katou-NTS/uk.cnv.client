package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;

/**
 * @author thanh_nx
 *
 *         代休使用数合計
 */
@Getter
@Setter
@AllArgsConstructor
public class DayOffDayTimeUse extends DomainObject {
	/** 日数 */
	private LeaveUsedDayNumber day;
	/** 時間 */
	private Optional<LeaveUsedTime> time;
}