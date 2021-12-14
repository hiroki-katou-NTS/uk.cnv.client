package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.AchievementAtr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.GetPeriodCanProcesse;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.IgnoreFlagDuringLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.reflectedperiod.ReflectedAtr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.reflectedperiod.ReflectedPeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.reflectedperiod.ReflectedPeriodOutput;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.organization.adapter.EmploymentAdapter;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLock;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLockRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.EmployeeGeneralInfoImport;
import nts.uk.ctx.at.shared.dom.calculationsetting.StampReflectionManagement;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.output.PeriodInMasterList;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagementRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.i18n.TextResource;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class CreateDailyResultEmployeeDomainServiceNewImpl implements CreateDailyResultEmployeeDomainServiceNew {

	@Inject
	private EmploymentAdapter employmentAdapter;

	@Inject
	private GetPeriodCanProcesse getPeriodCanProcesse;

	@Inject
	private ClosureStatusManagementRepository closureStatusManagementRepo;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private ActualLockRepository actualLockRepository;

	@Inject
	private CreateDailyEmployeesByPeriod createDailyEmployeesByPeriod;

	@Inject
	private ReflectedPeriod reflectedPeriod;
	
	@Inject
	private RegisterDailyAchievements registerDailyAchievements; 

	@Override
	public OutputCreateDailyResult createDailyResultEmployee(AsyncCommandHandlerContext asyncContext, String employeeId,
			DatePeriod periodTimes, String companyId, Optional<EmpCalAndSumExeLog> empCalAndSumExeLog,
			boolean reCreateWorkType, boolean reCreateWorkPlace, boolean reCreateRestTime,
			EmployeeGeneralInfoImport employeeGeneralInfoImport,
			Optional<StampReflectionManagement> stampReflectionManagement,
			Map<String, Map<String, WorkingConditionItem>> mapWorkingConditionItem,
			Map<String, Map<String, DateHistoryItem>> mapDateHistoryItem, PeriodInMasterList periodInMasterList,
			ExecutionTypeDaily executionType, Optional<Boolean> checkLock) {
		// 空のエラー一覧を作る
		List<ErrorMessageInfo> listErrorMessageInfo = new ArrayList<>();

		// 取得した「集計期間」．開始日を処理日にする
		GeneralDate processingDate = periodTimes.start();

		// Imported（就業）「所属雇用履歴」を取得する
		List<EmploymentHistoryImported> listEmploymentHis = this.employmentAdapter.getEmpHistBySid(companyId,
				employeeId);
		if (listEmploymentHis.isEmpty()) {
			// 取得したImported(就業．勤務実績)「所属雇用履歴」が存在するか確認する
			listErrorMessageInfo
					.add(new ErrorMessageInfo(companyId, employeeId, processingDate, ExecutionContent.DAILY_CREATION,
							new ErrMessageResource("010"), new ErrMessageContent(TextResource.localize("Msg_426"))));

			return new OutputCreateDailyResult(ProcessState.SUCCESS, listErrorMessageInfo);
		}

		GetPeriodCanProcesseRequireImpl require = new GetPeriodCanProcesseRequireImpl(closureStatusManagementRepo,
				closureEmploymentRepo, closureRepository, actualLockRepository);
		IgnoreFlagDuringLock ignoreFlagDuringLock = checkLock.isPresent() && checkLock.get().booleanValue()
				? IgnoreFlagDuringLock.CAN_CAL_LOCK
				: IgnoreFlagDuringLock.CANNOT_CAL_LOCK;
		// 作成できる期間一覧を求める
		@SuppressWarnings("static-access")
		List<DatePeriod> listPeriod = getPeriodCanProcesse.get(require, employeeId, periodTimes, listEmploymentHis,
				ignoreFlagDuringLock, AchievementAtr.DAILY);

		// 取得できた期間一覧をループする
		for (DatePeriod period : listPeriod) {
			Optional<String> empCalAndSumExecLogId = empCalAndSumExeLog.isPresent()
					? Optional.of(empCalAndSumExeLog.get().getEmpCalAndSumExecLogID())
					: Optional.empty();
			// 期間で社員の日別実績を作成する
			CreateDailyOuput createDailyOuput = createDailyEmployeesByPeriod.create(period, employeeId, executionType,
					empCalAndSumExecLogId, employeeGeneralInfoImport, periodInMasterList);
			listErrorMessageInfo.addAll(createDailyOuput.getListError());
			if (createDailyOuput.getReflectedPeriod() == ReflectedAtr.SUSPENDED) {
				new OutputCreateDailyResult(ProcessState.INTERRUPTION, listErrorMessageInfo);
			}
			//打刻を反映する
			ReflectedPeriodOutput reflectedPeriodOutput = reflectedPeriod.reflect(employeeId, period, executionType, createDailyOuput.getListIntegrationOfDaily(),
					listErrorMessageInfo, createDailyOuput.getChangedDailyAttendance(), empCalAndSumExecLogId);
			
			if(reflectedPeriodOutput.getReflectedAtr() == ReflectedAtr.SUSPENDED ) {
				new OutputCreateDailyResult(ProcessState.INTERRUPTION, listErrorMessageInfo); 
			}
			
			//日別実績を登録する
			registerDailyAchievements.register(createDailyOuput.getListIntegrationOfDaily(),
					createDailyOuput.getListError(), reflectedPeriodOutput.getListStamp(), empCalAndSumExecLogId);
		}

		return new OutputCreateDailyResult(ProcessState.SUCCESS, listErrorMessageInfo);
	}

	@AllArgsConstructor
	private class GetPeriodCanProcesseRequireImpl implements GetPeriodCanProcesse.Require {
		@Inject
		private ClosureStatusManagementRepository closureStatusManagementRepo;

		@Inject
		private ClosureEmploymentRepository closureEmploymentRepo;

		@Inject
		private ClosureRepository closureRepository;

		@Inject
		private ActualLockRepository actualLockRepository;

		@Override
		public DatePeriod getClosurePeriod(int closureId, YearMonth processYm) {
			// 指定した年月の期間を算出する
			DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(
					ClosureService.createRequireM1(closureRepository, closureEmploymentRepo), closureId, processYm);
			return datePeriodClosure;
		}

		@Override
		public List<ClosureStatusManagement> getAllByEmpId(String employeeId) {
			return closureStatusManagementRepo.getAllByEmpId(employeeId);
		}

		@Override
		public Optional<ClosureEmployment> findByEmploymentCD(String employmentCode) {
			String companyId = AppContexts.user().companyId();
			return closureEmploymentRepo.findByEmploymentCD(companyId, employmentCode);
		}

		@Override
		public Optional<ActualLock> findById(int closureId) {
			String companyId = AppContexts.user().companyId();
			return actualLockRepository.findById(companyId, closureId);
		}

		@Override
		public Closure findClosureById(int closureId) {
			String companyId = AppContexts.user().companyId();
			return closureRepository.findById(companyId, closureId).get();
		}

	}

}
