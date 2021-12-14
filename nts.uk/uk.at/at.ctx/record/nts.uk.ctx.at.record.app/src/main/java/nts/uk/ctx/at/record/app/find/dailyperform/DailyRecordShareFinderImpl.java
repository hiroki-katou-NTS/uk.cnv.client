package nts.uk.ctx.at.record.app.find.dailyperform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@Stateless
public class DailyRecordShareFinderImpl implements DailyRecordShareFinder {

	@Inject
	private DailyRecordWorkFinder finder;

	@Override
	public Optional<IntegrationOfDaily> find(String employeeId, GeneralDate date) {
		DailyRecordDto dto = finder.find(employeeId, date);
		return dto == null ? Optional.empty() : Optional.of(dto.toDomain(employeeId, date));
	}

	@Override
	public List<IntegrationOfDaily> findByListEmployeeId(List<String> employeeId, DatePeriod baseDate) {
		List<DailyRecordDto> listDailyResult = finder.find(employeeId, baseDate);
		return listDailyResult.stream().map(x -> x.toDomain(x.getEmployeeId(), x.getDate())).collect(Collectors.toList());
	}

	@Override
	public List<IntegrationOfDaily> find(String employeeId, List<GeneralDate> lstDate) {
		Map<String, List<GeneralDate>> param = new HashMap<>();
		param.put(employeeId, lstDate);
		List<DailyRecordDto> listDailyResult = finder.find(param);
		return listDailyResult.stream().map(x -> x.toDomain(x.getEmployeeId(), x.getDate())).collect(Collectors.toList());
	}

}
