package nts.uk.ctx.at.record.dom.reservation.bento.rules;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 弁当予約締め時刻名
 * @author Doan Duy Hung
 *
 */
@StringMaxLength(10)
public class BentoReservationTimeName extends StringPrimitiveValue<BentoReservationTimeName>{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public BentoReservationTimeName(String rawValue) {
		super(rawValue);
	}

}
