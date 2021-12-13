package nts.uk.ctx.at.record.pubimp.workrecord.actuallock;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsCondition;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.CreatingDailyResultsConditionRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.AchievementAtr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.GetPeriodCanProcesse;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.IgnoreFlagDuringLock;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.organization.adapter.EmploymentAdapter;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLock;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLockRepository;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.GetPeriodCanProcessePub;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.AchievementAtrExport;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.EmploymentHistoryExport;
import nts.uk.ctx.at.record.pub.workrecord.actuallock.export.IgnoreFlagDuringLockExport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagementRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class GetPeriodCanProcessePubImpl implements GetPeriodCanProcessePub {

	@Inject
	private ClosureStatusManagementRepository closureStatusManagementRepo;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private ActualLockRepository actualLockRepository;
	@Inject
	private EmploymentAdapter employmentAdapter;
	@Inject
	private CreatingDailyResultsConditionRepository creatingDailyResultsConditionRepo;
	@Inject
	private EmpEmployeeAdapter employeeAdapter;


	@Override
	public List<DatePeriod> get(String employeeId, DatePeriod period, List<EmploymentHistoryExport> listEmploymentHis,
			IgnoreFlagDuringLockExport ignoreFlagDuringLock, AchievementAtrExport achievementAtr) {
		RequireImpl impl = new RequireImpl(closureStatusManagementRepo, closureEmploymentRepo, closureRepository,
				actualLockRepository, employmentAdapter, creatingDailyResultsConditionRepo, employeeAdapter);

		return GetPeriodCanProcesse.get(impl, AppContexts.user().companyId(), employeeId, period, listEmploymentHis.stream()
				.map(x -> new EmploymentHistoryImported(x.getEmployeeId(), x.getEmploymentCode(), x.getPeriod()))
				.collect(Collectors.toList()), IgnoreFlagDuringLock.valueOf(ignoreFlagDuringLock.value),
				AchievementAtr.valueOf(achievementAtr.value));
	}

	@AllArgsConstructor
	private class RequireImpl implements GetPeriodCanProcesse.Require {

		private final ClosureStatusManagementRepository closureStatusManagementRepo;

		private final ClosureEmploymentRepository closureEmploymentRepo;

		private final ClosureRepository closureRepository;

		private final ActualLockRepository actualLockRepository;
		private final EmploymentAdapter employmentAdapter;
		private final CreatingDailyResultsConditionRepository creatingDailyResultsConditionRepo;
		private final EmpEmployeeAdapter employeeAdapter;


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

		@Override
		public Optional<CreatingDailyResultsCondition> creatingDailyResultsCondition(String cid) {
			return creatingDailyResultsConditionRepo.findByCid(cid);
		}

		@Override
		public EmployeeImport employeeInfo(CacheCarrier cacheCarrier, String empId) {
			return employeeAdapter.findByEmpIdRequire(cacheCarrier, empId);
		}

		@Override
		public List<EmploymentHistoryImported> getEmpHistBySid(String companyId, String employeeId) {
			return employmentAdapter.getEmpHistBySid(companyId, employeeId);
		}
	}

}
