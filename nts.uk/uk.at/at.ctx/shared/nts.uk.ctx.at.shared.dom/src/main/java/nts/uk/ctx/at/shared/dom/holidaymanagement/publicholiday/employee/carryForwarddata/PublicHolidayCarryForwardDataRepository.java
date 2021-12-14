package nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata;

import java.util.List;

import nts.arc.time.YearMonth;


public interface PublicHolidayCarryForwardDataRepository {

	/**
	 * 検索
	 *
	 * @param employeeId the employeeId
	 * @return the optional
	 */
	List<PublicHolidayCarryForwardData> get(String employeeId);
		
	/**
	 * 登録および更新
	 *
	 * @param domain the domain
	 */
	void persistAndUpdate(PublicHolidayCarryForwardData domain);
	
	/**
	 * 削除
	 * @param domain the domain
	 */
	void remove(PublicHolidayCarryForwardData domain); 
	
	/**
	 * 当月以降を削除
	 * @param employeeId
	 * @param yearMonth
	 */
	void deleteThisMonthAfter(String employeeId, YearMonth yearMonth);
	
	
	/**
	 * 社員の公休繰越データ全て削除
	 * @param employeeId
	 */
	void delete(String employeeId);
	
	
	
}
