package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.real;

import nts.arc.primitive.PrimitiveValue;
import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerMaxValue;
import nts.arc.primitive.constraint.IntegerMinValue;

/**
 * 小数桁数
 */
@IntegerMaxValue(10)
@IntegerMinValue(1)
public class DecimalDigitNumber extends IntegerPrimitiveValue<PrimitiveValue<Integer>>{
	
	private static final long serialVersionUID = 1L;
	
	public DecimalDigitNumber(int rawValue) {
		super(rawValue);
	}
}
