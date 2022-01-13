package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.BreakDayOffRemainMngRefactParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.SubstituteHolidayAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class NumberRemainVacationLeaveRangeProcess {

	@Inject
	private ComDayOffManaDataRepository comDayOffManaDataRepository;

	@Inject
	private LeaveManaDataRepository leaveManaDataRepository;

	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	@Inject
	private CompensLeaveEmSetRepository compensLeaveEmSetRepository;

	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepository;

	@Inject
	private InterimBreakDayOffMngRepository interimBreakDayOffMngRepository;

	@Inject
	private CompanyAdapter companyAdapter;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private ClosureRepository closureRepo;
	
	@Inject
	private LeaveComDayOffManaRepository leaveComDayOffManaRepository;

	public SubstituteHolidayAggrResult getBreakDayOffMngInPeriod(BreakDayOffRemainMngRefactParam inputParam) {

		return NumberRemainVacationLeaveRangeQuery.getBreakDayOffMngInPeriod(createRequire(), inputParam);
	}

	public RequireImpl createRequire() {
		return new RequireImpl.RequireImplBuilder(comDayOffManaDataRepository,
				leaveManaDataRepository, shareEmploymentAdapter, compensLeaveEmSetRepository,
				compensLeaveComSetRepository)
						.interimBreakDayOffMngRepo(interimBreakDayOffMngRepository).companyAdapter(companyAdapter)
						.closureEmploymentRepo(closureEmploymentRepo).closureRepo(closureRepo)
						.leaveComDayOffManaRepository(leaveComDayOffManaRepository).build();
	}
	
	@Getter
	public static class RequireImpl implements NumberRemainVacationLeaveRangeQuery.Require {

		private final ComDayOffManaDataRepository comDayOffManaDataRepository;

		private final LeaveManaDataRepository leaveManaDataRepository;

		private final ShareEmploymentAdapter shareEmploymentAdapter;

		private final CompensLeaveEmSetRepository compensLeaveEmSetRepository;

		private final CompensLeaveComSetRepository compensLeaveComSetRepository;
		
		private final InterimBreakDayOffMngRepository interimBreakDayOffMngRepository;

		private final CompanyAdapter companyAdapter;
		
		private final LeaveComDayOffManaRepository leaveComDayOffManaRepository;

		private final ClosureEmploymentRepository closureEmploymentRepo;

		private final ClosureRepository closureRepo;

		public RequireImpl(RequireImplBuilder builder) {
			this.comDayOffManaDataRepository = builder.comDayOffManaDataRepository;
			this.leaveManaDataRepository = builder.leaveManaDataRepository;
			this.shareEmploymentAdapter = builder.shareEmploymentAdapter;
			this.compensLeaveEmSetRepository = builder.compensLeaveEmSetRepository;
			this.compensLeaveComSetRepository = builder.compensLeaveComSetRepository;
			this.interimBreakDayOffMngRepository = builder.interimBreakDayOffMngRepository;
			this.companyAdapter = builder.companyAdapter;
			this.closureEmploymentRepo = builder.closureEmploymentRepo;
			this.closureRepo = builder.closureRepo;
			this.leaveComDayOffManaRepository = builder.leaveComDayOffManaRepository;

		}

		@Override
		public Optional<BsEmploymentHistoryImport> findEmploymentHistory(String companyId, String employeeId,
				GeneralDate baseDate) {
			return shareEmploymentAdapter.findEmploymentHistory(companyId, employeeId, baseDate);
		}

		@Override
		public CompensatoryLeaveEmSetting findComLeavEmpSet(String companyId, String employmentCode) {
			return compensLeaveEmSetRepository.find(companyId, employmentCode);
		}

		@Override
		public CompensatoryLeaveComSetting findComLeavComSet(String companyId) {
			return compensLeaveComSetRepository.find(companyId);
		}
		
		@Override
		public List<LeaveComDayOffManagement> getDigestOccByListComId(String sid, DatePeriod period) {
			return leaveComDayOffManaRepository.getDigestOccByListComId(sid, period);
		}

		@Override
		public List<InterimDayOffMng> getTempDayOffBySidPeriod(String sid, DatePeriod period) {
			return interimBreakDayOffMngRepository.getDayOffBySidPeriod(sid, period);
		}

		@Override
		public List<CompensatoryDayOffManaData> getFixByDayOffDatePeriod(String sid) {
			return comDayOffManaDataRepository.getBySid(AppContexts.user().companyId(), sid);
		}

		@Override
		public List<InterimBreakMng> getTempBreakBySidPeriod(String sid, DatePeriod period) {
			return interimBreakDayOffMngRepository.getBySidPeriod(sid, period);
		}

		@Override
		public List<LeaveManagementData> getFixLeavByDayOffDatePeriod(String sid) {
			return leaveManaDataRepository.getBySid(AppContexts.user().companyId(), sid);
		}

		public static class RequireImplBuilder {

			public ComDayOffManaDataRepository comDayOffManaDataRepository;

			public LeaveManaDataRepository leaveManaDataRepository;

			public ShareEmploymentAdapter shareEmploymentAdapter;

			public CompensLeaveEmSetRepository compensLeaveEmSetRepository;

			public CompensLeaveComSetRepository compensLeaveComSetRepository;

			public InterimBreakDayOffMngRepository interimBreakDayOffMngRepository;

			public CompanyAdapter companyAdapter;
			
			public LeaveComDayOffManaRepository leaveComDayOffManaRepository;

			public ClosureEmploymentRepository closureEmploymentRepo;

			public ClosureRepository closureRepo;

			public RequireImplBuilder(ComDayOffManaDataRepository comDayOffManaDataRepository,
					LeaveManaDataRepository leaveManaDataRepository, ShareEmploymentAdapter shareEmploymentAdapter,
					CompensLeaveEmSetRepository compensLeaveEmSetRepository,
					CompensLeaveComSetRepository compensLeaveComSetRepository) {
				this.comDayOffManaDataRepository = comDayOffManaDataRepository;
				this.leaveManaDataRepository = leaveManaDataRepository;
				this.shareEmploymentAdapter = shareEmploymentAdapter;
				this.compensLeaveEmSetRepository = compensLeaveEmSetRepository;
				this.compensLeaveComSetRepository = compensLeaveComSetRepository;
			}

			public RequireImplBuilder interimBreakDayOffMngRepo(
					InterimBreakDayOffMngRepository interimBreakDayOffMngRepository) {
				this.interimBreakDayOffMngRepository = interimBreakDayOffMngRepository;
				return this;
			}

			public RequireImplBuilder companyAdapter(CompanyAdapter companyAdapter) {
				this.companyAdapter = companyAdapter;
				return this;
			}

			public RequireImplBuilder closureEmploymentRepo(ClosureEmploymentRepository closureEmploymentRepo) {
				this.closureEmploymentRepo = closureEmploymentRepo;
				return this;
			}

			public RequireImplBuilder closureRepo(ClosureRepository closureRepo) {
				this.closureRepo = closureRepo;
				return this;
			}
			
			public RequireImplBuilder leaveComDayOffManaRepository(LeaveComDayOffManaRepository leaveComDayOffManaRepository) {
				this.leaveComDayOffManaRepository = leaveComDayOffManaRepository;
				return this;
			}

			public RequireImpl build() {
				return new RequireImpl(this);
			}

		}

	}
}
