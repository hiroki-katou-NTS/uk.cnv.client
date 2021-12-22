package nts.uk.ctx.bs.employee.pub.workplace;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
public class AffWorkplaceHistoryItemExport3 {

	/** The history Id. */
	// 履歴ID
	private String historyId;
	
	/** The Employee Id. */
	// 社員ID
	private String employeeId;
	
	/** The workplaceId. */
	// 職場ID
	private String  workplaceId;
	
	/** The normalWorkplaceCode. */
	// 通常職場コード
	private String  normalWorkplaceId;

//	private Optional<String> workLocationCode;
	
	public AffWorkplaceHistoryItemExport3(String historyId, String employeeId, String workplaceId,
			String normalWorkplaceId) {
		this.historyId   = historyId;
		this.employeeId  = employeeId;
		this.workplaceId = workplaceId;
		this.normalWorkplaceId = normalWorkplaceId;
//		this.workLocationCode  = workLocation == null? Optional.empty(): Optional.of(workLocation);
	}
}
