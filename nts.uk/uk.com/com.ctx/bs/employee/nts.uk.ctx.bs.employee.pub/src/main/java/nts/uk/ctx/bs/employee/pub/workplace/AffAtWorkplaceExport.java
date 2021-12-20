package nts.uk.ctx.bs.employee.pub.workplace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 所属職場履歴項目

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffAtWorkplaceExport {
	
	// 社員ID
	private String employeeId;

	/** The workplace id. */
	// 職場ID
	private String workplaceId;
	
	// 履歴ID
	private String historyID;
	
//	//通常職場ID
//	private String normalWorkplaceID;

}
