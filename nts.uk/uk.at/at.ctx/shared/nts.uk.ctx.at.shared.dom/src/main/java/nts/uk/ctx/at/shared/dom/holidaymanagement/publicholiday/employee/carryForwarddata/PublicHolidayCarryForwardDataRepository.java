package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;

import java.util.List;
import java.util.Optional;



public interface PublicHolidayCarryForwardDataRepository {

	/**
	 * 検索
	 *
	 * @param employeeId the employeeId
	 * @return the optional
	 */
	 Optional<PublicHolidayCarryForwardData> get(String employeeId);
	 
	 
	 
	 List<PublicHolidayCarryForwardData> getAll(List<String>employeeIds);
		
	/**
	 * 登録および更新
	 *
	 * @param domain the domain
	 */
	void persistAndUpdate(PublicHolidayCarryForwardData domain);
	
	
	
	void addAll(List<PublicHolidayCarryForwardData> domains);
	
	
	void updateAll(List<PublicHolidayCarryForwardData> domains);
	
	/**
	 * 削除
	 * @param domain the domain
	 */
	void remove(PublicHolidayCarryForwardData domain); 
	
	
	/**
	 * 社員の公休繰越データ削除
	 * @param employeeId
	 */
	void delete(String employeeId);
	
	
	
}
