package nts.uk.query.model.equipment.achievement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisplayOfItemsModel {

	// 項目名称
	private String itemName;

	// 単位
	private String unit;

	// 説明
	private String memo;
}
