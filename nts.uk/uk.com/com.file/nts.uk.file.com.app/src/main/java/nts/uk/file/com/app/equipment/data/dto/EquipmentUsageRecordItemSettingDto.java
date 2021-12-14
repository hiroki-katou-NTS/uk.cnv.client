package nts.uk.file.com.app.equipment.data.dto;

import lombok.Builder;
import lombok.Data;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;

@Data
@Builder
public class EquipmentUsageRecordItemSettingDto {

	// 会社ID
	private String cid;

	// 項目NO
	private String itemNo;

	// 項目入力制御
	private ItemInputControlDto inputControl;

	// 項目の表示
	private DisplayOfItemsDto items;

	public EquipmentUsageRecordItemSetting toDomain() {
		return new EquipmentUsageRecordItemSetting(cid, new EquipmentItemNo(itemNo), inputControl.toDomain(),
				items.toDomain());
	}
}
