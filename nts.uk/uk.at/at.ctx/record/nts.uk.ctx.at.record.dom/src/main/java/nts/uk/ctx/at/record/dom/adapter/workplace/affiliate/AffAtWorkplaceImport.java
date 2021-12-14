package nts.uk.ctx.at.record.dom.adapter.workplace.affiliate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Import所属職場履歴項目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffAtWorkplaceImport {
	// 社員ID
	private String employeeId;

	/** The workplace id. */
	// 職場ID
	private String workplaceId;

	// 履歴ID
	private String historyID;

	// 通常職場ID
	private String normalWorkplaceID;
}
