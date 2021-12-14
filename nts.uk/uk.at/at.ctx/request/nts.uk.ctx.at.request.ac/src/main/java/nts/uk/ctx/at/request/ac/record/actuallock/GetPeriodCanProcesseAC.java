package nts.uk.ctx.at.request.ac.record.actuallock;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.GetPeriodCanProcessePub;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.AchievementAtrExport;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.EmploymentHistoryExport;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.IgnoreFlagDuringLockExport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.GetPeriodCanProcesseAdapter;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.AchievementAtrImport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.IgnoreFlagDuringLockImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;

@Stateless
public class GetPeriodCanProcesseAC implements GetPeriodCanProcesseAdapter {

	@Inject
	private GetPeriodCanProcessePub pub;

	@Override
	public List<DatePeriod> get(String employeeId, DatePeriod period, List<EmploymentHistoryImported> listEmploymentHis,
			IgnoreFlagDuringLockImport ignoreFlagDuringLock, AchievementAtrImport achievementAtr) {
		return pub.get(employeeId, period,
				listEmploymentHis.stream()
						.map(x -> new EmploymentHistoryExport(x.getEmployeeId(), x.getEmploymentCode(), x.getPeriod()))
						.collect(Collectors.toList()),
				EnumAdaptor.valueOf(ignoreFlagDuringLock.value, IgnoreFlagDuringLockExport.class),
				EnumAdaptor.valueOf(achievementAtr.value, AchievementAtrExport.class));
	}

}
