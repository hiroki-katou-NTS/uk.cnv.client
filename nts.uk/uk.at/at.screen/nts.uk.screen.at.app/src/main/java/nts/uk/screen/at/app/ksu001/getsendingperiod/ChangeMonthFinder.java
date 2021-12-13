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
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.StringUtil;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.screen.at.app.ksu001.start.AggregatePersonalMapDto;
import nts.uk.screen.at.app.ksu001.start.AggregateWorkplaceMapDto;
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
		
		// call <<ScreenQuery>> 送る期間を取得する
		DatePeriod datePeriod = new DatePeriod(GeneralDate.fromString(param.startDate, DATE_FORMAT),
				GeneralDate.fromString(param.endDate, DATE_FORMAT));
		if (param.modePeriod == ModePeriod.extractionPeriod.value || param.modePeriod == ModePeriod.mode28Days.value) {
			datePeriod = getSendingPeriodScreenQuery.getSendingPeriod(datePeriod, param.isNextMonth, param.cycle28Day);

		} else if (param.modePeriod == ModePeriod.from1stToLastDay.value) {
			GeneralDate startDate = GeneralDate.fromString(param.startDate, DATE_FORMAT);
			if(param.isNextMonth) {
				startDate = startDate.addMonths(1);
    		} else {
    			startDate = startDate.addMonths(-1);
    		}
			YearMonth yearMonth = YearMonth.of(startDate.year(), startDate.month());
			datePeriod = DatePeriod.daysFirstToLastIn(yearMonth);
		}
		
		// call <<ScreenQuery>> 表示期間を変更する（勤務情報）
		if (param.viewMode.equals("time") || param.viewMode.equals("shortName")) {
			ChangePeriodInWorkInfoParam param1 = new ChangePeriodInWorkInfoParam(datePeriod.start(), datePeriod.end(),
					param.unit, param.workplaceId, param.workplaceGroupId, param.sids, param.getActualData,
					StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? null : Integer.valueOf(param.personTotalSelected),
					StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? null : Integer.valueOf(param.workplaceSelected), new DateInMonth(param.day, param.isLastDay));
			ChangePeriodInWorkInfoResult resultOtherMode = changePeriodInWorkInfo.getData(param1);
			ChangeMonthDto result = convertDataTimeShortNameMode(datePeriod, resultOtherMode);
			return result;

		} else if (param.viewMode.equals("shift")) {
			// call <<ScreenQuery>> 表示期間を変更する（シフト）
			ChangePeriodInShiftParam param2 = new ChangePeriodInShiftParam(datePeriod.start(), datePeriod.end(),
					param.unit, param.workplaceId, param.workplaceGroupId, param.sids,
					param.listShiftMasterNotNeedGetNew, param.getActualData,
					StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? null : Integer.valueOf(param.personTotalSelected),
					StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? null : Integer.valueOf(param.workplaceSelected),
				    new DateInMonth(param.day, param.isLastDay));
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
		
		result.setAggreratePersonal(resultOtherMode.aggreratePersonal == null ? null : AggregatePersonalMapDto.convertMap(resultOtherMode.aggreratePersonal));
		result.setAggrerateWorkplace(resultOtherMode.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(resultOtherMode.aggrerateWorkplace));
		return result;
	}
}
