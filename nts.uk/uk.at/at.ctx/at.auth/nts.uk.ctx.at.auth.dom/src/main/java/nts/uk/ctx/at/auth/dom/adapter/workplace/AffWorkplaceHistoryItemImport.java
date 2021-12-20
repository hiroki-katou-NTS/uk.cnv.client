package nts.uk.ctx.at.auth.dom.adapter.workplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class AffWorkplaceHistoryItem.
 */
// 所属職場履歴項目
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AffWorkplaceHistoryItemImport {

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
	
	public static AffWorkplaceHistoryItemImport createFromJavaType(String histId, String employeeId, String workplaceId){
		return new AffWorkplaceHistoryItemImport(histId,employeeId, workplaceId);

	}
	
}
