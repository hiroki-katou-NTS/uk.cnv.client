package nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.Getter;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.AbsRecMngInPeriodRefactParamInput;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.algorithm.param.CompenLeaveAggrResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManagementData;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class NumberCompensatoryLeavePeriodProcess {

	@Inject
	private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepository;

	@Inject
	private PayoutManagementDataRepository payoutManagementDataRepository;

	@Inject
	private InterimRecAbasMngRepository interimRecAbasMngRepository;

	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	@Inject
	private EmpSubstVacationRepository empSubstVacationRepository;

	@Inject
	private ComSubstVacationRepository comSubstVacationRepository;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private ClosureRepository closureRepo;

	@Inject
	private CompanyAdapter companyAdapter;
	
	@Inject
	private PayoutSubofHDManaRepository payoutSubofHDManaRepository;

	public CompenLeaveAggrResult process(AbsRecMngInPeriodRefactParamInput inputParam) {
		return NumberCompensatoryLeavePeriodQuery.process(createRequire(), inputParam);

	}

	public RequireImpl createRequire() {
		return new RequireImplBuilder(substitutionOfHDManaDataRepository, payoutManagementDataRepository
				, interimRecAbasMngRepository, shareEmploymentAdapter)
				.empSubstVacationRepository(empSubstVacationRepository)
				.comSubstVacationRepository(comSubstVacationRepository).companyAdapter(companyAdapter)
				.closureEmploymentRepo(closureEmploymentRepo).closureRepo(closureRepo)
				.payoutSubofHDManaRepository(payoutSubofHDManaRepository).build();
	}
	
	public class RequireImpl implements NumberCompensatoryLeavePeriodQuery.Require {

		private final SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepository;

		private final PayoutManagementDataRepository payoutManagementDataRepository;

		private final InterimRecAbasMngRepository interimRecAbasMngRepository;

		private final ShareEmploymentAdapter shareEmploymentAdapter;

		private final EmpSubstVacationRepository empSubstVacationRepository;

		private final ComSubstVacationRepository comSubstVacationRepository;
		
		private final PayoutSubofHDManaRepository payoutSubofHDManaRepository;

		public RequireImpl(RequireImplBuilder builder) {
			this.substitutionOfHDManaDataRepository = builder.getSubstitutionOfHDManaDataRepository();
			this.payoutManagementDataRepository = builder.getPayoutManagementDataRepository();
			this.interimRecAbasMngRepository = builder.getInterimRecAbasMngRepository();
			this.shareEmploymentAdapter = builder.getShareEmploymentAdapter();
			this.empSubstVacationRepository = builder.getEmpSubstVacationRepository();
			this.comSubstVacationRepository = builder.getComSubstVacationRepository();
			this.payoutSubofHDManaRepository = builder.getPayoutSubofHDManaRepository();

		}

		@Override
		public List<InterimAbsMng> getAbsBySidDatePeriod(String sid, DatePeriod period) {
			return interimRecAbasMngRepository.getAbsBySidDatePeriod(sid, period);
		}

		@Override
		public List<InterimRecMng> getRecBySidDatePeriod(String sid, DatePeriod period) {
			return interimRecAbasMngRepository.getRecBySidDatePeriod(sid, period);
		}

		@Override
		public Optional<EmpSubstVacation> findEmpById(String companyId, String contractTypeCode) {
			return empSubstVacationRepository.findById(companyId, contractTypeCode);
		}

		@Override
		public Optional<ComSubstVacation> findComById(String companyId) {
			return comSubstVacationRepository.findById(companyId);
		}

		@Override
		public List<EmploymentHistShareImport> findByEmployeeIdOrderByStartDate(String employeeId) {
			return shareEmploymentAdapter.findByEmployeeIdOrderByStartDate(employeeId);
		}

		@Override
		public List<PayoutSubofHDManagement> getOccDigetByListSid(String sid, DatePeriod date) {
			return payoutSubofHDManaRepository.getOccDigetByListSid(sid, date);
		}

		@Override
		public List<SubstitutionOfHDManagementData> getByYmdUnOffset(String sid) {
			return substitutionOfHDManaDataRepository.getBysiD(AppContexts.user().companyId(), sid);
		}

		@Override
		public List<PayoutManagementData> getPayoutMana(String sid) {
			return payoutManagementDataRepository.getSid(AppContexts.user().companyId(), sid);
		}

	}

	@Getter
	public class RequireImplBuilder {

		private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepository;

		private PayoutManagementDataRepository payoutManagementDataRepository;

		private InterimRecAbasMngRepository interimRecAbasMngRepository;

		private ShareEmploymentAdapter shareEmploymentAdapter;

		private EmpSubstVacationRepository empSubstVacationRepository;

		private ComSubstVacationRepository comSubstVacationRepository;


		private CompanyAdapter companyAdapter;
		
		private PayoutSubofHDManaRepository payoutSubofHDManaRepository;

		private ClosureEmploymentRepository closureEmploymentRepo;

		private ClosureRepository closureRepo;

		public RequireImplBuilder(SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepository,
				PayoutManagementDataRepository payoutManagementDataRepository,
				InterimRecAbasMngRepository interimRecAbasMngRepository,
				ShareEmploymentAdapter shareEmploymentAdapter) {
			this.substitutionOfHDManaDataRepository = substitutionOfHDManaDataRepository;
			this.payoutManagementDataRepository = payoutManagementDataRepository;
			this.interimRecAbasMngRepository = interimRecAbasMngRepository;
			this.shareEmploymentAdapter = shareEmploymentAdapter;
		}

		public RequireImplBuilder empSubstVacationRepository(EmpSubstVacationRepository empSubstVacationRepository) {
			this.empSubstVacationRepository = empSubstVacationRepository;
			return this;
		}

		public RequireImplBuilder comSubstVacationRepository(ComSubstVacationRepository comSubstVacationRepository) {
			this.comSubstVacationRepository = comSubstVacationRepository;
			return this;
		}


		public RequireImplBuilder companyAdapter(CompanyAdapter companyAdapter) {
			this.companyAdapter = companyAdapter;
			return this;
		}
		
		public RequireImplBuilder payoutSubofHDManaRepository(PayoutSubofHDManaRepository payoutSubofHDManaRepository) {
			this.payoutSubofHDManaRepository = payoutSubofHDManaRepository;
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
		
		public RequireImpl build() {
			return new RequireImpl(this);
		}
	}
}
