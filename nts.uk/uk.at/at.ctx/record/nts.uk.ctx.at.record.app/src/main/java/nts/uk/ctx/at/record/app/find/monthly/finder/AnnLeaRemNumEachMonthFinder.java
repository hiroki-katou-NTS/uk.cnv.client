package nts.uk.ctx.at.record.app.find.monthly.finder;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.app.find.monthly.root.AnnLeaRemNumEachMonthDto;
import nts.uk.ctx.at.record.dom.monthly.vacation.annualleave.AnnLeaRemNumEachMonthRepository;
import nts.uk.ctx.at.shared.app.util.attendanceitem.MonthlyFinderFacade;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureDate;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;

@Stateless
public class AnnLeaRemNumEachMonthFinder extends MonthlyFinderFacade {
	
	@Inject
	private AnnLeaRemNumEachMonthRepository repo;

	@Override
	@SuppressWarnings("unchecked")
	public AnnLeaRemNumEachMonthDto find(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		return AnnLeaRemNumEachMonthDto.from(this.repo.find(employeeId, yearMonth, closureId, closureDate).orElse(null));
	}

}
