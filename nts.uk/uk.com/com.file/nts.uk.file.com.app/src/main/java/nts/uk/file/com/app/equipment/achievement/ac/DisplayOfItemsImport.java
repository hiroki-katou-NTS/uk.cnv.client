package nts.uk.file.com.app.equipment.achievement.ac;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.pub.equipment.achievement.export.DisplayOfItemsExport;

@Data
@Builder
public class DisplayOfItemsImport {

	// 項目名称
	private String itemName;

	// 単位
	private String unit;

	// 説明
	private String memo;

	public static DisplayOfItemsImport fromExport(DisplayOfItemsExport export) {
		return DisplayOfItemsImport.builder().itemName(export.getItemName())
				.memo(export.getMemo()).unit(export.getUnit()).build();
	}
}
