package nts.uk.query.model.equipment.achievement;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EquipmentUsageRecordItemSettingModel {

	// 会社ID
	private String cid;

	// 項目NO
	private String itemNo;

	// 項目入力制御
	private ItemInputControlModel inputControl;

	// 項目の表示
	private DisplayOfItemsModel items;
}
