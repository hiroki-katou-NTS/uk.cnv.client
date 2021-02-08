package nts.uk.screen.at.app.ksus02;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.TimeSpanForCalcDto;
import nts.uk.ctx.at.schedule.app.command.executionlog.ScheduleCreatorExecutionTransaction.WorkInformationImpl;
import nts.uk.ctx.at.schedule.app.query.workrequest.GetWorkRequestByEmpsAndPeriod;
import nts.uk.ctx.at.schedule.app.query.workrequest.WorkAvailabilityDisplayInfoOfOneDayDto;
import nts.uk.ctx.at.schedule.dom.schedule.algorithm.WorkRestTimeZoneDto;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.DeadlineAndPeriodOfWorkAvailability;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.GetShiftTableRuleForOrganizationService;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRule;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRuleForCompany;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRuleForCompanyRepo;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRuleForOrganization;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.ShiftTableRuleForOrganizationRepo;
import nts.uk.ctx.at.schedule.dom.shift.management.shifttable.WorkAvailabilityPeriodUnit;
import nts.uk.ctx.at.schedule.dom.shift.management.workavailability.AssignmentMethod;
import nts.uk.ctx.at.shared.app.find.workrule.shiftmaster.ShiftMasterOrgFinder;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeDto;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.DailyAttendanceItem;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.EmpOrganizationImport;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.GetCombinationrAndWorkHolidayAtrService;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterRepository;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.dto.ShiftMasterDto;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.bs.employee.pub.workplace.export.EmpOrganizationPub;
import nts.uk.ctx.bs.employee.pub.workplace.workplacegroup.EmpOrganizationExport;
import nts.uk.screen.at.app.kdl045.query.WorkAvailabilityDisplayInfoDto;
import nts.uk.screen.at.app.kdl045.query.WorkAvailabilityOfOneDayDto;
import nts.uk.screen.at.app.query.kdp.kdps01.c.ItemDisplayedDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * «ScreenQuery» 初期起動の情報取得
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSUS02_勤務希望入力（スマホ）.勤務希望入力.メニュー別OCD.初期起動の情報取得
 * @author tutk
 *
 */
@Stateless
public class GetInforInitialStartup {
	@Inject
    private EmpOrganizationPub empOrganizationPub;
	
	@Inject
	private ShiftTableRuleForOrganizationRepo shiftTableRuleForOrganizationRepo;

	@Inject
	private ShiftTableRuleForCompanyRepo shiftTableRuleForCompanyRepo;
	
	@Inject
	private GetWorkRequestByEmpsAndPeriod getWorkRequestByEmpsAndPeriod;
	
	@Inject
	private ShiftMasterOrgFinder shiftMasterOrgFinder;
	
	@Inject
	private ShiftMasterRepository shiftMasterRepo;
	@Inject
	private BasicScheduleService service;
	@Inject
	private WorkTypeRepository workTypeRepo;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject
	private FixedWorkSettingRepository fixedWorkSet;

	@Inject
	private FlowWorkSettingRepository flowWorkSet;

