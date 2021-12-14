package nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.AchievementAtrImport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.IgnoreFlagDuringLockImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;

/**
 * @author thanh_nx
 *
 *         実績処理できる期間を取得するAdapter
 */
public interface GetPeriodCanProcesseAdapter {
	public List<DatePeriod> get(String employeeId, DatePeriod period, List<EmploymentHistoryImported> listEmploymentHis,
			IgnoreFlagDuringLockImport ignoreFlagDuringLock, AchievementAtrImport achievementAtr);
}
