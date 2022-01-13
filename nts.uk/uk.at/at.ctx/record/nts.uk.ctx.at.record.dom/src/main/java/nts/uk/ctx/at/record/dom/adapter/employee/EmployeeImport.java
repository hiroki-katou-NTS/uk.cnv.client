package nts.uk.ctx.at.record.dom.adapter.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author chungnt
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeImport {
	
	/** The company id. */
	private String companyID;

	/** The employee code. */
	private String employeeCD;

	/** The employee id. */
	private String employeeID;

	/** The person Id. */
	private String personID;
	
	private String perName;
}