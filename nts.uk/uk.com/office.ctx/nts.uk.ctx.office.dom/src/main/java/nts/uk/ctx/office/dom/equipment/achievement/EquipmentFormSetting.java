package nts.uk.ctx.office.dom.equipment.achievement;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.設備帳票設定
 * @author NWS-DungDV
 *
 */
@Getter
@AllArgsConstructor
public class EquipmentFormSetting extends AggregateRoot {
	// 会社ID
	@NotNull
	private String cid;
	
	// 帳票タイトル
	private EquipmentFormTitle title;
}
