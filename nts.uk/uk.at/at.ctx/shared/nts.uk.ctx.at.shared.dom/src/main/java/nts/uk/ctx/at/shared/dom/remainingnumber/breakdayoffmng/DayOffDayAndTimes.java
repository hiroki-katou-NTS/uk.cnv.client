package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantTime;

/**
 * 代休発生数合計
 */
@Getter
@Setter
@AllArgsConstructor
public class DayOffDayAndTimes extends DomainObject {
	/** 日数 */
	private MonthVacationGrantDay day;
	/** 時間 */
	private Optional<MonthVacationGrantTime> time;
}
