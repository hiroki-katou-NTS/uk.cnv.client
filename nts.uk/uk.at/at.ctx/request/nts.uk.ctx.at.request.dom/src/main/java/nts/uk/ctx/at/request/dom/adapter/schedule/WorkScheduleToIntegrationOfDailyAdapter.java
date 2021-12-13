package nts.uk.ctx.at.request.dom.adapter.schedule;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

public interface WorkScheduleToIntegrationOfDailyAdapter {
	public Optional<IntegrationOfDaily> getWorkSchedule(String sid, GeneralDate date);
}
