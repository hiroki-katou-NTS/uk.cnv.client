package nts.uk.ctx.sys.log.dom.datacorrectionlog;

import java.time.Year;
import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.shr.com.security.audittrail.correction.content.DataCorrectionLog;
import nts.uk.shr.com.security.audittrail.correction.content.TargetDataType;

/**
 * 
 * @author HungTT
 *
 */

public interface DataCorrectionLogRepository {

	List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId, YearMonth ym, GeneralDate ymd);
	
	List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId, DatePeriod datePeriod);

	List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId, YearMonthPeriod ymPeriod);

	List<DataCorrectionLog> getAllLogData(TargetDataType targetDataType, List<String> listEmployeeId, Year yearStart, Year yearEnd);
	
	List<DataCorrectionLog> findByTargetAndDate(String operationId, List<String> listEmployeeId, DatePeriod period, TargetDataType targetDataType);
	
	/**
	 * CLI003: fix bug #108872
	 * EA修正履歴No3675
	 * @param operationIds
	 * @param listEmployeeId
	 * @param period
	 * @param targetDataType
	 * @return
	 */
	List<DataCorrectionLog> findByTargetAndDateScreenF(List<String> operationIds, List<String> listEmployeeId, DatePeriod period, TargetDataType targetDataType);
	
	List<DataCorrectionLog> findByTargetAndDate(List<String> operationIds, List<String> listEmployeeId, DatePeriod period, TargetDataType targetDataType);
	
	/**
	 * CLI003: fix bug #108979 OFFSET " + offset + " ROWSFETCH FIRST " + limit + " ROWS ONLY
	 * OFFSET " + offset + " ROWS"
	 * FETCH FIRST " + limit + " ROWS ONLY
	 * this.getEntity().createQuery(sql).setFirstResult(offset)
	 * setMaxResults(limit)
	 * @param operationIds
	 * @param listEmployeeId
	 * @param period
	 * @param targetDataType
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<DataCorrectionLog> findByTargetAndDateRefactors(List<String> operationIds, List<String> listEmployeeId,
			DatePeriod period, TargetDataType targetDataType,
			int offset, int limit);
	
	//取得する
	List<DataCorrectionLog> getInfoLog(String sid, GeneralDate targetDate, Integer itemId, TargetDataType type);
}
