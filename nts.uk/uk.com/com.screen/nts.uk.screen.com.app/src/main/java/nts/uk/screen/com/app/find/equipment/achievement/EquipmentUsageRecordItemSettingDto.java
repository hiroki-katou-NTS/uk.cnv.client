package nts.uk.screen.com.app.find.equipment.achievement;

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

	public static EquipmentUsageRecordItemSettingDto fromDomain(EquipmentUsageRecordItemSetting domain) {
		return EquipmentUsageRecordItemSettingDto.builder().cid(domain.getCid())
				.inputControl(ItemInputControlDto.fromDomain(domain.getInputcontrol())).itemNo(domain.getItemNo().v())
				.items(DisplayOfItemsDto.fromDomain(domain.getItems())).build();
	}
	
	public EquipmentUsageRecordItemSetting toDomain() {
		return new EquipmentUsageRecordItemSetting(cid, new EquipmentItemNo(itemNo), inputControl.toDomain(),
				items.toDomain());
	}
}
