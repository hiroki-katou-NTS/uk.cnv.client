package nts.uk.ctx.office.dom.equipment.achievement;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.設備の実績入力フォーマット設定
 * @author NWS-DungDV
 *
 */
@Getter
@AllArgsConstructor
public class EquipmentPerformInputFormatSetting extends AggregateRoot {
	// 会社ID
	@NotNull
	private String cid;
	
	// 項目表示設定
	private List<ItemDisplay> itemDisplaySettings;

}
