package nts.uk.screen.com.app.find.equipment.achievement.ac;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.pub.equipment.achievement.export.EquipmentUsageRecordItemSettingExport;

@Data
@Builder
public class EquipmentUsageRecordItemSettingImport {

	// 会社ID
	private String cid;

	// 項目NO
	private String itemNo;

	// 項目入力制御
	private ItemInputControlImport inputControl;

	// 項目の表示
	private DisplayOfItemsImport items;

	public static EquipmentUsageRecordItemSettingImport fromExport(EquipmentUsageRecordItemSettingExport export) {
		return EquipmentUsageRecordItemSettingImport.builder().cid(export.getCid())
				.inputControl(ItemInputControlImport.fromExport(export.getInputControl())).itemNo(export.getItemNo())
				.items(DisplayOfItemsImport.fromExport(export.getItems())).build();
	}
}
