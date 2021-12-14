package nts.uk.ctx.office.dom.equipment.data;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * 利用数値項目
 */
@IntegerRange(min = -99999999, max = 99999999)
public class UseNumericItem extends IntegerPrimitiveValue<UseNumericItem> {

	private static final long serialVersionUID = 1L;

	public UseNumericItem(Integer rawValue) {
		super(rawValue);
	}
}
