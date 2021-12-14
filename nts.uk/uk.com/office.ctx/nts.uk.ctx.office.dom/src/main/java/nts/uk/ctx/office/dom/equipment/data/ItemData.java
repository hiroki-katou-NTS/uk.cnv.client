package nts.uk.ctx.office.dom.equipment.data;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.項目データ
 */
@Value
@AllArgsConstructor
public class ItemData {

	/**
	 * 項目NO
	 */
	private EquipmentItemNo itemNo;
	
	/**
	 * 項目値
	 */
	private ActualItemUsageValue actualValue;
}
