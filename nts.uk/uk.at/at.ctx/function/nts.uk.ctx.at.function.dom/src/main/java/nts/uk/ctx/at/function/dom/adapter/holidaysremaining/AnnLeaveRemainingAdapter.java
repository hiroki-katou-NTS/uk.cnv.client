package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import java.util.List;

import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;

public interface AnnLeaveRemainingAdapter {
	/**
	 * RequestList #No.363
	 * @param employeeId
	 * @param datePeriod
	 * @return
	 */
	public List<AnnLeaveUsageStatusOfThisMonthImported> getAnnLeaveUsageOfThisMonth(String employeeId,
			DatePeriod datePeriod);

	/**
	 * RequestList #No.265
	 * @param employeeId
	 * @return
	 */
	public AnnLeaveOfThisMonthImported getAnnLeaveOfThisMonth(String employeeId);

	/**
	 * RequestList #No.369
	 * @param companyId
	 * @param employeeId
	 * @return
	 */
	public NextHolidayGrantDateImported getNextHolidayGrantDate(String companyId, String employeeId);

	/**
	 * 社員の当月の年休付与数を取得する
	 * @param employeeId 社員ID
	 * @return 年休付与数 
	 * RequestList281
	 */
	public List<AnnLeaGrantNumberImported> algorithm(String employeeId);
	
	/**
	 * 社員の月毎の確定済み年休を取得する
	 * @param employeeId 社員ID
	 * @param period 年月期間
	 * @return 年休利用状況リスト
	 * RequestList255
	 */
	 public List<AnnualLeaveUsageImported> algorithm(String employeeId, YearMonthPeriod period);
	 /**
	 * @author hoatt
	 * Doi ung response KDR001
	 * RequestList255 社員の月毎の確定済み年休を取得する - ver2
	 * @param employeeId 社員ID
	 * @param period 年月期間
	 * @return 年休利用状況リスト
	 */
	 public List<AnnualLeaveUsageImported> getYearHdMonthlyVer2(String employeeId, YearMonthPeriod period);

	/**
	 * 社員の当月の年休付与数を取得する
	 * @param employeeId 社員ID
	 * @return 年休付与数
	 * RequestList281 V2
	 */
	public List<AnnLeaGrantNumberImported> getAnnLeaGrantNumberImporteds(String employeeId);
}
