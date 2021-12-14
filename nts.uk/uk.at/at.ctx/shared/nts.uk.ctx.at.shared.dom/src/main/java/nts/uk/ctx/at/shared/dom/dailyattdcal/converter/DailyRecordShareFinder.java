package nts.uk.ctx.at.shared.dom.dailyattdcal.converter;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

public interface DailyRecordShareFinder {
	
	public Optional<IntegrationOfDaily> find(String employeeId, GeneralDate date);

	public List<IntegrationOfDaily> findByListEmployeeId(List<String> employeeId,
			DatePeriod baseDate);
	
	public List<IntegrationOfDaily> find(String employeeId, List<GeneralDate> lstDate);
}
