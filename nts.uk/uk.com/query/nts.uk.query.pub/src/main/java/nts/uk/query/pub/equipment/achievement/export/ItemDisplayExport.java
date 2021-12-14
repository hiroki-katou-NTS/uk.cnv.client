package nts.uk.query.pub.equipment.achievement.export;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.model.equipment.achievement.ItemDisplayModel;

@Data
@Builder
public class ItemDisplayExport {

	// 表示幅
	private int displayWidth;

	// 表示順番
	private int displayOrder;

	// 項目NO
	private String itemNo;

	public static ItemDisplayExport fromModel(ItemDisplayModel model) {
		return ItemDisplayExport.builder().displayOrder(model.getDisplayOrder()).displayWidth(model.getDisplayWidth())
				.itemNo(model.getItemNo()).build();
	}
}
