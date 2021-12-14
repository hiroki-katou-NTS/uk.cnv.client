package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.設備項目NO
 */
@StringCharType(CharType.NUMERIC)
@StringMaxLength(1)
public class EquipmentItemNo extends StringPrimitiveValue<EquipmentItemNo> {

	private static final long serialVersionUID = 1L;
	
	public EquipmentItemNo(String rawValue) {
		super(rawValue);
	}
}
