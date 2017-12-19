package nts.uk.ctx.bs.employee.dom.temporaryabsence;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * @author danpv
 *
 */
public interface TempAbsHistRepository {

	// -----------------------------GET HISTORY ----------------------------------
	/**
	 * Get TemporaryAbsenceHist by employeeId
	 * @param employeeId
	 * @param histId
	 * @return
	 */
	Optional<TempAbsenceHistory> getByEmployeeId(String employeeId);
	/**
	 * Get TemporaryAbsenceHist by histId
	 * @param employeeId
	 * @param histId
	 * @return
	 */
	Optional<DateHistoryItem> getByHistId( String histId);
	
	/**
	 * get a TemporaryAbsenceHist with employeeId and standard date
	 * @param employeeId
	 * @param referenceDate
	 * @return
	 */
	Optional<DateHistoryItem> getItemByEmpIdAndStandardDate(String employeeId, GeneralDate standardDate);
	
	// ------------------------------ COMMAND HISTORY---------------------------------
	/**
	 * ドメインモデル「休職休業」を新規登録する
	 * @param item
	 * @param sid
	 */
	void add(String cid, String sid, DateHistoryItem item);
	/**
	 * 取得した「休職休業」を更新する
	 * @param domain
	 */
	void update(DateHistoryItem domain);
	
	/**
	 * ドメインモデル「休職休業」を削除する
	 * @param histId
	 */
	void delete(String histId);
	
}
