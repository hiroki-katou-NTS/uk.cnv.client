package nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem;

import java.util.List;
import java.util.Optional;


public interface MonthlyItemControlByAuthRepository {
	List<MonthlyItemControlByAuthority> getListMonthlyAttendanceItemAuthority(String companyId);
	
	Optional<MonthlyItemControlByAuthority> getMonthlyAttdItem(String companyID,String authorityMonthlyId);
	
	void updateMonthlyAttdItemAuth(MonthlyItemControlByAuthority monthlyItemControlByAuthority);
	
	void addMonthlyAttdItemAuth(MonthlyItemControlByAuthority monthlyItemControlByAuthority);
}
