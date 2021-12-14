package nts.uk.file.com.app.equipment.data.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentPerformInputFormatSetting;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentPerformInputFormatSettingDto {

	// 会社ID
	private String cid;

	// 項目表示設定
	private List<ItemDisplayDto> itemDisplaySettings;

	public EquipmentPerformInputFormatSetting toDomain() {
		return new EquipmentPerformInputFormatSetting(cid,
				itemDisplaySettings.stream().map(ItemDisplayDto::toDomain).collect(Collectors.toList()));
	}
}
