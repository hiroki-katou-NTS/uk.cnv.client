/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getsendingperiod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.gul.text.StringUtil;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.screen.at.app.ksu001.start.AggregatePersonalMapDto;
import nts.uk.screen.at.app.ksu001.start.AggregateWorkplaceMapDto;
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
	private static final String TIME = "time";
	private static final String SHORTNAME = "shortName";
	private static final String SHIFT = "shift";
	
	public ChangeMonthDto getData(ChangePeriodModeParam param) {
		GeneralDate startDate = GeneralDate.fromString(param.startDate, DATE_FORMAT);
		GeneralDate	endDate   =	GeneralDate.fromString(param.endDate, DATE_FORMAT);
		ChangeMonthDto result = new ChangeMonthDto();
		if (param.viewMode.equals(TIME) || param.viewMode.equals(SHORTNAME)) {
			// call <<ScreenQuery>> 表示期間を変更する（勤務情報）
			ChangePeriodInWorkInfoParam param1 = new ChangePeriodInWorkInfoParam(startDate, endDate,
					param.unit, param.workplaceId, param.workplaceGroupId, param.sids, param.getActualData,
					StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? null : Integer.valueOf(param.personTotalSelected),
					StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? null : Integer.valueOf(param.workplaceSelected), new DateInMonth(param.day, param.isLastDay));
			ChangePeriodInWorkInfoResult resultOtherMode = changePeriodInWorkInfo.getData(param1);
			result = convertDataTimeShortNameMode(startDate,endDate, resultOtherMode);

		} else if (param.viewMode.equals(SHIFT)) {
			// call <<ScreenQuery>> 表示期間を変更する（シフト）
			ChangePeriodInShiftParam param2 = new ChangePeriodInShiftParam(startDate, endDate, param.unit,
					param.workplaceId, param.workplaceGroupId, param.sids, param.listShiftMasterNotNeedGetNew,param.getActualData,
					StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? null : Integer.valueOf(param.personTotalSelected),
					StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? null : Integer.valueOf(param.workplaceSelected), new DateInMonth(param.day, param.isLastDay));
			ChangePeriodInShiftResult resultShiftMode = changePeriodInShift.getData(param2);;
			result = convertDataShiftMode(startDate,endDate, resultShiftMode);
		}
		result.dataBasicDto.startDate = startDate;
		result.dataBasicDto.endDate = endDate;
		return result;
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

		List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst = new ArrayList<>();
		Map<ShiftMasterDto, Integer> mapShiftMasterWithWorkStyle = resultShiftMode.mapShiftMasterWithWorkStyle;
		if(!mapShiftMasterWithWorkStyle.isEmpty()){
			mapShiftMasterWithWorkStyle.forEach((key, value) -> {
				shiftMasterWithWorkStyleLst.add(new ShiftMasterMapWithWorkStyle(key, value == null ? null : String.valueOf(value)));
			});
		}
		result.setShiftMasterWithWorkStyleLst(shiftMasterWithWorkStyleLst);
		result.setListWorkScheduleShift(resultShiftMode.listWorkScheduleShift);
		result.setAggreratePersonal(resultShiftMode.aggreratePersonal == null ? null : AggregatePersonalMapDto.convertMap(resultShiftMode.aggreratePersonal));
		result.setAggrerateWorkplace(resultShiftMode.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(resultShiftMode.aggrerateWorkplace));
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
		
		result.setAggreratePersonal(resultOtherMode.aggreratePersonal == null ? null : AggregatePersonalMapDto.convertMap(resultOtherMode.aggreratePersonal));
		result.setAggrerateWorkplace(resultOtherMode.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(resultOtherMode.aggrerateWorkplace));
		
		return result;
	}
}
