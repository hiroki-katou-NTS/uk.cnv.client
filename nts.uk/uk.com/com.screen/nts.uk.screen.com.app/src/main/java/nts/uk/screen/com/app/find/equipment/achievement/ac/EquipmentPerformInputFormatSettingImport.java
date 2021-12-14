package nts.uk.screen.com.app.find.equipment.achievement.ac;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.query.pub.equipment.achievement.export.EquipmentPerformInputFormatSettingExport;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentPerformInputFormatSettingImport {

	// 会社ID
	private String cid;

	// 項目表示設定
	private List<ItemDisplayImport> itemDisplaySettings;

	public static EquipmentPerformInputFormatSettingImport fromExport(EquipmentPerformInputFormatSettingExport export) {
		return new EquipmentPerformInputFormatSettingImport(export.getCid(),
				export.getItemDisplaySettings().stream().map(ItemDisplayImport::fromExport).collect(Collectors.toList()));
	}
}
