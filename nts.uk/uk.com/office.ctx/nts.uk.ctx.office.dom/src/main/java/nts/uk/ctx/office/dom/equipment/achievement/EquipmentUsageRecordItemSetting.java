package nts.uk.ctx.office.dom.equipment.achievement;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.設備利用実績の項目設定
 * @author NWS-DungDv
 *
 */
@Getter
@AllArgsConstructor
public class EquipmentUsageRecordItemSetting extends AggregateRoot {

	// 会社ID
	@NotNull
	private String cid;
	
	// 項目NO
	@NotNull
	private EquipmentItemNo itemNo;
	
	// 項目入力制御
	private ItemInputControl inputcontrol;
	
	// 項目の表示
	private DisplayOfItems items;
	
	/**
	 * [1] 入力した値の制御をチェックする
	 * @param inputVal 入力値
	 * @return エラー
	 */
	public Optional<ErrorItem> check(Optional<ActualItemUsageValue> optInputVal) {
		return this.inputcontrol.checkErrors(this.itemNo, this.items.getItemName(), optInputVal);
	}
}
