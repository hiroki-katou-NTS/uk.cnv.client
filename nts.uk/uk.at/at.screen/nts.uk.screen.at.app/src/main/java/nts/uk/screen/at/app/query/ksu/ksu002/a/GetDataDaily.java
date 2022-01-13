/**
 * 
 */
package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.PeriodListPeriodDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.DisplayInWorkInfoInput;

/**
 * @author chungnt
 *
 */
@Stateless
public class GetDataDaily {

	@Inject
	private GetWorkActualOfWorkInfo002 getWorkRecord;
	
	@Inject
	private KSU002Finder kSU002Finder;

	public List<WorkScheduleWorkInforDto.Achievement> getDataDaily(DisplayInWorkInfoInput param) {
		
		PeriodListPeriodDto periodListPeriod = kSU002Finder.getPeriodList(param.getPeriod(), param.getStartWeekDate());
		
		param.startDate = periodListPeriod.datePeriod.start().toString("yyyy/MM/dd");
		param.endDate = periodListPeriod.datePeriod.end().toString("yyyy/MM/dd");	

		// lay data Daily
		List<WorkScheduleWorkInforDto> listDataDaily = getWorkRecord.getDataActualOfWorkInfo(param);

		return listDataDaily.stream().map(m -> {
			return WorkScheduleWorkInforDto
					.Achievement
					.builder()
					.employeeId(m.getEmployeeId())
					.date(m.getDate())
					.workTypeCode(m.getWorkTypeCode())
					.workTypeName(m.getWorkTypeName())
					.workTimeCode(m.getWorkTimeCode())
					.workTimeName(m.getWorkTimeName())
					.startTime(m.getStartTime())
					.endTime(m.getEndTime())
					.build();
		}).collect(Collectors.toList());
	}
}
