package nts.uk.ctx.office.dom.equipment.information;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備マスタ.設備名称
 */
@StringMaxLength(30)
public class EquipmentName extends StringPrimitiveValue<EquipmentName> {

	private static final long serialVersionUID = 1L;

	public EquipmentName(String rawValue) {
		super(rawValue);
	}
	
}
