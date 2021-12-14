package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.オフィス支援.設備管理.実績項目設定.項目文字の桁数
 */
@IntegerRange(min = 1, max = 200)
public class DigitsNumber extends IntegerPrimitiveValue<DigitsNumber> {
	private static final long serialVersionUID = 1L;
	
	public DigitsNumber(Integer rawValue) {
		super(rawValue);
	}
}
