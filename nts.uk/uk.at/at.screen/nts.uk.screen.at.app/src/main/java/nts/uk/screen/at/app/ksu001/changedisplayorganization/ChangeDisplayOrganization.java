package nts.uk.screen.at.app.ksu001.changedisplayorganization;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShift;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShiftParam;
import nts.uk.screen.at.app.ksu001.displayinshift.DisplayInShiftResult;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DataSpecDateAndHolidayDto;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.EventInfoAndPerCondPeriodParam;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.EventInfoAndPersonalConditionsPeriod;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.EmployeeInformationDto;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.ExtractTargetEmployeesParam;
import nts.uk.screen.at.app.ksu001.extracttargetemployees.ScreenQueryExtractTargetEmployees;

/**
 * 表示組織を変更する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).A：個人スケジュール修正(職場別).メニュー別OCD
 * @author hoangnd
 *
 */
@Stateless
public class ChangeDisplayOrganization {

	@Inject
	private ScreenQueryExtractTargetEmployees screenQueryExtractTargetEmployees;
	
	@Inject
	private EventInfoAndPersonalConditionsPeriod eventInfoAndPersonalConditionsPeriod;
	
	@Inject
	private DisplayInShift displayInShift;
	
	public ChangeDisplayOrganizationDto get(ChangeDisplayOrganizationParam param) {
		
		ChangeDisplayOrganizationDto output = new ChangeDisplayOrganizationDto();
		TargetOrgIdenInfor targetOrgIdenInfor;
		if (param.unit == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrgIdenInfor = new TargetOrgIdenInfor(TargetOrganizationUnit.WORKPLACE,
					Optional.of(param.workplaceId),
					Optional.empty());
		} else {
			targetOrgIdenInfor = new TargetOrgIdenInfor(
					TargetOrganizationUnit.WORKPLACE_GROUP,
					Optional.empty(),
					Optional.of(param.workplaceGroupId));
		}
		// 抽出する(年月日, 対象組織識別情報)
		List<EmployeeInformationImport> employeeInformationImports = 
				screenQueryExtractTargetEmployees.getListEmp(new ExtractTargetEmployeesParam(GeneralDate.today(), new DatePeriod(param.getStartDate(), param.getEndDate()), targetOrgIdenInfor));
		output.setEmployeeInformationDtos(
				employeeInformationImports.stream()
										  .map(x -> new EmployeeInformationDto(
												  x.getEmployeeId(),
												  x.getEmployeeCode(),
												  x.getBusinessName()
												  ))
										  .collect(Collectors.toList())
										  
				);
		
		// 取得する(期間, 対象組織識別情報, List<社員ID>)
		DataSpecDateAndHolidayDto dataSpecDateAndHolidayDto =
				eventInfoAndPersonalConditionsPeriod.getData(
						new EventInfoAndPerCondPeriodParam(
								param.getStartDate(),
								param.getEndDate(),
								param.getSids(),
								targetOrgIdenInfor
								)
						
				);
		
		output.setDataSpecDateAndHolidayDto(dataSpecDateAndHolidayDto);
		// Ab:勤務表示、Ac:略名表示の場合
		if (param.mode == ChangeDisplayOrganizationParam.WORK_MODE 
				|| param.mode == ChangeDisplayOrganizationParam.ABBREVIATION_MODE) {
			DisplayInShiftResult displayInShiftResult_New = 
				displayInShift.getData(new DisplayInShiftParam(
							param.getSids(),
							param.getStartDate(),
							param.getEndDate(),
							param.getWorkplaceId(),
							param.getWorkplaceGroupId(),
							param.getListShiftMasterNotNeedGetNew(),
							param.getShiftPaletteWantGet(),
							param.getActualData,
							param.getUnit(),
							param.getPersonalCounterOp(),
							param.getWorkplaceCounterOp(),
							param.getDay()
						));
			
			output.setListWorkScheduleShift(displayInShiftResult_New.getListWorkScheduleShift());
			output.setMapShiftMasterWithWorkStyle(displayInShiftResult_New.getMapShiftMasterWithWorkStyle());
			output.setAggreratePersonal(displayInShiftResult_New.getAggreratePersonal());
			output.setAggrerateWorkplace(displayInShiftResult_New.getAggrerateWorkplace());
		}
		
		return output;
		
		
	}
}
