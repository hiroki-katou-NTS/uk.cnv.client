package nts.uk.screen.com.app.find.equipment.achievement;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import nts.uk.ctx.office.dom.equipment.achievement.DisplayOfItems;
import nts.uk.ctx.office.dom.equipment.achievement.ItemDescription;
import nts.uk.ctx.office.dom.equipment.achievement.UsageItemName;
import nts.uk.ctx.office.dom.equipment.achievement.UsageRecordUnit;

@Data
@Builder
public class DisplayOfItemsDto {

	// 項目名称
	private String itemName;

	// 単位
	private String unit;

	// 説明
	private String memo;

	public static DisplayOfItemsDto fromDomain(DisplayOfItems domain) {
		return DisplayOfItemsDto.builder().itemName(domain.getItemName().v())
				.memo(domain.getMemo().map(ItemDescription::v).orElse(null))
				.unit(domain.getUnit().map(UsageRecordUnit::v).orElse(null)).build();
	}
	
	public DisplayOfItems toDomain() {
		return new DisplayOfItems(new UsageItemName(itemName), Optional.ofNullable(unit).map(UsageRecordUnit::new),
				Optional.ofNullable(memo).map(ItemDescription::new));
	}
}
