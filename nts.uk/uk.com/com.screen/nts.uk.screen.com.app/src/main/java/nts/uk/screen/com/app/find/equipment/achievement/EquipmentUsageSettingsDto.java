package nts.uk.screen.com.app.find.equipment.achievement;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.screen.com.app.find.equipment.achievement.ac.EquipmentPerformInputFormatSettingImport;
import nts.uk.screen.com.app.find.equipment.achievement.ac.EquipmentUsageRecordItemSettingImport;

@Data
@AllArgsConstructor
public class EquipmentUsageSettingsDto {
	
	// LIST＜設備利用実績の項目設定＞
	private List<EquipmentUsageRecordItemSettingImport> itemSettings;

	// 設備の実績入力フォーマット設定.項目表示設定
	private EquipmentPerformInputFormatSettingImport formatSetting;
	
	// 設備帳票設定
	private EquipmentFormSettingDto formSetting;
}
