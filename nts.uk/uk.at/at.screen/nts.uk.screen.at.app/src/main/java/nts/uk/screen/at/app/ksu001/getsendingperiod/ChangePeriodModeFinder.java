/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getsendingperiod;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.start.ChangePeriodModeParam;
import nts.uk.screen.at.app.ksu001.start.DataBasicDto;

/**
 * @author laitv 
 *
 */
@Stateless
public class ChangePeriodModeFinder {
	
	@Inject
	private ChangePeriodInShift changePeriodInShift;
	@Inject
	private ChangePeriodInWorkInfomation changePeriodInWorkInfo;

	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	public ChangeMonthDto getData(ChangePeriodModeParam param) {

		GeneralDate startDate = GeneralDate.fromString(param.startDate, DATE_FORMAT);
		GeneralDate	endDate   =	GeneralDate.fromString(param.endDate, DATE_FORMAT);

		if (param.viewMode.equals("time") || param.viewMode.equals("shortName")) {
			ChangePeriodInWorkInfoParam param1 = new ChangePeriodInWorkInfoParam(startDate, endDate,
					param.unit, param.workplaceId, param.workplaceGroupId, param.sids, param.getActualData);
			ChangePeriodInWorkInfoResult resultOtherMode = changePeriodInWorkInfo.getData(param1);
			ChangeMonthDto result = convertDataTimeShortNameMode(startDate,endDate, resultOtherMode);
			return result;

		} else if (param.viewMode.equals("shift")) {
			ChangePeriodInShiftParam param2 = new ChangePeriodInShiftParam(startDate, endDate, param.unit,
					param.workplaceId, param.workplaceGroupId, param.sids, param.listShiftMasterNotNeedGetNew,
					param.getActualData);
			ChangePeriodInShiftResult resultShiftMode = changePeriodInShift.getData(param2);
			ChangeMonthDto result = convertDataShiftMode(startDate,endDate, resultShiftMode);
			return result;
		}
		return null;
	}
	
	private ChangeMonthDto convertDataShiftMode(GeneralDate startDate, GeneralDate endDate,ChangePeriodInShiftResult resultShiftMode ) {
		ChangeMonthDto result = new ChangeMonthDto();

		DataBasicDto dataBasicDto = new DataBasicDto();
		dataBasicDto.setStartDate(startDate);
		dataBasicDto.setEndDate(endDate);
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
	
	private ChangeMonthDto convertDataTimeShortNameMode(GeneralDate startDate, GeneralDate endDate,ChangePeriodInWorkInfoResult resultOtherMode ) {
		ChangeMonthDto result = new ChangeMonthDto();

		DataBasicDto dataBasicDto = new DataBasicDto();
		dataBasicDto.setStartDate(startDate);
		dataBasicDto.setEndDate(endDate);
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
