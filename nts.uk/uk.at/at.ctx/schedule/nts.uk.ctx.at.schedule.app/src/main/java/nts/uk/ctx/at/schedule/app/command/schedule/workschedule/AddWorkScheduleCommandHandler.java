package nts.uk.ctx.at.schedule.app.command.schedule.workschedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.adapter.classification.SClsHistImported;
import nts.uk.ctx.at.schedule.dom.adapter.classification.SyClassificationAdapter;
import nts.uk.ctx.at.schedule.dom.schedule.createworkschedule.createschedulecommon.correctworkschedule.CorrectWorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskScheduleDetail;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkScheduleRepository;
import nts.uk.ctx.at.shared.dom.adapter.employee.SClsHistImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSyEmploymentImport;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SharedAffJobTitleHisImport;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SharedAffJobtitleHisAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisImport;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.BusinessTypeOfEmployee;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.repository.BusinessTypeEmpService;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisScheduleAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;
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
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 作業予定を登録する
 * 
 * @author HieuLt
 */
@Stateless
public class AddWorkScheduleCommandHandler extends CommandHandler<AddWorkScheduleCommand> {

	@Inject
	private WorkScheduleRepository repo;
	@Inject
	private BasicScheduleService basicScheduleService;
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
	@Inject
	private WorkScheduleRepository workScheduleRepo;
	@Inject
	private CorrectWorkSchedule correctWorkSchedule;
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	@Inject
	private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;
	@Inject
	private SharedAffJobtitleHisAdapter sharedAffJobtitleHisAdapter;
	@Inject
	private SharedAffWorkPlaceHisAdapter sharedAffWorkPlaceHisAdapter;
	@Inject
	private WorkingConditionRepository workingConditionRepo;
	@Inject
	private BusinessTypeEmpService businessTypeEmpService;
	@Inject
	private SyClassificationAdapter syClassificationAdapter;

	@Override
	protected void handle(CommandHandlerContext<AddWorkScheduleCommand> context) {
		AddWorkScheduleCommand command = context.getCommand();
		GeneralDate date = GeneralDate.fromString(command.getYmd(), "yyyy/MM/dd");
		List<TaskScheduleDetailEmp> lst = command.lstTaskScheduleDetailEmp;
		List<String> lstEmt = lst.stream().map(mapper -> mapper.getEmpId()).collect(Collectors.toList()).stream().distinct().collect(Collectors.toList());
		Require require = new Require(basicScheduleService, workTypeRepo, workTimeSettingRepository, fixedWorkSet,
				flowWorkSet, flexWorkSet, predetemineTimeSet, workScheduleRepo, correctWorkSchedule,
				interimRemainDataMngRegisterDateChange, employmentHisScheduleAdapter, sharedAffJobtitleHisAdapter,
				sharedAffWorkPlaceHisAdapter, workingConditionRepo, businessTypeEmpService, syClassificationAdapter);
		List<WorkSchedule> lstWorkSchedule = new ArrayList<WorkSchedule>();
		for (String item : lstEmt) {
			// 1.1:get(社員ID、年月日) : Optional<勤務予定>
			Optional<WorkSchedule> workSchedule = repo.get(item, date);
			// 1.2:not Optional<勤務予定>．isPresent
			if (!workSchedule.isPresent()) {
				throw new BusinessException("Msg_1541");
			} else {
				// 1.3:Optional<勤務予定>.isPresent :$新の作業予定 = 作る(List<作業予定詳細>):
				List<TaskScheduleDetail> details = new ArrayList<>();
				Optional<TaskScheduleDetailEmp> task = lst.stream().filter(pre -> pre.empId.equals(item)).findFirst();
				
				if (task.isPresent()) {
				details = task.get().taskScheduleDetail.stream().map(y -> {
						TaskScheduleDetail detail = TaskScheduleDetailDto.toDomain(y);
						return detail;
					}).collect(Collectors.toList());
				}
				
				TaskSchedule newTaskSchedule = TaskSchedule.create(details);
				// 1.4:Optional<勤務予定>．isPresent : 作業予定を入れ替える(@Require, 作業予定)
				workSchedule.get().updateTaskSchedule(require, newTaskSchedule);
				lstWorkSchedule.add(workSchedule.get());
				// 2
			}
		}
		lstWorkSchedule.forEach(x -> {
			workScheduleRepo.update(x);
		});
	}

	@AllArgsConstructor
	private static class Require implements WorkSchedule.Require {

		@Inject
		private BasicScheduleService basicScheduleService;
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

		//
		@Inject
		private WorkScheduleRepository workScheduleRepo;
		@Inject
		private CorrectWorkSchedule correctWorkSchedule;
		@Inject
		private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;

