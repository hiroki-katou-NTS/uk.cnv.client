package nts.uk.ctx.at.request.dom.applicationreflect.manager.algorithm.employee;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.GetPeriodCanProcesseAdapter;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.AchievementAtrImport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.IgnoreFlagDuringLockImport;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SWkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.actuallock.DetermineActualResultLockAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.actuallock.DetermineActualResultLockAdapter.LockStatus;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.actuallock.DetermineActualResultLockAdapter.PerformanceType;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.reflect.CreateEditStatusHistAppReasonAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.reflect.ReflectApplicationWorkRecordAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.workfixed.WorkFixedAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.workrecord.identificationstatus.IdentificationAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.reflect.ReflectApplicationWorkScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.BasicScheduleConfirmImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExeConditionRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.request.dom.reasonappdaily.ReasonApplicationDailyResult;
import nts.uk.ctx.at.request.dom.reasonappdaily.ReasonApplicationDailyResultRepo;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class CreateRequireReflectionProcess {

	@Inject
	private ReflectApplicationWorkScheduleAdapter eflectApplicationWorkScheduleAdapter;

	@Inject
	private ScBasicScheduleAdapter scBasicScheduleAdapter;

	@Inject
	private IdentificationAdapter identificationAdapter;

	@Inject
	private DetermineActualResultLockAdapter determineActualResultLockAdapter;

	@Inject
	private EmployeeRequestAdapter employeeRequestAdapter;

	@Inject
	private WorkFixedAdapter workFixedAdapter;

	@Inject
	private ReflectApplicationWorkRecordAdapter reflectApplicationWorkRecordAdapter;

	@Inject
	private CreateEditStatusHistAppReasonAdapter createEditStatusHistAppReasonAdapter;

//	@Inject
//	private RequestSettingRepository requestSettingRepository;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;

	@Inject
	private AppReflectExeConditionRepository appReflectExeConditionRepository;

	@Inject
	private ReasonApplicationDailyResultRepo reasonApplicationDailyResultRepo;
	
	@Inject
	private GetPeriodCanProcesseAdapter getPeriodCanProcesseAdapter;

	public RequireImpl createImpl() {
		return new RequireImpl(AppContexts.user().companyId(), eflectApplicationWorkScheduleAdapter,
				scBasicScheduleAdapter, identificationAdapter, determineActualResultLockAdapter, employeeRequestAdapter,
				workFixedAdapter, reflectApplicationWorkRecordAdapter, createEditStatusHistAppReasonAdapter,
				closureEmploymentRepository, appReflectExeConditionRepository,
				reasonApplicationDailyResultRepo, getPeriodCanProcesseAdapter);
	}

	@AllArgsConstructor
	public class RequireImpl implements ReflectionProcess.Require {

		private final String cid;

		private final ReflectApplicationWorkScheduleAdapter eflectApplicationWorkScheduleAdapter;

		private final ScBasicScheduleAdapter scBasicScheduleAdapter;

		private final IdentificationAdapter identificationAdapter;

		private final DetermineActualResultLockAdapter determineActualResultLockAdapter;

		private final EmployeeRequestAdapter employeeRequestAdapter;

		private final WorkFixedAdapter workFixedAdapter;

		private final ReflectApplicationWorkRecordAdapter reflectApplicationWorkRecordAdapter;

		private final CreateEditStatusHistAppReasonAdapter createEditStatusHistAppReasonAdapter;

		//private final RequestSettingRepository requestSettingRepository;

		private final ClosureEmploymentRepository closureEmploymentRepository;

		private final AppReflectExeConditionRepository appReflectExeConditionRepository;

		private final ReasonApplicationDailyResultRepo reasonApplicationDailyResultRepo;
		
		private final GetPeriodCanProcesseAdapter getPeriodCanProcesseAdapter;

		@Override
		public Pair<ReflectStatusResult, AtomTask> process(ApplicationShare application,
				GeneralDate date, ReflectStatusResult reflectStatus, int preAppWorkScheReflectAttr) {
			return eflectApplicationWorkScheduleAdapter.process(application, date, reflectStatus,
					preAppWorkScheReflectAttr);
		}

		@Override
		public List<BasicScheduleConfirmImport> findConfirmById(List<String> employeeID, DatePeriod date) {
			return scBasicScheduleAdapter.findConfirmById(employeeID, date);
		}

		@Override
		public List<GeneralDate> getProcessingYMD(String companyID, String employeeID, DatePeriod period) {
			return identificationAdapter.getProcessingYMD(companyID, employeeID, period);
		}

		@Override
		public LockStatus lockStatus(String companyId, GeneralDate baseDate, Integer closureId, PerformanceType type) {
			return determineActualResultLockAdapter.lockStatus(companyId, baseDate, closureId, type);
		}

		@Override
		public SWkpHistImport getSWkpHistByEmployeeID(String employeeId, GeneralDate baseDate) {
			return employeeRequestAdapter.getSWkpHistByEmployeeID(employeeId, baseDate);
		}

		@Override
		public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
			return employeeRequestAdapter.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId);
		}

		@Override
		public boolean getEmploymentFixedStatus(String companyID, GeneralDate date, String workPlaceID, int closureID) {
			return workFixedAdapter.getEmploymentFixedStatus(companyID, date, workPlaceID, closureID);
		}

		@Override
		public Pair<ReflectStatusResult, Optional<AtomTask>> processWork(ApplicationShare application,
				GeneralDate date, ReflectStatusResult reflectStatus, GeneralDateTime reflectTime) {
			return reflectApplicationWorkRecordAdapter.process(application, date, reflectStatus, reflectTime);
		}

		@Override
		public List<ReasonApplicationDailyResult> findReasonAppDaily(String employeeId, GeneralDate date,
				PrePostAtr preAtr, ApplicationType apptype, Optional<OvertimeAppAtr> overAppAtr) {
			return reasonApplicationDailyResultRepo.findReasonAppDaily(employeeId, date, preAtr, apptype, overAppAtr);
		}

		@Override
		public void addUpdateReason(List<ReasonApplicationDailyResult> reason) {

			reasonApplicationDailyResultRepo.addUpdateReason(cid, reason);
		}

		@Override
		public void processCreateHist(String employeeId, GeneralDate date, String appId,
				ScheduleRecordClassifi classification, Map<Integer, String> mapValue, GeneralDateTime reflectTime) {
			createEditStatusHistAppReasonAdapter.process(employeeId, date, appId, classification, mapValue, reflectTime);
		}

		@Override
		public Optional<AppReflectExecutionCondition> findAppReflectExecCond(String companyId) {
			return appReflectExeConditionRepository.findByCompanyId(companyId);
		}

//		@Override
//		public Optional<AppReflectionSetting> getAppReflectionSetting(String companyId, ApplicationType appType) {
//			// ????? apptype
//			return requestSettingRepository.getAppReflectionSetting(companyId);
//		}

//		@Override
//		public Optional<EmployeeWorkDataSetting> getEmpWorkDataSetting(String employeeId) {
//			// TODO ??? repo
//			return Optional.empty();
//		}

		@Override
		public Optional<ClosureEmployment> findByEmploymentCD(String companyID, String employmentCD) {
			return closureEmploymentRepository.findByEmploymentCD(companyID, employmentCD);
		}

		@Override
		public List<DatePeriod> getPeriodProcess(String employeeId, DatePeriod period,
				List<EmploymentHistoryImported> listEmploymentHis, IgnoreFlagDuringLockImport ignoreFlagDuringLock,
				AchievementAtrImport achievementAtr) {
			return getPeriodCanProcesseAdapter.get(employeeId, period, listEmploymentHis, ignoreFlagDuringLock, achievementAtr);
		}

	}
}
