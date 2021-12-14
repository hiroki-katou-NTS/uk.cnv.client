package nts.uk.file.com.app.equipment.achievement;

import java.time.Duration;
import java.time.LocalTime;

import lombok.Builder;
import lombok.Data;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.file.com.app.equipment.achievement.ac.EquipmentUsageRecordItemSettingImport;
import nts.uk.file.com.app.equipment.achievement.ac.ItemDisplayImport;
import nts.uk.shr.com.i18n.TextResource;

@Data
@Builder
public class EquipmentUsageSettingsDataSource {

	private static final String[] ITEM_TYPE_RESOURCE_IDS = {
		"OEM003_26",
		"OEM003_27",
		"OEM003_28",
		"OEM003_29",
		"OEM003_30",
		"OEM003_31",
		"OEM003_32",
		"OEM003_33",
		"OEM003_34"
	};
	private static final String TRUE_SYMBOL = "○";
	private static final String NONE_SYMBOL = "－";

	// NO
	private int displayNo;

	// 項目名
	private String itemName;

	// 表示幅
	private int displayWidth;

	// 選択項目
	private String itemType;

	// 最小
	private String minimum;

	// 最大
	private String maximum;

	// 単位
	private String unit;

	// 必須
	private String required;

	// 項目の説明
	private String memo;

	public static EquipmentUsageSettingsDataSource createDataSource(ItemDisplayImport itemDisplay,
			EquipmentUsageRecordItemSettingImport itemSetting) {
		int displayNo = itemDisplay.getDisplayOrder();
		String itemName = itemSetting.getItems().getItemName();
		int displayWidth = itemDisplay.getDisplayWidth();
		String itemType = ITEM_TYPE_RESOURCE_IDS[Integer.valueOf(itemSetting.getItemNo()) - 1];
		String minimum = null, maximum = null, unit = null;
		switch (EnumAdaptor.valueOf(itemSetting.getInputControl().getItemCls(), ItemClassification.class)) {
		case TEXT:
			minimum = NONE_SYMBOL;
			maximum = String.format("%,d", itemSetting.getInputControl().getDigitsNo());
			unit = itemSetting.getItems().getUnit();
			break;
		case NUMBER:
			minimum = String.format("%,d", itemSetting.getInputControl().getMinimum());
			maximum = String.format("%,d", itemSetting.getInputControl().getMaximum());
			unit = itemSetting.getItems().getUnit();
			break;
		case TIME:
			minimum = LocalTime.MIN.plus(Duration.ofMinutes(itemSetting.getInputControl().getMinimum())).toString();
			maximum = LocalTime.MIN.plus(Duration.ofMinutes(itemSetting.getInputControl().getMaximum())).toString();
			unit = NONE_SYMBOL;
			break;
		}
		String required = itemSetting.getInputControl().isRequire() ? TRUE_SYMBOL : NONE_SYMBOL;
		String memo = itemSetting.getItems().getMemo();
		return EquipmentUsageSettingsDataSource.builder()
				.displayNo(displayNo)
				.displayWidth(displayWidth)
				.itemName(itemName)
				.itemType(TextResource.localize(itemType))
				.maximum(maximum)
				.minimum(minimum)
				.memo(memo)
				.required(required)
				.unit(unit)
				.build();
	}
}
