package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

public interface PublicHolidayCarryForwardHistoryRepository {

	/**
	 * 登録および更新
	 * @param domain
	 */
	void persistAndUpdate(PublicHolidayCarryForwardHistory domain);
	
	
	/**
	 * 当月以降を削除
	 * @param employeeId
	 * @param yearMonth
	 */
	void deleteThisMonthAfter(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);
}
