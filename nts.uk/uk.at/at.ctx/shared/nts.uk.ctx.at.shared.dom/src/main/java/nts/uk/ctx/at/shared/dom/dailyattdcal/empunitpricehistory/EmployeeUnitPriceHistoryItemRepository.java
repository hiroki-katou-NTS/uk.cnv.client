/**
 * 
 */
package nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * 社員単価履歴項目Repository
 * @author laitv
 */
public interface EmployeeUnitPriceHistoryItemRepository {
	
	// [1] 社員単価履歴項目get(社員ID, 年月日)
	Optional<EmployeeUnitPriceHistoryItem> getByEmployeeIdAndBaseDate(String sid, GeneralDate baseDate);
	
	Optional<EmployeeUnitPriceHistoryItem> getByHistoryId(String histId);
	
	List<EmployeeUnitPriceHistoryItem> getByHistIdList(List<String> histIdList);
	
	void add(EmployeeUnitPriceHistoryItem employeeUnitPriceHistoryItem);
	
	void update(EmployeeUnitPriceHistoryItem employeeUnitPriceHistoryItem);

	void delete(String companyId, String empId , String historyId); 
}
