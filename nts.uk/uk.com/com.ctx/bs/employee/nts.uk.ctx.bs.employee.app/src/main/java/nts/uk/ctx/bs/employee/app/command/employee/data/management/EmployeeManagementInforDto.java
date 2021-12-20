package nts.uk.ctx.bs.employee.app.command.employee.data.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmployeeManagementInforDto {
	
	private String companyId;
	
	private String personalId;
	
	private String employeeId;
	
	private String employeeCode;
	
}
