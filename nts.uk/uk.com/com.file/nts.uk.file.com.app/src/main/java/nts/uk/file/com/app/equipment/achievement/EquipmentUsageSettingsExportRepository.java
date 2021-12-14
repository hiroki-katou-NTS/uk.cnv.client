package nts.uk.file.com.app.equipment.achievement;

import nts.uk.file.com.app.equipment.achievement.ac.EquipmentUsageSettingsImport;

public interface EquipmentUsageSettingsExportRepository {

	// 設備利用実績設定のマスタ一覧を作成する
	EquipmentUsageSettingsImport findSettings();
}
