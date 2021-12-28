package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * 作業補足情報NO
 * @author tutt
 *
 */
@IntegerRange(min = 1, max = 5)
public class SuppInfoNo extends IntegerPrimitiveValue<SuppInfoNo>  {

	private static final long serialVersionUID = 1L;

	public SuppInfoNo(Integer rawValue) {
		super(rawValue);
	}

}
