package nts.uk.file.com.app.equipment.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.file.com.app.equipment.data.dto.EquipmentPerformInputFormatSettingDto;
import nts.uk.file.com.app.equipment.data.dto.EquipmentUsageRecordItemSettingDto;

@Data
@AllArgsConstructor
public class EquipmentDataQuery {

	/**
	 * Optional<設備分類コード>
	 */
	private String equipmentClsCode;
	
	/**
	 * Optional<設備コード>
	 */
	private String equipmentCode;
	
	/**
	 * 年月
	 */
	private int ym;
	
	/**
	 * 設備利用実績の項目設定<List>
	 */
	private List<EquipmentUsageRecordItemSettingDto> itemSettings;
	
	/**
	 * 設備の実績入力フォーマット設定<List>
	 */
	private EquipmentPerformInputFormatSettingDto formatSetting;
	
	private int printType;
}
