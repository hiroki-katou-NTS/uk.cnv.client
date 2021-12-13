/**
 * 
 */
package nts.uk.screen.at.app.ksu001.changeworkplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DateInMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShift;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShiftParam;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShiftResult;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInfoParam_New;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInfoResult;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInformation;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DataSpecDateAndHolidayDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.EventInfoAndPerCondPeriodParam;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.EventInfoAndPersonalConditionsPeriod;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.ExtractTargetEmployeesParam;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.ScreenQueryExtractTargetEmployees;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.TargetOrgIdenInforDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;
import nts.uk.screen.at.app.ksu001.start.AggregatePersonalMapDto;
import nts.uk.screen.at.app.ksu001.start.AggregateWorkplaceMapDto;
import nts.uk.screen.at.app.ksu001.start.ShiftPaletteWantGet;
import nts.uk.screen.at.app.ksu001.start.StartKSU001Dto;

/**
 * @author laitv 初期起動 
 *
 */
@Stateless
public class ChangeWorkPlaceFinder {

	@Inject
	private ScreenQueryExtractTargetEmployees extractTargetEmployees;
	@Inject
	private EventInfoAndPersonalConditionsPeriod eventInfoAndPersonalCondPeriod;
	@Inject
	private DisplayInWorkInformation displayInWorkInfo;
	@Inject
	private DisplayInShift displayInShift;
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	public StartKSU001Dto getData(ChangeWorkPlaceParam param) {
		
		// call <<ScreenQuery>> 対象社員を抽出する
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		if (param.unit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE,
					Optional.of(param.wkpId),
					Optional.empty());
		}else{
			targetOrgIdenInfor = new TargetOrgIdenInfor(
					TargetOrganizationUnit.WORKPLACE_GROUP,
					Optional.empty(),
					Optional.of(param.wkpId));
		}
		
		GeneralDate startDate = GeneralDate.fromString(param.startDate, DATE_FORMAT);
		GeneralDate endDate   = GeneralDate.fromString(param.endDate, DATE_FORMAT);
		
		ExtractTargetEmployeesParam param2 = new ExtractTargetEmployeesParam(GeneralDate.today(), new DatePeriod(startDate, endDate), targetOrgIdenInfor);
		List<EmployeeInformationImport> resultStep2 = extractTargetEmployees.getListEmp(param2);
		//
		
		// call <<ScreenQuery>> 期間中のイベント情報と個人条件を取得する
		List<String> listSid = resultStep2.stream().map(mapper -> mapper.getEmployeeId()).collect(Collectors.toList());
		EventInfoAndPerCondPeriodParam param3 = new EventInfoAndPerCondPeriodParam(startDate, endDate, listSid, targetOrgIdenInfor);
		DataSpecDateAndHolidayDto resultStep3 = eventInfoAndPersonalCondPeriod.getData(param3);
		
		
		// data tra ve cua mode workInfo or abName
		DisplayInWorkInfoResult  resultStep4 = new DisplayInWorkInfoResult();

		// data tra ve cua mode shift
		DisplayInShiftResult resultStep51 = new DisplayInShiftResult();
		
		if (param.viewMode.equals("time") || param.viewMode.equals("shortName")) {
			// <<ScreenQuery>> 勤務情報で表示する
			TargetOrgIdenInforDto targetOrgIdenInforDto = new TargetOrgIdenInforDto(targetOrgIdenInfor);
			DateInMonth closeDate =  new DateInMonth(param.day, param.isLastDay);
			DisplayInWorkInfoParam_New param4 = new DisplayInWorkInfoParam_New(listSid, startDate, endDate,
					param.getActualData, closeDate, targetOrgIdenInforDto,
					StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? null : Integer.valueOf(param.personTotalSelected),
					StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? null : Integer.valueOf(param.workplaceSelected));
			resultStep4 = displayInWorkInfo.getDataWorkInfo(param4);
			
		} else if (param.viewMode.equals("shift")) {
			// <<ScreenQuery>> シフトで表示する
			DisplayInShiftParam param51 = new DisplayInShiftParam();
			param51.setListSid(listSid);
			param51.setStartDate(startDate);
			param51.setEndDate(endDate);
			param51.setWorkplaceId(param.wkpId);
			param51.setWorkplaceGroupId(param.wkpId);
			param51.setListShiftMasterNotNeedGetNew(new ArrayList<>());
			param51.setShiftPaletteWantGet(new ShiftPaletteWantGet(param.shiftPalletUnit, param.pageNumberCom, param.pageNumberOrg));
			param51.setGetActualData(param.getActualData);
			param51.setUnit(param.unit);
			
			param51.setPersonalCounterOp(StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? null : Integer.valueOf(param.personTotalSelected));
			param51.setWorkplaceCounterOp(StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? null : Integer.valueOf(param.workplaceSelected));
			param51.setDay(new DateInMonth(param.day, param.isLastDay));

			resultStep51 = displayInShift.getData(param51);
		}
		
