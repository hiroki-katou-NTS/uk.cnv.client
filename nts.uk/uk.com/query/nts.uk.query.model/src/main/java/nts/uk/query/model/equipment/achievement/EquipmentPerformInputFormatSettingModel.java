package nts.uk.query.model.equipment.achievement;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EquipmentPerformInputFormatSettingModel {

	// 会社ID
	private String cid;

	// 項目表示設定
	private List<ItemDisplayModel> itemDisplaySettings;
}
