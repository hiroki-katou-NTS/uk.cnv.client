package nts.uk.query.model.equipment.achievement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDisplayModel {

	// 表示幅
	private int displayWidth;

	// 表示順番
	private int displayOrder;

	// 項目NO
	private String itemNo;
}