	@Inject
	private FlexWorkSettingRepository flexWorkSet;
	
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSet;

	
	public FirstInformationDto get(GeneralDate baseDate) {
		
		String employeeId = AppContexts.user().employeeId();
		GetTargetIdentifiInforService.Require requireGetTargetIdentifiInforService = new RequireGetTargetIdentifiInforServiceImpl(empOrganizationPub);
		//1:
		TargetOrgIdenInfor targetOrgIdenInfor = GetTargetIdentifiInforService.get(requireGetTargetIdentifiInforService, baseDate, employeeId);
		
		GetShiftTableRuleForOrganizationService.Require requireGetShiftTableRule = new RequireGetShiftTableRuleForOrganizationServiceImpl(
				shiftTableRuleForOrganizationRepo, shiftTableRuleForCompanyRepo);
		//2:
		Optional<ShiftTableRule> optShiftTableRule = GetShiftTableRuleForOrganizationService.get(requireGetShiftTableRule, targetOrgIdenInfor);
		//3:
		if(!optShiftTableRule.isPresent()) {
			throw new BusinessException("Msg_2049");
		}
		//4:
		if(optShiftTableRule.get().getUseWorkAvailabilityAtr() == NotUseAtr.NOT_USE) {
			throw new BusinessException("Msg_2052");
		}
		//5:
		WorkAvailabilityPeriodUnit workAvailabilityPeriodUnit = optShiftTableRule.get().getShiftTableSetting().get().getShiftPeriodUnit();
		//6:
		DeadlineAndPeriodOfWorkAvailability deadlineAndPeriodOfWorkAvailability = optShiftTableRule.get().getShiftTableSetting().get().getCorrespondingDeadlineAndPeriod(baseDate);
		List<String> listEmp = new ArrayList<>();
		listEmp.add(employeeId);
		//7:
		List<WorkAvailabilityDisplayInfoOfOneDayDto> listData = getWorkRequestByEmpsAndPeriod.get(listEmp, deadlineAndPeriodOfWorkAvailability.getPeriod());
		List<ShiftMasterDto> listShiftMasterDto = new ArrayList<>(); 
		if(optShiftTableRule.get().getAvailabilityAssignMethodList().get(0) == AssignmentMethod.SHIFT ) {
			//8:
			listShiftMasterDto = shiftMasterOrgFinder.optainShiftMastersByWorkPlace(targetOrgIdenInfor.getTargetId(), targetOrgIdenInfor.getUnit().value);
			listShiftMasterDto.forEach(shiftMaster -> {
				shiftMaster.setWorkTime1(shiftMaster.getWorkTime1().replaceAll("前日", ""));
				shiftMaster.setWorkTime2(shiftMaster.getWorkTime2().replaceAll("前日", ""));
				shiftMaster.setWorkTime1(shiftMaster.getWorkTime1().replaceAll("当日", ""));
				shiftMaster.setWorkTime2(shiftMaster.getWorkTime2().replaceAll("当日", ""));
				shiftMaster.setWorkTime1(shiftMaster.getWorkTime1().replaceAll("翌日", ""));
				shiftMaster.setWorkTime2(shiftMaster.getWorkTime2().replaceAll("翌日", ""));
				shiftMaster.setWorkTime1(shiftMaster.getWorkTime1().replaceAll("翌々日", ""));
				shiftMaster.setWorkTime2(shiftMaster.getWorkTime2().replaceAll("翌々日", ""));
				shiftMaster.setWorkTime1(shiftMaster.getWorkTime1().replaceAll("前々日", ""));
				shiftMaster.setWorkTime2(shiftMaster.getWorkTime2().replaceAll("前々日", ""));
				shiftMaster.setColor("#"+shiftMaster.getColor());
				shiftMaster.setColorSmartphone("#"+shiftMaster.getColorSmartphone());
			});
			listShiftMasterDto = listShiftMasterDto.stream().sorted(Comparator.comparing(ShiftMasterDto::getShiftMasterCode))
			.collect(Collectors.toList());
			//9:
			GetCombinationrAndWorkHolidayAtrService.Require require = new RequireImpl(shiftMasterRepo, service,
					workTypeRepo, workTimeSettingRepository, fixedWorkSet, flowWorkSet, flexWorkSet,
					predetemineTimeSet);
			Map<ShiftMaster,Optional<WorkStyle>> dataMapWorkstyle = GetCombinationrAndWorkHolidayAtrService.getCode(require, employeeId, listShiftMasterDto.stream().map(c->c.getShiftMasterCode()).collect(Collectors.toList()));
			for(ShiftMasterDto shiftMasterDto : listShiftMasterDto) {
				dataMapWorkstyle.forEach((key, value) -> {
					if(key.getShiftMasterCode().v().equals(shiftMasterDto.getShiftMasterCode())) {
						shiftMasterDto.setColorText( value.isPresent()?getColorTextByWorkStyle(value.get()):"");
						return;
					}
				});
			}
		}
		return new FirstInformationDto(
				optShiftTableRule.get().getAvailabilityAssignMethodList().get(0).value, 
				workAvailabilityPeriodUnit.value, 
				deadlineAndPeriodOfWorkAvailability.getDeadline().toString(), 
				deadlineAndPeriodOfWorkAvailability.getPeriod().start().toString(), 
				deadlineAndPeriodOfWorkAvailability.getPeriod().end().toString(),
				listData.stream().map(c-> convertToWorkAvailabilityDisplayInfoOfOneDayDto(c)).collect(Collectors.toList()),
				listShiftMasterDto.stream().map(c->new ShiftMasterAndWorkInfoScheTime(c, null)).collect(Collectors.toList()));
	}
	
