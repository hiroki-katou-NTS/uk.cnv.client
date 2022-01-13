package nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * PrimitiveValue: 平均回数
 *
 */
@IntegerRange(min = 0, max = 99)
public class AverageNumberOfTimes extends IntegerPrimitiveValue<AverageNumberOfTimes> {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new time duration.
	 *
	 * @param numberOfTimes the number of times
	 */
	public AverageNumberOfTimes(Integer numberOfTimes) {
		super(numberOfTimes);
	}
}
