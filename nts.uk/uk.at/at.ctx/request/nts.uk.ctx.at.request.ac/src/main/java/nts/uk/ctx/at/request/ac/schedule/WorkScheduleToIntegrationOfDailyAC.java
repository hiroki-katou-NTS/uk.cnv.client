package nts.uk.ctx.at.request.ac.schedule;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.adapter.schedule.WorkScheduleToIntegrationOfDailyAdapter;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkScheduleToIntegrationOfDailyPub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@Stateless
public class WorkScheduleToIntegrationOfDailyAC implements WorkScheduleToIntegrationOfDailyAdapter {

	@Inject
	private WorkScheduleToIntegrationOfDailyPub pub;

	@Override
	public Optional<IntegrationOfDaily> getWorkSchedule(String sid, GeneralDate date) {
		return pub.getWorkSchedule(sid, date).map(x -> (IntegrationOfDaily) x);
	}

}
