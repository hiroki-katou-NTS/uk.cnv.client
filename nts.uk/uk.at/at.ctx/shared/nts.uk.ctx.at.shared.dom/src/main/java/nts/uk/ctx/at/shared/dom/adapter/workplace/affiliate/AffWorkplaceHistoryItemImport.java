package nts.uk.ctx.at.shared.dom.adapter.workplace.affiliate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 所属職場履歴項目Imported
 */
@Getter
@AllArgsConstructor
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

//	private Optional<String> workLocationCode;

}
