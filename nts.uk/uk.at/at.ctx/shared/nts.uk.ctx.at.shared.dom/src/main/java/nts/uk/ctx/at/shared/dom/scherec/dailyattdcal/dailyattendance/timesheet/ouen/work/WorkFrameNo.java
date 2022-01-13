package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/** 作業枠NO */
@IntegerRange(min = 1, max = 5)
public class WorkFrameNo extends IntegerPrimitiveValue<WorkFrameNo> {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public WorkFrameNo(Integer rawValue) {
		super(rawValue);
	}
}
