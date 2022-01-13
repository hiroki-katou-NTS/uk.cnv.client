package nts.uk.ctx.at.aggregation.ac.workschedule;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.adapter.workschedule.WorkScheduleAdapter;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkSchedulePub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@Stateless
public class WorkScheduleAdapterImpl implements WorkScheduleAdapter {
	
	@Inject
	private WorkSchedulePub workSchedulePub;

	@Override
	public List<IntegrationOfDaily> getList(List<String> employeeIds, DatePeriod period) {
		
		return workSchedulePub.getListWorkSchedule(employeeIds, period);
	}

}
