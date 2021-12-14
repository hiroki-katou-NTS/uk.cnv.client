/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getsendingperiod;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.start.ChangeMonthParam;
import nts.uk.screen.at.app.ksu001.start.DataBasicDto;

/**
 * @author laitv 
 *
 */
@Stateless
public class ChangeMonthFinder {
	
	@Inject
	private GetSendingPeriodScreenQuery getSendingPeriodScreenQuery;
	@Inject
	private ChangePeriodInShift changePeriodInShift;
	@Inject
	private ChangePeriodInWorkInfomation changePeriodInWorkInfo;

	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	public ChangeMonthDto getData(ChangeMonthParam param) {
		
		DatePeriod datePeriod = null;
		if (param.modePeriod == 1) {
			DatePeriod currentPeriod = new DatePeriod(GeneralDate.fromString(param.startDate, DATE_FORMAT),
					GeneralDate.fromString(param.endDate, DATE_FORMAT));
			datePeriod = getSendingPeriodScreenQuery.getSendingPeriod(currentPeriod, param.isNextMonth, false);

		} else if (param.modePeriod == 3) {
			GeneralDate startDate = GeneralDate.fromString(param.startDate, DATE_FORMAT);
			if(param.isNextMonth) {
				startDate = startDate.addMonths(1);
    		} else {
    			startDate = startDate.addMonths(-1);
    		}
			YearMonth yearMonth = YearMonth.of(startDate.year(), startDate.month());
			datePeriod = DatePeriod.daysFirstToLastIn(yearMonth);
		}
		
		if (param.viewMode.equals("time") || param.viewMode.equals("shortName")) {
			ChangePeriodInWorkInfoParam param1 = new ChangePeriodInWorkInfoParam(datePeriod.start(), datePeriod.end(),
					param.unit, param.workplaceId, param.workplaceGroupId, param.sids, param.getActualData);
			ChangePeriodInWorkInfoResult resultOtherMode = changePeriodInWorkInfo.getData(param1);
			ChangeMonthDto result = convertDataTimeShortNameMode(datePeriod, resultOtherMode);
			return result;

		} else if (param.viewMode.equals("shift")) {
			ChangePeriodInShiftParam param2 = new ChangePeriodInShiftParam(datePeriod.start(), datePeriod.end(), param.unit,
					param.workplaceId, param.workplaceGroupId, param.sids, param.listShiftMasterNotNeedGetNew,
					param.getActualData);
			ChangePeriodInShiftResult resultShiftMode = changePeriodInShift.getData(param2);
			ChangeMonthDto result = convertDataShiftMode(datePeriod, resultShiftMode);
			return result;
		}
		return null;
	}
	
	private ChangeMonthDto convertDataShiftMode(DatePeriod datePeriod,ChangePeriodInShiftResult resultShiftMode ) {
		ChangeMonthDto result = new ChangeMonthDto();

		DataBasicDto dataBasicDto = new DataBasicDto();
		dataBasicDto.setStartDate(datePeriod.start());
		dataBasicDto.setEndDate(datePeriod.end());
		result.setDataBasicDto(dataBasicDto);
		
		List<EmployeeInformationDto> listEmpInfo = resultShiftMode.listEmpInfo.stream().map(item -> {
			return new EmployeeInformationDto(item.getEmployeeId(), item.getEmployeeCode(),
					item.getBusinessName().toString());
		}).collect(Collectors.toList());
		result.setListEmpInfo(listEmpInfo);

		List<DateInformationDto> listDateInfo = resultShiftMode.dataSpecDateAndHolidayDto.listDateInfo.stream().map(i -> {
			return new DateInformationDto(i);
		}).collect(Collectors.toList());
		result.setListDateInfo(listDateInfo);

		List<PersonalConditionsDto> listPersonalConditions = resultShiftMode.dataSpecDateAndHolidayDto.listPersonalConditions.stream().map(i -> {
			return new PersonalConditionsDto(i);
		}).collect(Collectors.toList());
		result.setListPersonalConditions(listPersonalConditions);

		DisplayControlPersonalCondDto displayControlPersonalCond = resultShiftMode.dataSpecDateAndHolidayDto.optDisplayControlPersonalCond.isPresent()
				? new DisplayControlPersonalCondDto(resultShiftMode.dataSpecDateAndHolidayDto.optDisplayControlPersonalCond.get()) : null;
		result.setDisplayControlPersonalCond(displayControlPersonalCond);

		result.setShiftMasterWithWorkStyleLst(resultShiftMode.schedulesbyShiftDataResult.listShiftMaster);
		result.setListWorkScheduleShift(resultShiftMode.schedulesbyShiftDataResult.listWorkScheduleShift);
		return result;
	}
	
	private ChangeMonthDto convertDataTimeShortNameMode(DatePeriod datePeriod,ChangePeriodInWorkInfoResult resultOtherMode ) {
		ChangeMonthDto result = new ChangeMonthDto();

		DataBasicDto dataBasicDto = new DataBasicDto();
		dataBasicDto.setStartDate(datePeriod.start());
		dataBasicDto.setEndDate(datePeriod.end());
		result.setDataBasicDto(dataBasicDto);
		
		List<EmployeeInformationDto> listEmpInfo = resultOtherMode.listEmpInfo.stream().map(item -> {
			return new EmployeeInformationDto(item.getEmployeeId(), item.getEmployeeCode(),
					item.getBusinessName().toString());
		}).collect(Collectors.toList());
		result.setListEmpInfo(listEmpInfo);

		List<DateInformationDto> listDateInfo = resultOtherMode.dataSpecDateAndHolidayDto.listDateInfo.stream().map(i -> {
			return new DateInformationDto(i);
		}).collect(Collectors.toList());
		result.setListDateInfo(listDateInfo);

		List<PersonalConditionsDto> listPersonalConditions = resultOtherMode.dataSpecDateAndHolidayDto.listPersonalConditions.stream().map(i -> {
			return new PersonalConditionsDto(i);
		}).collect(Collectors.toList());
		result.setListPersonalConditions(listPersonalConditions);

		DisplayControlPersonalCondDto displayControlPersonalCond = resultOtherMode.dataSpecDateAndHolidayDto.optDisplayControlPersonalCond.isPresent()
				? new DisplayControlPersonalCondDto(resultOtherMode.dataSpecDateAndHolidayDto.optDisplayControlPersonalCond.get()) : null;
		result.setDisplayControlPersonalCond(displayControlPersonalCond);

		result.setListWorkScheduleWorkInfor(resultOtherMode.listWorkScheduleWorkInfor);
		return result;
	}
}
