package nts.uk.query.model.equipment.achievement;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentUsageSettingsModel {

	// LIST＜設備利用実績の項目設定＞
	private List<EquipmentUsageRecordItemSettingModel> itemSettings;
	
	// 設備の実績入力フォーマット設定.項目表示設定
	private EquipmentPerformInputFormatSettingModel formatSetting;
}
