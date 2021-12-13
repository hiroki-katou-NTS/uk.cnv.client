package nts.uk.ctx.at.record.pub.monthly.vacation.annualleave;

import java.util.List;
import java.util.Map;

import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;

/**
 * 社員の月毎の確定済み年休を取得する
 * @author shuichu_ishida
 */
public interface GetConfirmedAnnualLeave {

	/**
	 * 社員の月毎の確定済み年休を取得する
	 * @param employeeId 社員ID
	 * @param period 年月期間
	 * @return 年休利用状況リスト
	 */
	// RequestList255
	List<AnnualLeaveUsageExport> algorithm(String employeeId, YearMonthPeriod period);
	/**
	 * @author hoatt
	 * Doi ung response KDR001
	 * RequestList255 社員の月毎の確定済み年休を取得する - ver2
	 * @param employeeId 社員ID
	 * @param period 年月期間
	 * @return 年休利用状況リスト
	 */
	List<AnnualLeaveUsageExport> getYearHdMonthlyVer2(String employeeId, YearMonthPeriod period, Map<YearMonth, List<RemainMerge>> mapRemainMer);


	/**
	 * RequestList255 社員の月毎の確定済み年休を取得する - ver4
	 * @param employeeId 社員ID
	 * @param period 年月期間
	 * @return 年休利用状況リスト
	 */
	List<AnnualLeaveUsageExport> getYearHdMonthlyVer4(String employeeId, YearMonthPeriod period, Map<YearMonth, List<RemainMerge>> mapRemainMer);
}