	private String getColorTextByWorkStyle(WorkStyle ws) {
		switch (ws) {
		case ONE_DAY_REST:
			return "#FF0000";
		case ONE_DAY_WORK:
			return "#00f";
		default:
			return "#ff7f27";
		}
	}
	
	private WorkAvailabilityOfOneDayDto convertToWorkAvailabilityDisplayInfoOfOneDayDto(WorkAvailabilityDisplayInfoOfOneDayDto dto) {
		return new WorkAvailabilityOfOneDayDto(
				dto.getEmployeeId(), 
				dto.getAvailabilityDate(), 
				dto.getMemo(), 
				new WorkAvailabilityDisplayInfoDto(
						dto.getDisplayInfo().getAssignmentMethod(),
						dto.getDisplayInfo().getNameList(),
						dto.getDisplayInfo().getTimeZoneList().stream().map(c-> new TimeSpanForCalcDto(c.getStartTime(),c.getEndTime())).collect(Collectors.toList())
						));
	}
	
	@AllArgsConstructor
    private class RequireGetTargetIdentifiInforServiceImpl implements GetTargetIdentifiInforService.Require {

        @Inject
        private EmpOrganizationPub empOrganizationPub;

        @Override
        public List<EmpOrganizationImport> getEmpOrganization(GeneralDate referenceDate, List<String> listEmpId) {

            List<EmpOrganizationExport> exports = empOrganizationPub.getEmpOrganiztion(referenceDate, listEmpId);
            List<EmpOrganizationImport> data = exports.stream().map(i -> {
                return new EmpOrganizationImport(new EmployeeId(i.getEmpId()), i.getBusinessName(), i.getEmpCd(), i.getWorkplaceId(), i.getWorkplaceGroupId());
            }).collect(Collectors.toList());
            return data;
        }

    }
	
	@AllArgsConstructor
	private static class RequireGetShiftTableRuleForOrganizationServiceImpl implements GetShiftTableRuleForOrganizationService.Require {
		private final String companyId = AppContexts.user().companyId();

		@Inject
		private ShiftTableRuleForOrganizationRepo shiftTableRuleForOrganizationRepo;

		@Inject
		private ShiftTableRuleForCompanyRepo shiftTableRuleForCompanyRepo;

		@Override
		public Optional<ShiftTableRuleForOrganization> getOrganizationShiftTable(TargetOrgIdenInfor targetOrg) {
			Optional<ShiftTableRuleForOrganization> data = shiftTableRuleForOrganizationRepo.get(companyId, targetOrg);
			return data;
		}

		@Override
		public Optional<ShiftTableRuleForCompany> getCompanyShiftTable() {
			Optional<ShiftTableRuleForCompany> data = shiftTableRuleForCompanyRepo.get(companyId);
			return data;
		}
	}
	
	
	@AllArgsConstructor
	private static class RequireImpl implements GetCombinationrAndWorkHolidayAtrService.Require {

		private final String companyId = AppContexts.user().companyId();

		@Inject
		private ShiftMasterRepository shiftMasterRepo;
		@Inject
		private BasicScheduleService service;
		@Inject
		private WorkTypeRepository workTypeRepo;
		@Inject
		private WorkTimeSettingRepository workTimeSettingRepository;
		
		@Inject
		private FixedWorkSettingRepository fixedWorkSet;

		@Inject
		private FlowWorkSettingRepository flowWorkSet;

		@Inject
		private FlexWorkSettingRepository flexWorkSet;
		
		@Inject
		private PredetemineTimeSettingRepository predetemineTimeSet;

		@Override
		public List<ShiftMaster> getByListEmp(String companyID, List<String> lstShiftMasterCd) {
			List<ShiftMaster> data = shiftMasterRepo.getByListShiftMaterCd2(companyId, lstShiftMasterCd);
			return data;
		}

		@Override
		public List<ShiftMaster> getByListWorkInfo(String companyId, List<WorkInformation> lstWorkInformation) {
			List<ShiftMaster> data = shiftMasterRepo.get(companyId, lstWorkInformation);
			return data;
		}

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			 return service.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			Optional<FixedWorkSetting> workSetting = fixedWorkSet.findByKey(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			Optional<FlowWorkSetting> workSetting = flowWorkSet.find(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			Optional<FlexWorkSetting> workSetting = flexWorkSet.find(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			Optional<PredetemineTimeSetting> workSetting = predetemineTimeSet.findByWorkTimeCode(companyId, wktmCd.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

	}
}