		//
		@Inject
		private EmploymentHisScheduleAdapter employmentHisScheduleAdapter;
		@Inject
		private SharedAffJobtitleHisAdapter sharedAffJobtitleHisAdapter;
		@Inject
		private SharedAffWorkPlaceHisAdapter sharedAffWorkPlaceHisAdapter;
		@Inject
		private WorkingConditionRepository workingConditionRepo;
		@Inject
		private BusinessTypeEmpService businessTypeEmpService;
		@Inject
		private SyClassificationAdapter syClassificationAdapter;

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			String companyId = AppContexts.user().companyId();
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		// implements WorkInformation.Require
		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			String companyId = AppContexts.user().companyId();
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

		// implements WorkInformation.Require
		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		// implements WorkInformation.Require
		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			String companyId = AppContexts.user().companyId();
			Optional<FixedWorkSetting> workSetting = fixedWorkSet.findByKey(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		// implements WorkInformation.Require
		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			String companyId = AppContexts.user().companyId();
			Optional<FlowWorkSetting> workSetting = flowWorkSet.find(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		// implements WorkInformation.Require
		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			String companyId = AppContexts.user().companyId();
			Optional<FlexWorkSetting> workSetting = flexWorkSet.find(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		// implements WorkInformation.Require
		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			String companyId = AppContexts.user().companyId();
			Optional<PredetemineTimeSetting> workSetting = predetemineTimeSet.findByWorkTimeCode(companyId, wktmCd.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

		// implements AffiliationInforOfDailyAttd.Require
		@Override
		public SharedSyEmploymentImport getAffEmploymentHistory(String employeeId, GeneralDate standardDate) {
			List<EmploymentPeriodImported> listEmpHist = employmentHisScheduleAdapter
					.getEmploymentPeriod(Arrays.asList(employeeId), new DatePeriod(standardDate, standardDate));
			if (listEmpHist.isEmpty())
				return null;
			return new SharedSyEmploymentImport(listEmpHist.get(0).getEmpID(), listEmpHist.get(0).getEmploymentCd(), "",
					listEmpHist.get(0).getDatePeriod());
		}

		// implements AffiliationInforOfDailyAttd.Require
		@Override
		public SharedAffJobTitleHisImport getAffJobTitleHistory(String employeeId, GeneralDate standardDate) {
			List<SharedAffJobTitleHisImport> listAffJobTitleHis = sharedAffJobtitleHisAdapter
					.findAffJobTitleHisByListSid(Arrays.asList(employeeId), standardDate);
			if (listAffJobTitleHis.isEmpty())
				return null;
			return listAffJobTitleHis.get(0);
		}

		// implements AffiliationInforOfDailyAttd.Require
		@Override
		public SharedAffWorkPlaceHisImport getAffWorkplaceHistory(String employeeId, GeneralDate standardDate) {
			Optional<SharedAffWorkPlaceHisImport> rs = sharedAffWorkPlaceHisAdapter.getAffWorkPlaceHis(employeeId,
					standardDate);
			return rs.isPresent() ? rs.get() : null;
		}

		// implements AffiliationInforOfDailyAttd.Require
		@Override
		public SClsHistImport getClassificationHistory(String employeeId, GeneralDate standardDate) {
			String companyId = AppContexts.user().companyId();
			Optional<SClsHistImported> imported = syClassificationAdapter.findSClsHistBySid(companyId, employeeId,
					standardDate);
			if (!imported.isPresent()) {
				return null;
			}
			return new SClsHistImport(imported.get().getPeriod(), imported.get().getEmployeeId(),
					imported.get().getClassificationCode(), imported.get().getClassificationName());
		}

		// implements AffiliationInforOfDailyAttd.Require
		// QA:http://192.168.50.4:3000/issues/113789
		@Override
		public Optional<BusinessTypeOfEmployee> getBusinessType(String employeeId, GeneralDate standardDate) {
			List<BusinessTypeOfEmployee> list = businessTypeEmpService.getData(employeeId, standardDate);
			if (list.isEmpty())
				return Optional.empty();
			return Optional.of(list.get(0));
		}

		// implements AffiliationInforOfDailyAttd.Require
		@Override
		public Optional<WorkingConditionItem> getWorkingConditionHistory(String employeeId, GeneralDate standardDate) {
			String companyId = AppContexts.user().companyId();
			Optional<WorkingConditionItem> rs = workingConditionRepo.getWorkingConditionItemByEmpIDAndDate(companyId,
					standardDate, employeeId);
			return rs;
		}

		// implements EditStateOfDailyAttd.Require
		@Override
		public String getLoginEmployeeId() {
			return AppContexts.user().employeeId();
		}

	}
}
