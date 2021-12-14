package nts.uk.ctx.office.dom.equipment.classificationmaster;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;
import nts.uk.shr.com.primitive.ZeroPaddedCode;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.設備分類マスタ.設備分類コード
 * @author NWS-DungDV
 *
 */
@StringCharType(CharType.ALPHA_NUMERIC)
@StringMaxLength(4)
@ZeroPaddedCode
public class EquipmentClassificationCode extends StringPrimitiveValue<EquipmentClassificationCode> {

	private static final long serialVersionUID = 1L;

	public EquipmentClassificationCode(String rawValue) {
		super(rawValue);
	}
}
