/**
 * 
 */
package nts.uk.screen.at.app.ksu001.start;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
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
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.DataScreenQueryGetInforDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.FuncCtrlDisplayFormatDto;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.ScreenQueryGetInforOfInitStartup;
import nts.uk.screen.at.app.ksu001.getinfoofInitstartup.TargetOrgIdenInforDto;
import nts.uk.screen.at.app.ksu001.getshiftpalette.ShiftMasterDto;

/**
 * @author laitv 初期起動 
 * path: UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正（職場別）.メニュー別OCD.初期起動
 */
@Stateless
public class StartKSU001Ver5 {

	@Inject
	private ScreenQueryGetInforOfInitStartup getInforOfInitStartup;
	@Inject
	private ScreenQueryExtractTargetEmployees extractTargetEmployees;
	@Inject
	private EventInfoAndPersonalConditionsPeriod eventInfoAndPersonalCondPeriod;
	@Inject
	private DisplayInWorkInformation displayInWorkInfo;
	@Inject
	private DisplayInShift displayInShift;
	
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	
	public StartKSU001Dto getData(StartKSU001Param param) {
		
		// step 1
		DataScreenQueryGetInforDto resultStep1 = getInforOfInitStartup.getData();
		
		// step 2 start
		GeneralDate startDate = StringUtil.isNullOrEmpty(param.startDate, true) ? resultStep1.startDate : GeneralDate.fromString(param.startDate, DATE_FORMAT);
		GeneralDate endDate   = StringUtil.isNullOrEmpty(param.endDate, true) ? resultStep1.endDate : GeneralDate.fromString(param.endDate, DATE_FORMAT);
		resultStep1.setStartDate(startDate);
		resultStep1.setEndDate(endDate);
		
		int unit = 0;
		String workplaceId = "";
		String workplaceGroupId = "";
		if (StringUtil.isNullOrEmpty(param.workplaceId, true) && StringUtil.isNullOrEmpty(param.workplaceGroupId, true)) {
			unit = resultStep1.targetOrgIdenInfor.unit;
			workplaceId = resultStep1.targetOrgIdenInfor.workplaceId;
			workplaceGroupId = resultStep1.targetOrgIdenInfor.workplaceGroupId;
		} else {
			unit = param.unit;
			workplaceId = param.workplaceId;
			workplaceGroupId = param.workplaceGroupId ;
		}
		
		TargetOrgIdenInfor targetOrgIdenInfor = null;
		if (unit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE,Optional.of(workplaceId),Optional.empty());
		} else {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE_GROUP, Optional.empty(),Optional.of(workplaceGroupId));
		}

		ExtractTargetEmployeesParam param2 = new ExtractTargetEmployeesParam(GeneralDate.today(), new DatePeriod(startDate, endDate), targetOrgIdenInfor);
		List<EmployeeInformationImport> resultStep2 = extractTargetEmployees.getListEmp(param2);
		// step 2 end
		
		// step 3 start
		List<String> listSid = resultStep2.stream().map(mapper -> mapper.getEmployeeId()).collect(Collectors.toList());
		EventInfoAndPerCondPeriodParam param3 = new EventInfoAndPerCondPeriodParam(startDate, endDate,listSid, targetOrgIdenInfor);
		DataSpecDateAndHolidayDto resultStep3 = eventInfoAndPersonalCondPeriod.getData(param3);
		// step 3 end
		
		// data tra ve cua step4 || step 5.2
		DisplayInWorkInfoResult  resultStep4 = new DisplayInWorkInfoResult();

		// data tra ve cua step 5.1
		DisplayInShiftResult resultStep51 = new DisplayInShiftResult();
		
		// tính toán để xem gọi viewMode nào
		Integer viewModeSelected = calculateViewModeSelected(param.viewMode, resultStep1.scheFunctionCtrlByWorkplace.useDisplayFormat);
		
		// lấy data Grid, shiftPallet, Worktype
		if (viewModeSelected == FuncCtrlDisplayFormatDto.WorkInfo.value || viewModeSelected == FuncCtrlDisplayFormatDto.AbbreviatedName.value) {
			// step 4 || 5.2 start
			TargetOrgIdenInforDto targetOrgIdenInforDto = new TargetOrgIdenInforDto(targetOrgIdenInfor);
			DisplayInWorkInfoParam_New param4 = new DisplayInWorkInfoParam_New(
					listSid, startDate, endDate,
					param.getActualData, resultStep1.closeDate, targetOrgIdenInforDto,
					StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? (resultStep1.useCategoriesPersonal.isEmpty() ? null : resultStep1.useCategoriesPersonal.get(0).getValue()) : Integer.valueOf(param.personTotalSelected),
					StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? (resultStep1.useCategoriesWorkplace.isEmpty() ? null : resultStep1.useCategoriesWorkplace.get(0).getValue()) : Integer.valueOf(param.workplaceSelected));
			resultStep4 = displayInWorkInfo.getDataWorkInfo(param4);
			
		} else if (viewModeSelected == FuncCtrlDisplayFormatDto.Shift.value) {
			// step 5.1 start
			DisplayInShiftParam param51 = new DisplayInShiftParam();
			param51.setListSid(listSid);
			param51.setStartDate(startDate);
			param51.setEndDate(endDate);
			param51.setWorkplaceId(resultStep1.targetOrgIdenInfor.workplaceId);
			param51.setWorkplaceGroupId(resultStep1.targetOrgIdenInfor.workplaceGroupId);
			param51.setListShiftMasterNotNeedGetNew(new ArrayList<>());
			param51.setShiftPaletteWantGet(new ShiftPaletteWantGet(param.shiftPalletUnit, param.pageNumberCom, param.pageNumberOrg));
			param51.setGetActualData(param.getActualData);
			param51.setUnit(resultStep1.targetOrgIdenInfor.unit);
			
			param51.setPersonalCounterOp(StringUtil.isNullOrEmpty(param.personTotalSelected, true) ? (resultStep1.useCategoriesPersonal.isEmpty() ? null : resultStep1.useCategoriesPersonal.get(0).getValue()) : Integer.valueOf(param.personTotalSelected));
			param51.setWorkplaceCounterOp(StringUtil.isNullOrEmpty(param.workplaceSelected, true) ? (resultStep1.useCategoriesWorkplace.isEmpty() ? null : resultStep1.useCategoriesWorkplace.get(0).getValue()) : Integer.valueOf(param.workplaceSelected));
			param51.setDay(resultStep1.closeDate);

			resultStep51 = displayInShift.getData(param51);
		}
		
		StartKSU001Dto result = convertData(resultStep1, resultStep2, resultStep3, resultStep4, resultStep51, viewModeSelected);
		return result;
	}
	
	private Integer calculateViewModeSelected(String viewModefromUI, List<Integer> useDisplayFormat) {
		/** 略名 AbbreviatedName(0) */
		/** 勤務 WorkInfo(1) */
		/** シフト Shift(2) */
		if (useDisplayFormat.isEmpty())
			throw new BusinessException("List 使用する表示形式(useDisplayFormat) empty");

		Integer vMode = viewModefromUI.equals("time") ? 1 : (viewModefromUI.equals("shortName") ? 0 : 2);
		if (useDisplayFormat.contains(vMode)) {
			return vMode;
		} else {
			if (useDisplayFormat.contains(FuncCtrlDisplayFormatDto.WorkInfo.value)) {
				return FuncCtrlDisplayFormatDto.WorkInfo.value;
			} else if (useDisplayFormat.contains(FuncCtrlDisplayFormatDto.AbbreviatedName.value)) {
				return FuncCtrlDisplayFormatDto.AbbreviatedName.value;
			} else if (useDisplayFormat.contains(FuncCtrlDisplayFormatDto.Shift.value)) {
				return FuncCtrlDisplayFormatDto.Shift.value;
			}
		}
		return FuncCtrlDisplayFormatDto.WorkInfo.value;
	}
	
	private StartKSU001Dto convertData(DataScreenQueryGetInforDto resultStep1,
			List<EmployeeInformationImport> resultStep2, 
			DataSpecDateAndHolidayDto resultStep3, 
			DisplayInWorkInfoResult  resultStep4,
			DisplayInShiftResult resultStep51, Integer viewModeSelected) {
		StartKSU001Dto result = new StartKSU001Dto();
		
		//	data tra ve cua step1	
		DataBasicDto dataBasicDto = new DataBasicDto(resultStep1);
		dataBasicDto.setViewMode(viewModeSelected);
		result.setDataBasicDto(dataBasicDto);
		
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
		result.setListWorkTypeInfo(resultStep4.listWorkTypeInfo);
		result.setListWorkScheduleWorkInfor(resultStep4.workScheduleWorkInforDtos);
		
		// data tra ve cua 5.1
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
		if (viewModeSelected == FuncCtrlDisplayFormatDto.Shift.value) {
			result.setAggreratePersonal(resultStep51.aggreratePersonal == null ? null : AggregatePersonalMapDto.convertMap(resultStep51.aggreratePersonal));
			result.setAggrerateWorkplace(resultStep51.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(resultStep51.aggrerateWorkplace));
		} else {
			result.setAggreratePersonal(resultStep4.aggreratePersonal == null ?  null : AggregatePersonalMapDto.convertMap(resultStep4.aggreratePersonal));
			result.setAggrerateWorkplace(resultStep4.aggrerateWorkplace == null ? null : AggregateWorkplaceMapDto.convertMap(resultStep4.aggrerateWorkplace));
		}
		return result;
	}
}
