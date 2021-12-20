package nts.uk.ctx.at.aggregation.ac.dailyrecord;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.adapter.dailyrecord.DailyRecordAdapter;
import nts.uk.ctx.at.record.pub.dailyresult.DailyRecordPub;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@Stateless
public class DailyRecordAdapterImpl implements DailyRecordAdapter {
	
	@Inject
	private DailyRecordPub dailyRecordPub;

	@Override
	public List<IntegrationOfDaily> getDailyRecordByScheduleManagement(List<String> employeeIds, DatePeriod period) {

		return dailyRecordPub.getDailyRecordByScheduleManagement(employeeIds, period);
	}

}
