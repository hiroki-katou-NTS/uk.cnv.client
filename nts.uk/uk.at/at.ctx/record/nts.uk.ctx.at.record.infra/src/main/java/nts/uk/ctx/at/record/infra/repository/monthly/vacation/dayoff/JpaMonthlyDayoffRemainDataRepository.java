package nts.uk.ctx.at.record.infra.repository.monthly.vacation.dayoff;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.ClosureStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.dayoff.MonthlyDayoffRemainDataRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@Stateless
public class JpaMonthlyDayoffRemainDataRepository extends JpaRepository implements MonthlyDayoffRemainDataRepository{

	@Inject
	private RemainMergeRepository remainRepo;
	
	@Override
	public List<MonthlyDayoffRemainData> getDayOffDataBySidYmStatus(String employeeId, YearMonth ym,
			ClosureStatus status) {
		
		return  this.remainRepo.getByYmStatus(employeeId, ym, status)
				.stream().map(c -> c.getMonthlyDayoffRemainData()).collect(Collectors.toList());
	}
	
	@Override
	public Optional<MonthlyDayoffRemainData> find(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		
		return this.remainRepo.find(employeeId, yearMonth, closureId, closureDate)
				.map(c -> c.getMonthlyDayoffRemainData());
	}
	
	@Override
	public List<MonthlyDayoffRemainData> findByYearMonthOrderByStartYmd(String employeeId, YearMonth yearMonth) {

		return this.remainRepo.findByYearMonthOrderByStartYmd(employeeId, yearMonth)
				.stream().map(c -> c.getMonthlyDayoffRemainData()).collect(Collectors.toList());
	}
	
	@Override
	public List<MonthlyDayoffRemainData> findBySidsAndYearMonths(List<String> employeeIds, List<YearMonth> yearMonths) {
		
		return this.remainRepo.findBySidsAndYearMonths(employeeIds, yearMonths)
				.stream().map(c -> c.getMonthlyDayoffRemainData()).collect(Collectors.toList());
	}
	
	@Override
	public void persistAndUpdate(MonthlyDayoffRemainData domain) {
		
		this.remainRepo.persistAndUpdate(domain);
	}
	
	@Override
	public void remove(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		
		this.remainRepo.removeDayOff(employeeId, yearMonth, closureId, closureDate);
	}
}
