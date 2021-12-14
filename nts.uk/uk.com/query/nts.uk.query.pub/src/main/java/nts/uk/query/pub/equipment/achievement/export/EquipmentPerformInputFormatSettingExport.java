package nts.uk.query.pub.equipment.achievement.export;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.query.model.equipment.achievement.EquipmentPerformInputFormatSettingModel;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentPerformInputFormatSettingExport {

	// 会社ID
	private String cid;

	// 項目表示設定
	private List<ItemDisplayExport> itemDisplaySettings;

	public static EquipmentPerformInputFormatSettingExport fromModel(EquipmentPerformInputFormatSettingModel model) {
		if (model == null) {
			return new EquipmentPerformInputFormatSettingExport(
					null,
					new ArrayList<ItemDisplayExport>()
			);
		}
		return new EquipmentPerformInputFormatSettingExport(model.getCid(),
				model.getItemDisplaySettings().stream().map(ItemDisplayExport::fromModel).collect(Collectors.toList()));
	}
}
