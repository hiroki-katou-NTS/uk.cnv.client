package nts.uk.screen.com.app.find.equipment.achievement.ac;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.pub.equipment.achievement.export.ItemDisplayExport;

@Data
@Builder
public class ItemDisplayImport {

	// 表示幅
	private int displayWidth;

	// 表示順番
	private int displayOrder;

	// 項目NO
	private String itemNo;

	public static ItemDisplayImport fromExport(ItemDisplayExport export) {
		return ItemDisplayImport.builder().displayOrder(export.getDisplayOrder()).displayWidth(export.getDisplayWidth())
				.itemNo(export.getItemNo()).build();
	}
}
