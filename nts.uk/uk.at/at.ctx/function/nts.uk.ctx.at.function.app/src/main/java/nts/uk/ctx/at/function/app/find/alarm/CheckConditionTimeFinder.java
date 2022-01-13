package nts.uk.ctx.at.function.app.find.alarm;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.employment.EmploymentAdapter;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.CheckConditionTimeDto;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.ExtractionRangeService;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.CurrentMonth;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class CheckConditionTimeFinder {

	@Inject
	private EmploymentAdapter employmentAdapter;

	@Inject
	private ClosureEmploymentRepository closureEmpRepo;

	@Inject
	private ClosureRepository closurerepo;

	@Inject
	private ExtractionRangeService extractionRangeService;

	public List<CheckConditionTimeDto> getCheckConditionTime(String alarmCode) {

		String companyId = AppContexts.user().companyId();
		String employmentCode = employmentAdapter.getClosure(AppContexts.user().employeeId(), GeneralDate.today());

		Optional<ClosureEmployment> closureEmploymentOpt = closureEmpRepo
				.findByEmploymentCD(AppContexts.user().companyId(), employmentCode);
		if (!closureEmploymentOpt.isPresent())
			throw new RuntimeException(" Clousure not find!");
		if (closureEmploymentOpt.get().getClosureId() == null)
			throw new RuntimeException("Closure is null!");
		Integer closureId = closureEmploymentOpt.get().getClosureId();

		Optional<Closure> closureOpt = closurerepo.findById(AppContexts.user().companyId(), closureId);
		if (!closureOpt.isPresent())
			throw new RuntimeException("Have a clousreId but not exist Closure domain!");
		CurrentMonth currentMonth = closureOpt.get().getClosureMonth();

		return extractionRangeService.getPeriodByCategory(alarmCode, companyId, closureId,
				currentMonth.getProcessingYm().v());
	}
	
	public int getProcessingYm() {
		String employmentCode = employmentAdapter.getClosure(AppContexts.user().employeeId(), GeneralDate.today());

		Optional<ClosureEmployment> closureEmploymentOpt = closureEmpRepo
				.findByEmploymentCD(AppContexts.user().companyId(), employmentCode);
		if (!closureEmploymentOpt.isPresent())
			throw new RuntimeException(" Clousure not find!");
		if (closureEmploymentOpt.get().getClosureId() == null)
			throw new RuntimeException("Closure is null!");
		Integer closureId = closureEmploymentOpt.get().getClosureId();

		Optional<Closure> closureOpt = closurerepo.findById(AppContexts.user().companyId(), closureId);
		if (!closureOpt.isPresent())
			throw new RuntimeException("Have a clousreId but not exist Closure domain!");
		CurrentMonth currentMonth = closureOpt.get().getClosureMonth();
		
		return currentMonth.getProcessingYm().v();		
	}

}
