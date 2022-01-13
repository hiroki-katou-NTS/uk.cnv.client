package nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AffWorkplaceHistoryItem {
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

