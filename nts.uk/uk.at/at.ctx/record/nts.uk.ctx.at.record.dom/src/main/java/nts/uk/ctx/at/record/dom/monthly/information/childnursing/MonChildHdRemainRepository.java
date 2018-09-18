package nts.uk.ctx.at.record.dom.monthly.information.childnursing;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureDate;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

public interface MonChildHdRemainRepository {

	/**
	 * Find by YearMonth, ClosureId & ClosureDate
	 * @param employeeId
	 * @param yearMonth
	 * @param closureId
	 * @param closureDate
	 * @return
	 */
	Optional<MonChildHdRemain> find(String employeeId, YearMonth yearMonth,
			ClosureId closureId, ClosureDate closureDate);
	
	/**
	 * Find by YearMonth
	 * @param employeeId
	 * @param yearMonth
	 * @return
	 */
	List<MonChildHdRemain> findByYearMonthOrderByStartYmd(String employeeId, YearMonth yearMonth);
	
	/**
	 * 
	 * @param employeeId
	 * @param yearMonth
	 * @param closureId
	 * @return
	 */
	List<MonChildHdRemain> findByYMAndClosureIdOrderByStartYmd(
			String employeeId, YearMonth yearMonth, ClosureId closureId);
	
	/**
	 * 
	 * @param employeeIds
	 * @param yearMonth
	 * @param closureId
	 * @param closureDate
	 * @return
	 */
	List<MonChildHdRemain> findByEmployees(List<String> employeeIds, YearMonth yearMonth,
			ClosureId closureId, ClosureDate closureDate);

	/**
	 * 
	 * @param employeeIds
	 * @param yearMonths
	 * @return
	 */
	List<MonChildHdRemain> findBySidsAndYearMonths(List<String> employeeIds, List<YearMonth> yearMonths);
	
	/**
	 * 
	 * @param employeeId
	 * @param criteriaDate
	 * @return
	 */
	List<MonChildHdRemain> findByDate(String employeeId, GeneralDate criteriaDate);

	/**
	 * 
	 * @param employeeId
	 * @param period
	 * @return
	 */
	List<MonChildHdRemain> findByPeriodIntoEndYmd(String employeeId, DatePeriod period);

	/**
	 * 
	 * @param attendanceTimeOfMonthly
	 */
	void persistAndUpdate(MonChildHdRemain remarksMonthlyRecord);

	/**
	 * 
	 * @param employeeId
	 * @param yearMonth
	 * @param closureId
	 * @param closureDate
	 */
	void remove(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);

	/**
	 * 
	 * @param employeeId
	 * @param yearMonth
	 */
	void removeByYearMonth(String employeeId, YearMonth yearMonth);
}
