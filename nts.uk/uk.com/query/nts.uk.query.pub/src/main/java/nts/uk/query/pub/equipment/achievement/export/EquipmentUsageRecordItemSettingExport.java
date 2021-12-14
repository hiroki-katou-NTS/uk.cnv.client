package nts.uk.query.pub.equipment.achievement.export;

import lombok.Builder;
import lombok.Data;
import nts.uk.query.model.equipment.achievement.EquipmentUsageRecordItemSettingModel;

@Data
@Builder
public class EquipmentUsageRecordItemSettingExport {

	// 会社ID
	private String cid;

	// 項目NO
	private String itemNo;

	// 項目入力制御
	private ItemInputControlExport inputControl;

	// 項目の表示
	private DisplayOfItemsExport items;

	public static EquipmentUsageRecordItemSettingExport fromModel(EquipmentUsageRecordItemSettingModel model) {
		return EquipmentUsageRecordItemSettingExport.builder().cid(model.getCid())
				.inputControl(ItemInputControlExport.fromModel(model.getInputControl())).itemNo(model.getItemNo())
				.items(DisplayOfItemsExport.fromModel(model.getItems())).build();
	}
}
