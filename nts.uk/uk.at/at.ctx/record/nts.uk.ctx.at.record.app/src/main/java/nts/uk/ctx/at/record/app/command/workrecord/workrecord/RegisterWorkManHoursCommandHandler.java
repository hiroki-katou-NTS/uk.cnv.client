package nts.uk.ctx.at.record.app.command.workrecord.workrecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.daily.ouen.ManHourInputResult;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.RegisterWorkHoursService;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.CalculateDailyRecordServiceCenter;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskmaster.TaskingRepository;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別時間帯別実績.App.作業工数を登録する.応援作業別勤怠時間帯を登録する
 * 
 * @author tutt
 *
 */
@Stateless
public class RegisterWorkManHoursCommandHandler
		extends CommandHandlerWithResult<AddAttendanceTimeZoneCommand, List<IntegrationOfDaily>> {

	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenWorkTimeSheetOfDailyRepo;

	@Inject
	private OuenWorkTimeOfDailyRepo ouenWorkTimeOfDailyRepo;

	@Inject
	private EditStateOfDailyPerformanceRepository editStateOfDailyPerformanceRepo;

	@Inject
	private SyWorkplaceAdapter syWorkplaceAdapter;

	@Inject
	private TaskingRepository taskingRepo;

	@Inject
	private TaskFrameUsageSettingRepository taskFrameUsageSettingRepo;
	
	@Inject
	private CalculateDailyRecordServiceCenter calculate;
	
	@Inject
	private IntegrationOfDailyGetter getter;
	
	@Inject
	private EmployeeDailyPerErrorRepository empDailyPerErrorRepo;

	@Override
	protected List<IntegrationOfDaily> handle(CommandHandlerContext<AddAttendanceTimeZoneCommand> context) {
		List<IntegrationOfDaily> result = new ArrayList<>();
		AddAttendanceTimeZoneCommand command = context.getCommand();

		// loop 年月日 in List<年月日,List<作業詳細>>
		List<WorkDetail> workDetails = command.getWorkDetails();

		// 1: *登録する(対象者,年月日,編集状態,List<作業詳細>): 工数入力結果
		RequireImpl require = new RequireImpl(ouenWorkTimeSheetOfDailyRepo, ouenWorkTimeOfDailyRepo,
				editStateOfDailyPerformanceRepo, syWorkplaceAdapter, taskingRepo, taskFrameUsageSettingRepo, getter, empDailyPerErrorRepo);
		
		if (workDetails == null || workDetails.isEmpty()) {
			return result;
		}
		
		for (WorkDetail w : workDetails) {
			ManHourInputResult manHourInputResult = RegisterWorkHoursService.register(require, AppContexts.user().companyId(), command.getEmployeeId(),
					w.getDate(), command.getEditStateSetting(), w.getLstWorkDetailsParam());
			
			
			
			if (manHourInputResult.getIntegrationOfDaily().isPresent()) {
				result.add(manHourInputResult.getIntegrationOfDaily().get());
			}
			transaction.execute(() -> {
				// 2:persist
				manHourInputResult.getAtomTask().run();
			});
			
		}
		
		return result;
	}

	@AllArgsConstructor
	public class RequireImpl implements RegisterWorkHoursService.Require {

		private OuenWorkTimeSheetOfDailyRepo ouenWorkTimeSheetOfDailyRepo;

		private OuenWorkTimeOfDailyRepo ouenWorkTimeOfDailyRepo;

		private EditStateOfDailyPerformanceRepository editStateOfDailyPerformanceRepo;

		private SyWorkplaceAdapter syWorkplaceAdapter;

		private TaskingRepository taskingRepo;

		private TaskFrameUsageSettingRepository taskFrameUsageSettingRepo;
		
		private IntegrationOfDailyGetter getter;
		
		private EmployeeDailyPerErrorRepository empDailyPerErrorRepo;

		@Override
		public OuenWorkTimeSheetOfDaily find(String empId, GeneralDate ymd) {
			return ouenWorkTimeSheetOfDailyRepo.find(empId, ymd);
		}

		@Override
		public String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date) {
			return syWorkplaceAdapter.getAffWkpHistItemByEmpDate(employeeID, date);
		}

		@Override
		public Optional<Task> getTask(TaskFrameNo taskFrameNo, WorkCode code) {
			return taskingRepo.getOptionalTask(AppContexts.user().companyId(), taskFrameNo, new TaskCode(code.v()));
		}

		@Override
		public TaskFrameUsageSetting getTaskFrameUsageSetting() {
			return taskFrameUsageSettingRepo.getWorkFrameUsageSetting(AppContexts.user().companyId());
		}

		@Override
		public Optional<IntegrationOfDaily> get(String employeeId, DatePeriod date) {
			return Optional.ofNullable(getter.getIntegrationOfDaily(employeeId, date).size() > 0 ? getter.getIntegrationOfDaily(employeeId, date).get(0) : null);
		}

		@Override
		public IntegrationOfDaily calculationIntegrationOfDaily(IntegrationOfDaily integrationOfDaily,
				ExecutionType executionType) {
			return calculate.calculatePassCompanySetting(Collections.singletonList(integrationOfDaily), Optional.empty(), executionType).get(0);
		}

		@Override
		public List<EditStateOfDailyPerformance> getEditStateOfDailyPerformance(String empId, GeneralDate ymd) {
			return editStateOfDailyPerformanceRepo.findByKey(empId, ymd);
		}

		@Override
		public void insert(OuenWorkTimeSheetOfDaily domain) {
			ouenWorkTimeSheetOfDailyRepo.insert(Collections.singletonList(domain));

		}

		@Override
		public void update(OuenWorkTimeSheetOfDaily domain) {
			ouenWorkTimeSheetOfDailyRepo.update(Collections.singletonList(domain));

		}

		@Override
		public void delete(OuenWorkTimeSheetOfDaily domain) {
			ouenWorkTimeSheetOfDailyRepo.delete(Collections.singletonList(domain));

		}

		@Override
		public void insert(EditStateOfDailyPerformance domain) {
			editStateOfDailyPerformanceRepo.add(Collections.singletonList(domain));

		}

		@Override
		public void update(EditStateOfDailyPerformance domain) {
			editStateOfDailyPerformanceRepo.updateByKey(Collections.singletonList(domain));

		}

		@Override
		public Optional<OuenWorkTimeOfDaily> getOuenWorkTimeOfDaily(String empId, GeneralDate ymd) {
			return Optional.ofNullable(ouenWorkTimeOfDailyRepo.find(empId, ymd));

		}

		@Override
		public void insert(OuenWorkTimeOfDaily domain) {
			ouenWorkTimeOfDailyRepo.insert(Collections.singletonList(domain));

		}

		@Override
		public void update(OuenWorkTimeOfDaily domain) {
			ouenWorkTimeOfDailyRepo.update(Collections.singletonList(domain));

		}

		@Override
		public void delete(OuenWorkTimeOfDaily domain) {
			ouenWorkTimeOfDailyRepo.delete(Collections.singletonList(domain));

		}

		@Override
		public Optional<OuenWorkTimeSheetOfDaily> findOuenWorkTimeSheetOfDaily(String empId, GeneralDate ymd) {
			return Optional.ofNullable(ouenWorkTimeSheetOfDailyRepo.find(empId, ymd));
		}

		@Override
		public void delete(String sid, GeneralDate date, String code) {
			empDailyPerErrorRepo.deleteByErrorCode(sid, date, code);
		}

		@Override
		public void insert(EmployeeDailyPerError employeeDailyPerformanceError) {
			empDailyPerErrorRepo.insert(employeeDailyPerformanceError);
		}

	}

}
