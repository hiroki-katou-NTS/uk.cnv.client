package nts.uk.ctx.office.dom.equipment.classificationmaster;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備分類マスタ.設備分類名称
 * @author NWS-DungDV
 *
 */
@StringMaxLength(30)
public class EquipmentClassificationName extends StringPrimitiveValue<EquipmentClassificationName> {
	
	private static final long serialVersionUID = 1L;

	public EquipmentClassificationName(String rawValue) {
		super(rawValue);
	}
}
