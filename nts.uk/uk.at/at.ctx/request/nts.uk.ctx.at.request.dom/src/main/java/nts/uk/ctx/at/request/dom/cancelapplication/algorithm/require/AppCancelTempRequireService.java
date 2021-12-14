package nts.uk.ctx.at.request.dom.cancelapplication.algorithm.require;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.GetPeriodCanProcesseAdapter;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.AchievementAtrImport;
import nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto.IgnoreFlagDuringLockImport;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SWkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.actuallock.DetermineActualResultLockAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.actuallock.DetermineActualResultLockAdapter.LockStatus;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.actuallock.DetermineActualResultLockAdapter.PerformanceType;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.cancelapplication.RQRecoverAppReflectImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.cancelapplication.RQRecoverAppReflectOutputImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.cancelapplication.RecoverRCBeforeAppReflectAdaper;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.cancelapplication.RecoverSCBeforeAppReflectAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.workfixed.WorkFixedAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.workrecord.identificationstatus.IdentificationAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.BasicScheduleConfirmImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExeConditionRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.AppReflectExecutionCondition;
import nts.uk.ctx.at.request.dom.applicationreflect.object.ReflectStatusResult;
import nts.uk.ctx.at.request.dom.cancelapplication.algorithm.ApplicationCancellationProcess;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class AppCancelTempRequireService {
	
	@Inject
	protected ApplicationRepository appRepo;
	
	@Inject
	protected DetermineActualResultLockAdapter determineActualResultLockAdapter;
	
	@Inject
	protected IdentificationAdapter identificationAdapter;
	
	@Inject
	protected ScBasicScheduleAdapter scBasicScheduleAdapter;
	
	@Inject
	protected AppReflectExeConditionRepository appReflectExeConditionRepository;
	
	@Inject
	protected WorkFixedAdapter workFixedAdapter;
	
	@Inject
	protected EmployeeRequestAdapter employeeRequestAdapter;
	
	@Inject
	protected ClosureEmploymentRepository closureEmploymentRepository;
	
	@Inject
	protected EmpEmployeeAdapter empEmployeeAdapter;
	
	@Inject
	protected ClosureEmploymentRepository closureEmploymentRepo;
	
	@Inject
	protected ClosureRepository closureRepo;
	
	@Inject
	protected ShareEmploymentAdapter shareEmploymentAdapter;
	
	@Inject
	protected InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	@Inject
	protected RecoverSCBeforeAppReflectAdapter recoverSCBeforeAppReflectAdapter;
	
	@Inject
	protected RecoverRCBeforeAppReflectAdaper recoverRCBeforeAppReflectAdaper;
	
	@Inject
	private GetPeriodCanProcesseAdapter getPeriodCanProcesseAdapter;
	
	public ApplicationCancellationProcess.Require createRequire() {
		return new ApplicationCancellationProcess.Require() {
			
			@Override
			public RQRecoverAppReflectOutputImport processRecover(ApplicationShare application, GeneralDate date,
					ReflectStatusResult reflectStatus, NotUseAtr dbRegisterClassfi) {
				return recoverRCBeforeAppReflectAdaper.process(application, date, reflectStatus, dbRegisterClassfi);
			}
			
			@Override
			public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
				return employeeRequestAdapter.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId);
			}
			
			@Override
			public SWkpHistImport getSWkpHistByEmployeeID(String employeeId, GeneralDate baseDate) {
				return employeeRequestAdapter.getSWkpHistByEmployeeID(employeeId, baseDate);
			}
			
			@Override
			public boolean getEmploymentFixedStatus(String companyID, GeneralDate date, String workPlaceID, int closureID) {
				return workFixedAdapter.getEmploymentFixedStatus(companyID, date, workPlaceID, closureID);
			}
			
			@Override
			public LockStatus lockStatus(String companyId, GeneralDate baseDate, Integer closureId, PerformanceType type) {
				return determineActualResultLockAdapter.lockStatus(companyId, baseDate, closureId, type);
			}
			
			@Override
			public List<GeneralDate> getProcessingYMD(String companyID, String employeeID, DatePeriod period) {
				return identificationAdapter.getProcessingYMD(companyID, employeeID, period);
			}
			
			@Override
			public List<BasicScheduleConfirmImport> findConfirmById(List<String> employeeID, DatePeriod date) {
				return scBasicScheduleAdapter.findConfirmById(employeeID, date);
			}
			
			@Override
			public RQRecoverAppReflectImport process(ApplicationShare application, GeneralDate date,
					ReflectStatusResult reflectStatus, NotUseAtr dbRegisterClassfi) {
				return recoverSCBeforeAppReflectAdapter.process(application, date, reflectStatus, dbRegisterClassfi);
			}
			
			@Override
			public Optional<AppReflectExecutionCondition> findAppReflectExecCond(String companyId) {
				return appReflectExeConditionRepository.findByCompanyId(companyId);
			}
			
			@Override
			public EmployeeImport employee(CacheCarrier cacheCarrier, String empId) {
				return empEmployeeAdapter.findByEmpIdRequire(cacheCarrier, empId);
			}
			
			@Override
			public List<Closure> closure(String companyId) {
				return closureRepo.findAll(companyId);
			}
			
			@Override
			public List<SharedSidPeriodDateEmploymentImport> employmentHistory(CacheCarrier cacheCarrier, List<String> sids,
					DatePeriod datePeriod) {
				return shareEmploymentAdapter.getEmpHistBySidAndPeriodRequire(cacheCarrier, sids, datePeriod);
			}
			
			@Override
			public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
				return closureEmploymentRepo.findByEmploymentCD(companyID, employmentCD);
			}
			
			@Override
			public void updateApp(Application application) {
				appRepo.update(application);
			}
			
			@Override
			public void registerRemain(String cid, String sid, List<GeneralDate> lstDate) {
				interimRemainDataMngRegisterDateChange.registerDateChange(cid, sid, lstDate);
			}
			
			@Override
			public List<EmploymentHistShareImport> findEmpHistbySid(String employeeId) {
				return shareEmploymentAdapter.findByEmployeeIdOrderByStartDate(employeeId);
			}
			
			@Override
			public Optional<ClosureEmployment> findByEmploymentCD(String companyID, String employmentCD) {
				return closureEmploymentRepository.findByEmploymentCD(companyID, employmentCD);
			}

			@Override
			public List<Closure> closureActive(String companyId, UseClassification useAtr) {
				return closureRepo.findAllActive(companyId, useAtr);
			}

			@Override
			public List<DatePeriod> getPeriodProcess(String employeeId, DatePeriod period,
					List<EmploymentHistoryImported> listEmploymentHis, IgnoreFlagDuringLockImport ignoreFlagDuringLock,
					AchievementAtrImport achievementAtr) {
				return getPeriodCanProcesseAdapter.get(employeeId, period, listEmploymentHis, ignoreFlagDuringLock, achievementAtr);
			}
		};
	}
}
