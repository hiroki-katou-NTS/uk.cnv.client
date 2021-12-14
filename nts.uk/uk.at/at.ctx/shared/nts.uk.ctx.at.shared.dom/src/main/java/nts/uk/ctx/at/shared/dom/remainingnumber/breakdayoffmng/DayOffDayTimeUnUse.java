package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;

/**
 * @author thanh_nx
 *
 *         代休未消化数
 */
@Getter
@Setter
@AllArgsConstructor
public class DayOffDayTimeUnUse extends DomainObject {
	/** 日数 */
	private LeaveRemainingDayNumber day;
	/** 時間 */
	private Optional<LeaveRemainingTime> time;
}