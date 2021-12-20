/**
 * 
 */
package nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * 社員単価履歴Repository
 * @author laitv
 *
 */
public interface EmployeeUnitPriceHistoryRepository {

	// [1] 社員単価履歴項目get(社員ID, 年月日)
	Optional<EmployeeUnitPriceHistoryItem> getByEmployeeIdAndBaseDate(String sid, GeneralDate baseDate);
	
	Optional<EmployeeUnitPriceHistory> getHistByEmployeeIdAndBaseDate(String sid, GeneralDate baseDate);
	
	Optional<EmployeeUnitPriceHistory> getHistByHistId(String histId);
	
	Optional<EmployeeUnitPriceHistory> getBySid(String cid, String sid);
	
	Optional<EmployeeUnitPriceHistory> getBySidDesc(String cid, String sid);
	
	List<EmployeeUnitPriceHistory> getBySidsAndCid(List<String> employeeIds, String cid);
	
	List<EmployeeUnitPriceHistory> getBySidsAndCidAndBaseDate(List<String> employeeIds, String cid, GeneralDate baseDate);
	
	void add(EmployeeUnitPriceHistory employeeUnitPriceHistory);
	
	void add(String sid, DateHistoryItem domain);
	
	void update(EmployeeUnitPriceHistory employeeUnitPriceHistory);
	
	void update(DateHistoryItem itemToBeUpdated);
	
	void delete(String companyId, String empId , String historyId);
	
}
