package nts.uk.ctx.sys.auth.dom.adapter.workplace;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AffiliationWorkplace {
	/** The history Id. */
	// 履歴ID
	private String historyId;
	
	/** The Employee Id. */
	// 社員ID
	private String employeeId;
	
	/** The workplaceId. */
	// 職場ID
	private String  workplaceId;
	
//	/** The normalWorkplaceCode. */
//	// 通常職場コード
//	private String  normalWorkplaceId;

	public AffiliationWorkplace(String historyId, String employeeId, String workplaceId) {
		super();
		this.historyId = historyId;
		this.employeeId = employeeId;
		this.workplaceId = workplaceId;
	}
	
	
}
