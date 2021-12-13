package nts.uk.ctx.at.function.dom.adapter;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkplaceHistoryItemImport {
	
	private String historyId;
	
	private String employeeId;

	private String  workplaceId;

//	private String  normalWorkplaceId;
	
	/** 勤務場所コード */
//	private Optional<String> workLocationCode;

	public WorkplaceHistoryItemImport(String historyId, String employeeId, String workplaceId) {
		this.historyId = historyId;
		this.employeeId = employeeId;
		this.workplaceId = workplaceId;
//		this.normalWorkplaceId = normalWorkplaceId;
//		this.workLocationCode = workLocationCode;
	}
}
