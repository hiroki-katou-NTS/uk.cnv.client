package nts.uk.file.com.app.equipment.achievement.ac;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.query.pub.equipment.achievement.export.EquipmentUsageSettingsExport;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EquipmentUsageSettingsImport {

	// LIST＜設備利用実績の項目設定＞
	private List<EquipmentUsageRecordItemSettingImport> itemSettings;

	// 設備の実績入力フォーマット設定.項目表示設定
	private EquipmentPerformInputFormatSettingImport formatSetting;

	public static EquipmentUsageSettingsImport fromExport(EquipmentUsageSettingsExport export) {
		return new EquipmentUsageSettingsImport(
				export.getItemSettings().stream().map(EquipmentUsageRecordItemSettingImport::fromExport)
						.collect(Collectors.toList()),
				EquipmentPerformInputFormatSettingImport.fromExport(export.getFormatSetting()));
	}
}
