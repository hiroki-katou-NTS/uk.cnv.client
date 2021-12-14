package nts.uk.file.com.app.equipment.data.dto;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.office.dom.equipment.achievement.DigitsNumber;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.ItemInputControl;
import nts.uk.ctx.office.dom.equipment.achievement.MaximumUsageRecord;
import nts.uk.ctx.office.dom.equipment.achievement.MinimumUsageRecord;

@Data
@Builder
public class ItemInputControlDto {

	// 項目分類
	private int itemCls;

	// 必須
	private boolean require;

	// 桁数
	private Integer digitsNo;

	// 最大値
	private Integer maximum;

	// 最小値
	private Integer minimum;

	public ItemInputControl toDomain() {
		return new ItemInputControl(EnumAdaptor.valueOf(itemCls, ItemClassification.class), require,
				Optional.ofNullable(digitsNo).map(DigitsNumber::new),
				Optional.ofNullable(maximum).map(MaximumUsageRecord::new),
				Optional.ofNullable(minimum).map(MinimumUsageRecord::new));
	}
}