		StartKSU001Dto result = convertData(param.viewMode, resultStep2, resultStep3, resultStep4, resultStep51);
		return result;
	}
	
	private StartKSU001Dto convertData(String viewMode,
			List<EmployeeInformationImport> resultStep2, 
			DataSpecDateAndHolidayDto resultStep3, 
			DisplayInWorkInfoResult  resultStep4,
			DisplayInShiftResult resultStep51) {
		StartKSU001Dto result = new StartKSU001Dto();
		
		//  data tra ve cua step2
		List<EmployeeInformationDto> listEmpInfo = resultStep2.stream().map(item -> {
			return new EmployeeInformationDto(item.getEmployeeId(), item.getEmployeeCode(), item.getBusinessName().toString());
		}).collect(Collectors.toList());
		result.setListEmpInfo(listEmpInfo);
		
		//  data tra ve cua step3
		List<DateInformationDto> listDateInfo = resultStep3.getListDateInfo().stream().map(i -> {
			return new DateInformationDto(i);
		}).collect(Collectors.toList());
		result.setListDateInfo(listDateInfo);
		
		List<PersonalConditionsDto> listPersonalConditions = resultStep3.getListPersonalConditions().stream().map(i -> {
			return new PersonalConditionsDto(i);
		}).collect(Collectors.toList());
		result.setListPersonalConditions(listPersonalConditions);
		
		DisplayControlPersonalCondDto displayControlPersonalCond = resultStep3.optDisplayControlPersonalCond.isPresent()
				? new DisplayControlPersonalCondDto(resultStep3.optDisplayControlPersonalCond.get()) : null;
		result.setDisplayControlPersonalCond(displayControlPersonalCond);
		
		//  data tra ve cua mode workInfo | ABName
		result.setListWorkTypeInfo(resultStep4.listWorkTypeInfo);
		result.setListWorkScheduleWorkInfor(resultStep4.workScheduleWorkInforDtos);
		
		// data tra ve cua mode shift
		List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst = new ArrayList<>();
		result.setListPageInfo(resultStep51.listPageInfo);
		result.setTargetShiftPalette(resultStep51.targetShiftPalette);
		result.setListWorkScheduleShift(resultStep51.listWorkScheduleShift);
		Map<ShiftMasterDto, Integer> mapShiftMasterWithWorkStyle = resultStep51.mapShiftMasterWithWorkStyle;
		if(!mapShiftMasterWithWorkStyle.isEmpty()){
			mapShiftMasterWithWorkStyle.forEach((key, value) -> {
				shiftMasterWithWorkStyleLst.add(new ShiftMasterMapWithWorkStyle(key, value == null ? null : String.valueOf(value)));
			});
		}
		result.setShiftMasterWithWorkStyleLst(shiftMasterWithWorkStyleLst);
		
		if (viewMode.equals("shift")) {
			result.setAggreratePersonal(resultStep51.aggreratePersonal == null ? null : AggregatePersonalMapDto.convertMap(resultStep51.aggreratePersonal));
			result.setAggrerateWorkplace(resultStep51.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(resultStep51.aggrerateWorkplace));
		} else {
			result.setAggreratePersonal(resultStep4.aggreratePersonal == null ? null : AggregatePersonalMapDto.convertMap(resultStep4.aggreratePersonal));
			result.setAggrerateWorkplace(resultStep4.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(resultStep4.aggrerateWorkplace));
		}
		return result;
	}
}
