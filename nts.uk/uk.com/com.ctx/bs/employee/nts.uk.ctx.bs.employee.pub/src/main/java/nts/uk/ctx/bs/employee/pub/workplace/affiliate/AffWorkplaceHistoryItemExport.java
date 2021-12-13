package nts.uk.ctx.bs.employee.pub.workplace.affiliate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 所属職場履歴項目Exported
 */
@Getter
@AllArgsConstructor
public class AffWorkplaceHistoryItemExport {

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
