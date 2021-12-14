package nts.uk.query.pub.equipment.achievement.export;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.query.model.equipment.achievement.EquipmentUsageSettingsModel;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentUsageSettingsExport {

	// LIST＜設備利用実績の項目設定＞
	private List<EquipmentUsageRecordItemSettingExport> itemSettings;

	// 設備の実績入力フォーマット設定.項目表示設定
	private EquipmentPerformInputFormatSettingExport formatSetting;

	public static EquipmentUsageSettingsExport fromModel(EquipmentUsageSettingsModel model) {
		if (model == null) {
			return new EquipmentUsageSettingsExport(
					new ArrayList<>(),
					EquipmentPerformInputFormatSettingExport.fromModel(null)
			);
		}
		return new EquipmentUsageSettingsExport(
				model.getItemSettings().stream().map(EquipmentUsageRecordItemSettingExport::fromModel)
						.collect(Collectors.toList()),
				EquipmentPerformInputFormatSettingExport.fromModel(model.getFormatSetting()));
	}
}
