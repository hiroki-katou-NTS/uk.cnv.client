package nts.uk.ctx.office.dom.equipment.classificationmaster;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備分類マスタ.設備分類
 * @author NWS-DungDV
 *
 */
@Getter
public class EquipmentClassification extends AggregateRoot {
	
	// コード
	private EquipmentClassificationCode code;
	
	// 名称
	private EquipmentClassificationName name;
	
	// [C-0] 事業所情報 (コード, 名称)
	public EquipmentClassification(EquipmentClassificationCode code, EquipmentClassificationName name) {
		this.code = code;
		this.name = name;
	}

}
