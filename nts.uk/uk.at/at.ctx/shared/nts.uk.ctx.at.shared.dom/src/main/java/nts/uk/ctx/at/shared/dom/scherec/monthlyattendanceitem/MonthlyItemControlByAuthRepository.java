package nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem;

import java.util.List;
import java.util.Optional;


public interface MonthlyItemControlByAuthRepository {
	List<MonthlyItemControlByAuthority> getListMonthlyAttendanceItemAuthority(String companyId);
	
	List<String> getMonthlytRolesByCid(String companyId);
	
	Optional<MonthlyItemControlByAuthority> getMonthlyAttdItem(String companyID,String authorityMonthlyId);
	
	void updateMonthlyAttdItemAuth(MonthlyItemControlByAuthority monthlyItemControlByAuthority);
	
	void addMonthlyAttdItemAuth(MonthlyItemControlByAuthority monthlyItemControlByAuthority);
	
	Optional<MonthlyItemControlByAuthority> getMonthlyAttdItemByUse(String companyID,String authorityMonthlyId,List<Integer> itemMonthlyID,int toUse);
	
	/**
	 * 権限別月次項目制御
	 */
	Optional<MonthlyItemControlByAuthority> getMonthlyAttdItemByAttItemId(String companyID, String authorityMonthlyId,
			List<Integer> attendanceItemIds);
			
	Optional<MonthlyItemControlByAuthority> getAllMonthlyAttdItemByUse(String companyID,String authorityMonthlyId, int toUse);
	
	// 複写先のドメインモデルを削除する
	void delete(String companyId, String roleId);
}
