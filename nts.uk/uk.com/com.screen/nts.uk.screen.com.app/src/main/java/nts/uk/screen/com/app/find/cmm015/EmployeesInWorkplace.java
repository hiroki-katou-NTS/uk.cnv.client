package nts.uk.screen.com.app.find.cmm015;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EmployeesInWorkplace {

	// 社員ID
	private String employeeID;
	
	// 社員CD
	private String employeeCD;
	
	// 社員名称
	private String employeeName;
	
	// 職位ID
	private String jobtitleID;
	
	// 職位名
	private String jobtitle;
	
	// 並び順（序列）
	private Integer order;
	
}
