package nts.uk.ctx.bs.employee.dom.workplace;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeAffiliationHelper {
	
	/**
	 * 社員IDリストを指定して社員の所属組織を作成する
	 * @param employeeIdList 社員IDリスト
	 * @return List<社員の所属組織>
	 */
	public static List<EmployeeAffiliation> createListWithEmployeeIds (String... employeeIdList) {
		
		return Arrays.asList(employeeIdList).stream()
				.map(empId -> createWithEmloyeeId(empId))
				.collect(Collectors.toList());
	}
	
	/**
	 * 社員IDを指定して社員の所属組織を作成する
	 * @param employeeId 社員ID
	 * @return 社員の所属組織
	 */
	public static EmployeeAffiliation createWithEmloyeeId(String employeeId) {
		
		return EmployeeAffiliation.createWithoutInfo(
				employeeId, 
				"wpl-id", 
				"wpl-group-id" );
	}
	
	

}
