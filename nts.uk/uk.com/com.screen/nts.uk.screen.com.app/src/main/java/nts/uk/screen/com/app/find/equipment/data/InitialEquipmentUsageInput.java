package nts.uk.screen.com.app.find.equipment.data;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * UKDesign.UniversalK.オフィス.OEW_設備管理.OEW001_設備利用の入力.ユーザー固有情報.設備利用の入力の初期選択
 */
@Value
@AllArgsConstructor
public class InitialEquipmentUsageInput {

	/**
	 * 設備コード
	 */
	private String equipmentCode;
	
	/**
	 * 設備分類コード
	 */
	private String equipmentClsCode;
}
