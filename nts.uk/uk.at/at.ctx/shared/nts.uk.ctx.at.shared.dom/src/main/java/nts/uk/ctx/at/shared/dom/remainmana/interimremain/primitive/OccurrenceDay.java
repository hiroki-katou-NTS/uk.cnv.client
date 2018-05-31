package nts.uk.ctx.at.shared.dom.remainmana.interimremain.primitive;

import nts.arc.primitive.HalfIntegerPrimitiveValue;
import nts.arc.primitive.constraint.HalfIntegerRange;
/**
 * 発生日数
 * @author do_dt
 *
 */
@HalfIntegerRange(min=0d, max = 1d)
public class OccurrenceDay extends HalfIntegerPrimitiveValue<OccurrenceDay>{

	public OccurrenceDay(Double rawValue) {
		super(rawValue);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
