package nts.uk.ctx.at.record.pub.remainnumber.annualleave;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;

public interface FindAnnLeaUsedDaysFromPreviousToNextGrantDatePub {

	/**
	 * [RQ717]前回付与から次回付与までの年休使用数を求める（時間使用を含まない）
	 * @param employeeId
	 * @param criteriaDate
	 * @return
	 */
	AnnualLeaveUsedDayNumber findUsedDays(String employeeId, GeneralDate criteriaDate);
}
