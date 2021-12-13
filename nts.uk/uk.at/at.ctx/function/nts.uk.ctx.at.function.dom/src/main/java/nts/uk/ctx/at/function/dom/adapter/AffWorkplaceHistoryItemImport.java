package nts.uk.ctx.at.function.dom.adapter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AffWorkplaceHistoryItemImport {
	/** The history Id. */
	// 履歴ID
	private String historyId;
	/** The workplaceId. */
	// 職場ID
	private String  workplaceId;
	
//	/** The normalWorkplaceCode. */
//	// 通常職場コード
//	private String  normalWorkplaceId;

	public AffWorkplaceHistoryItemImport(String historyId, String workplaceId) {
		super();
		this.historyId = historyId;
		this.workplaceId = workplaceId;
	}
	
}
