package nts.uk.ctx.bs.employee.pub.workplace;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 所属職場履歴項目
 * @author lanlt
 *
 */
@Data
@AllArgsConstructor
public class AffWorkplaceHistoryItemExport {
	/** The history Id. */
	// 履歴ID
	private String historyId;
	/** The workplaceId. */
	// 職場ID
	private String  workplaceId;
	
//	/** The normalWorkplaceCode. */
//	// 通常職場コード
//	private String  normalWorkplaceId;
}
