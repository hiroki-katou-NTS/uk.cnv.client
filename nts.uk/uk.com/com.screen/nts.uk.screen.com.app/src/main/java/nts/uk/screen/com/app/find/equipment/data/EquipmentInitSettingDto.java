package nts.uk.screen.com.app.find.equipment.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.screen.com.app.find.equipment.achievement.EquipmentPerformInputFormatSettingDto;
import nts.uk.screen.com.app.find.equipment.achievement.EquipmentUsageRecordItemSettingDto;

@Data
@AllArgsConstructor
public class EquipmentInitSettingDto {

	/**
	 * 設備利用実績の項目設定<List>
	 */
	private List<EquipmentUsageRecordItemSettingDto> itemSettings;
	
	/**
	 * 設備の実績入力フォーマット設定<List>
	 */
	private EquipmentPerformInputFormatSettingDto formatSetting; 
}
