package nts.uk.file.com.app.equipment.data.dto;

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

	public DisplayOfItems toDomain() {
		return new DisplayOfItems(new UsageItemName(itemName), Optional.ofNullable(unit).map(UsageRecordUnit::new),
				Optional.ofNullable(memo).map(ItemDescription::new));
	}
}
