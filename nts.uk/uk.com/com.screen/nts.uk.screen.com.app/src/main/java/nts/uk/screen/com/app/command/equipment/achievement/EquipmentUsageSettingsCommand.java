package nts.uk.screen.com.app.command.equipment.achievement;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.office.app.command.equipment.achievement.dto.EquipmentFormSettingDto;
import nts.uk.ctx.office.app.command.equipment.achievement.dto.EquipmentPerformInputFormatSettingDto;
import nts.uk.ctx.office.app.command.equipment.achievement.dto.EquipmentUsageRecordItemSettingDto;

@Value
public class EquipmentUsageSettingsCommand {
	
	/**
	 * 設備帳票設定
	 */
	private EquipmentFormSettingDto formSetting;
	
	/**
	 * 設備利用実績の項目設定(List)
	 */
	private List<EquipmentUsageRecordItemSettingDto> itemSettings;
	
	/**
	 * 設備の実績入力フォーマット設定
	 */
	private EquipmentPerformInputFormatSettingDto formatSetting;
}
