package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface TempPublicHolidayManagementRepository {

	/**
	 * 検索 暫定公休管理データ
	 * @param sid 社員ID
	 * @param ymd　年月日
	 * @return
	 */
	List<TempPublicHolidayManagement> find(String sid, GeneralDate ymd);
	/**
	 * 検索 暫定公休管理データ（期間）
	 * @param sid 社員ID
	 * @param period　期間
	 * @return
	 */
	List<TempPublicHolidayManagement> findByPeriodOrderByYmd(String sid, DatePeriod period);
}
