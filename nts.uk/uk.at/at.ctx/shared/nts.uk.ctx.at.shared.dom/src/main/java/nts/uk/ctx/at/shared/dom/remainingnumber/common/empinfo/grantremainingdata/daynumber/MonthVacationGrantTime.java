package nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber;

import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

/**
 * @author thanh_nx
 *
 *         月別休暇付与時間
 */
@TimeRange(min = "00:00", max = "999:59")
public class MonthVacationGrantTime extends TimeDurationPrimitiveValue<MonthVacationGrantTime> {
	private static final long serialVersionUID = 1L;

	public MonthVacationGrantTime(int timeAsMinutes) {
		super(timeAsMinutes);
	}
}
