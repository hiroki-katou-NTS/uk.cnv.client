package nts.uk.ctx.at.record.dom.remainingnumber.nursingcareleavemanagement.basic;

import nts.arc.primitive.HalfIntegerPrimitiveValue;
import nts.arc.primitive.constraint.HalfIntegerRange;

@HalfIntegerRange(min = 0, max = 999.5)
public class NumOfUseDay extends HalfIntegerPrimitiveValue<NumOfUseDay> {

	/**
	 * 使用日数
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NumOfUseDay(Double rawValue) {
		super(rawValue);
	}

}