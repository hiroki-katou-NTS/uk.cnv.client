package nts.uk.ctx.bs.employee.pub.workplace.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkplaceHistoryItemExport {

	private String historyId;
	
	private String employeeId;
	
	private String  workplaceId;

//	private String  normalWorkplaceId;

//	private Optional<String> workLocationCode;
}
