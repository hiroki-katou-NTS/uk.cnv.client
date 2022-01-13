package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * 作業補足数値
 * @author tutt
 *
 */
@IntegerRange(min = -99999999, max = 99999999)
public class SuppNumValue extends IntegerPrimitiveValue<SuppNumValue> {

	private static final long serialVersionUID = 1L;

	public SuppNumValue(Integer rawValue) {
		super(rawValue);
	}

}
