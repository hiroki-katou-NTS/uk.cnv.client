/**
 * 
 */
package nts.uk.screen.at.app.ksu001.changeworkplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShift;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShiftParam;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShiftResult;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInfoParam;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInfoResult;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInformation;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.WorkTypeInfomation;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DataSpecDateAndHolidayDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DisplayControlPersonalCondDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.EventInfoAndPerCondPeriodParam;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.EventInfoAndPersonalConditionsPeriod;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.PersonalConditionsDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.ExtractTargetEmployeesParam;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.ScreenQueryExtractTargetEmployees;
import nts.uk.screen.at.app.ksu001.getshiftpalette.PageInfo;
import nts.uk.screen.at.app.ksu001.getshiftpalette.TargetShiftPalette;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;
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

		ExtractTargetEmployeesParam param2 = new ExtractTargetEmployeesParam(endDate, targetOrgIdenInfor);
		List<EmployeeInformationImport> resultStep2 = extractTargetEmployees.getListEmp(param2);
		//
		
		// call <<ScreenQuery>> 期間中のイベント情報と個人条件を取得する
		List<String> listSid = resultStep2.stream().map(mapper -> mapper.getEmployeeId()).collect(Collectors.toList());
		EventInfoAndPerCondPeriodParam param3 = new EventInfoAndPerCondPeriodParam(startDate, endDate, listSid, targetOrgIdenInfor);
		DataSpecDateAndHolidayDto resultStep3 = eventInfoAndPersonalCondPeriod.getData(param3);
		
		
		// data tra ve cua step4 || step 5.2
		List<WorkTypeInfomation> listWorkTypeInfo = new ArrayList<>();
		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = new ArrayList<>();

		// data tra ve cua step 5.1
		List<PageInfo> listPageInfo = new ArrayList<>();
		TargetShiftPalette targetShiftPalette = null;
		List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst = new ArrayList<>();
		// data cua Grid
		List<ScheduleOfShiftDto> listWorkScheduleShift = new ArrayList<>();
		
		if (param.viewMode.equals("time") || param.viewMode.equals("shortName")) {
			// step 4 || 5.2 start
			DisplayInWorkInfoParam param4 = new DisplayInWorkInfoParam(listSid, startDate, endDate, param.getActualData);
			DisplayInWorkInfoResult  resultStep4 = new DisplayInWorkInfoResult();
			resultStep4 = displayInWorkInfo.getDataWorkInfo(param4);
			listWorkTypeInfo = resultStep4.listWorkTypeInfo;
			listWorkScheduleWorkInfor = resultStep4.listWorkScheduleWorkInfor;
			
		} else if (param.viewMode.equals("shift")) {
			// step 5.1 start
			// step5.1
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

			DisplayInShiftResult resultStep51 = displayInShift.getData(param51);
			listPageInfo = resultStep51.listPageInfo;
			targetShiftPalette = resultStep51.targetShiftPalette;
			shiftMasterWithWorkStyleLst = resultStep51.shiftMasterWithWorkStyleLst;
			listWorkScheduleShift = resultStep51.listWorkScheduleShift;
		}
		
		StartKSU001Dto result = convertData(resultStep2, resultStep3,
				listWorkTypeInfo, listWorkScheduleWorkInfor, 
				listPageInfo,targetShiftPalette,shiftMasterWithWorkStyleLst,listWorkScheduleShift);
		return result;
	}
	
	private StartKSU001Dto convertData(List<EmployeeInformationImport> resultStep2, 
			DataSpecDateAndHolidayDto resultStep3, List<WorkTypeInfomation> listWorkTypeInfo, 
			List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor,
			List<PageInfo> listPageInfo, TargetShiftPalette targetShiftPalette, List<ShiftMasterMapWithWorkStyle> shiftMasterWithWorkStyleLst, List<ScheduleOfShiftDto> listWorkScheduleShift) {
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
		
		//  data tra ve cua step4
		result.setListWorkTypeInfo(listWorkTypeInfo);
		result.setListWorkScheduleWorkInfor(listWorkScheduleWorkInfor);
		// 5.1
		result.setListPageInfo(listPageInfo);
		result.setTargetShiftPalette(targetShiftPalette);
		result.setShiftMasterWithWorkStyleLst(shiftMasterWithWorkStyleLst);
		result.setListWorkScheduleShift(listWorkScheduleShift);
		return result;
	}
}
