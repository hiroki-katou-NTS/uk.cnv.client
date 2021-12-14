package nts.uk.ctx.office.dom.equipment.information;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備マスタ.設備備考
 */
@StringMaxLength(200)
public class EquipmentRemark extends StringPrimitiveValue<EquipmentRemark> {

	private static final long serialVersionUID = 1L;

	public EquipmentRemark(String rawValue) {
		super(rawValue);
	}
	
}
