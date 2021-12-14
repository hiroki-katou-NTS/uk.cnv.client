package nts.uk.ctx.office.dom.equipment.data;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentItemNo;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備利用実績データ.実績データ
 */
@Data
@AllArgsConstructor
public class ResultData {

	/**
	 * 項目NO
	 */
	private EquipmentItemNo itemNo;

	/**
	 * 項目分類
	 */
	private ItemClassification itemClassification;

	/**
	 * 項目値
	 */
	private Optional<ActualItemUsageValue> actualValue;

	/**
	 * [C-1] 新規追加
	 * 
	 * @param require
	 * @param cid             会社ID
	 * @param equipmentItemNo 項目NO
	 * @param actualValue     項目値
	 * @return 実績データ
	 */
	public static ResultData createData(Require require, String cid, EquipmentItemNo equipmentItemNo,
			ActualItemUsageValue actualValue) {
		// $項目設定 = require.設備利用実績の項目設定を取得する(会社ID、項目NO)
		Optional<EquipmentUsageRecordItemSetting> optItemSetting = require.getItemSetting(cid, equipmentItemNo.v());
		// $項目分類　＝　$項目設定.項目入力制御.項目分類
		if (optItemSetting.isPresent()) {
			// $項目分類　＝　$項目設定.項目入力制御.項目分類
			ItemClassification itemClassification = optItemSetting.get().getInputcontrol().getItemCls();
			// return　$実績データ　＝　実績データ#実績データ(項目NO、$項目分類、項目値)
			return new ResultData(equipmentItemNo, itemClassification, Optional.ofNullable(actualValue));
		}
		return null;
	}
	
	/**
	 * [1] 項目値を変更する
	 * @param itemValues	項目データList
	 */
	public void updateValue(List<ItemData> itemValues) {
		// $項目値　=　項目データList　：　							
		//	filter　$.項目NO　＝＝　@項目NO	
		//	map　$.項目値	
		Optional<ActualItemUsageValue> optActualValue = itemValues.stream()
				.filter(data -> this.itemNo.equals(data.getItemNo()))
				.findFirst().map(ItemData::getActualValue);
		// @項目値　＝　$項目値
		this.actualValue = optActualValue;
	}

	public static interface Require {

		/**
		 * [R-1] 設備利用実績の項目設定を取得する
		 */
		Optional<EquipmentUsageRecordItemSetting> getItemSetting(String cid, String itemNo);
	}
}
