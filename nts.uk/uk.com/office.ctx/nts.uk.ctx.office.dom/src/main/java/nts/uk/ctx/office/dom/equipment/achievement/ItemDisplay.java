package nts.uk.ctx.office.dom.equipment.achievement;

import org.eclipse.persistence.internal.xr.ValueObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.項目表示
 * @author NWS-DungDV
 *
 */
@Getter
@AllArgsConstructor
public class ItemDisplay extends ValueObject {
	// 表示幅
	private DisplayWidth displayWidth;
	
	// 表示順番
	private int displayOrder;
	
	// 項目NO
	private EquipmentItemNo itemNo;
}
