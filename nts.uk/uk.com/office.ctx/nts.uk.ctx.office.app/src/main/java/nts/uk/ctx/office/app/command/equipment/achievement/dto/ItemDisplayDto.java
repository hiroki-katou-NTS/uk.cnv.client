package nts.uk.ctx.office.app.command.equipment.achievement.dto;

import lombok.Builder;
import lombok.Data;
import nts.uk.ctx.office.dom.equipment.achievement.DisplayWidth;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.ItemDisplay;

@Data
@Builder
public class ItemDisplayDto {

	// 表示幅
	private int displayWidth;

	// 表示順番
	private int displayOrder;

	// 項目NO
	private String itemNo;

	public static ItemDisplayDto fromDomain(ItemDisplay domain) {
		return ItemDisplayDto.builder().displayOrder(domain.getDisplayOrder())
				.displayWidth(domain.getDisplayWidth().v()).itemNo(domain.getItemNo().v()).build();
	}
	
	public ItemDisplay toDomain() {
		return new ItemDisplay(new DisplayWidth(displayWidth), displayOrder, new EquipmentItemNo(itemNo));
	}
}
