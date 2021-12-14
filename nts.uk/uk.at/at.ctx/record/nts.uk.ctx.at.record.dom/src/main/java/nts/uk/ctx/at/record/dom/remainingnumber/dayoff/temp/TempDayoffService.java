package nts.uk.ctx.at.record.dom.remainingnumber.dayoff.temp;

import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * （仮対応用）代休
 * @author shuichu_ishida
 */
public interface TempDayoffService {

	/**
	 * （仮対応用）代休
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param yearMonth 年月
	 * @param period 期間
	 * @param closureId 締めID
	 * @param closureDate 締め日
	 * @return 代休月別残数データ 
	 */
	MonthlyDayoffRemainData algorithm(String companyId, String employeeId, YearMonth yearMonth,
			DatePeriod period, ClosureId closureId, ClosureDate closureDate);
}