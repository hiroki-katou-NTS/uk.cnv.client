package nts.uk.query.pub.equipment.achievement.export;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.model.equipment.achievement.DisplayOfItemsModel;

@Data
@Builder
public class DisplayOfItemsExport {

	// 項目名称
	private String itemName;

	// 単位
	private String unit;

	// 説明
	private String memo;

	public static DisplayOfItemsExport fromModel(DisplayOfItemsModel model) {
		return DisplayOfItemsExport.builder().itemName(model.getItemName())
				.memo(model.getMemo()).unit(model.getUnit()).build();
	}
}
