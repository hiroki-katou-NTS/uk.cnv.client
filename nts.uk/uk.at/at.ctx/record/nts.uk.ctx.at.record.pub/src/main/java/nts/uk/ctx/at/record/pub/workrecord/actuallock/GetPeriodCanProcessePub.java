package nts.uk.ctx.at.record.pub.workrecord.actuallock;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.AchievementAtrExport;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.EmploymentHistoryExport;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.IgnoreFlagDuringLockExport;

/**
 * @author thanh_nx
 *
 *         実績処理できる期間を取得するPub
 */
public interface GetPeriodCanProcessePub {
	public List<DatePeriod> get(String employeeId, DatePeriod period, List<EmploymentHistoryExport> listEmploymentHis,
			IgnoreFlagDuringLockExport ignoreFlagDuringLock, AchievementAtrExport achievementAtr);
}
