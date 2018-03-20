package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber;

import nts.arc.primitive.HalfIntegerPrimitiveValue;
import nts.arc.primitive.constraint.HalfIntegerRange;

@HalfIntegerRange(min = -999.5 , max = 999.5)
public class AnnualLeaveRemainingDayNumber extends HalfIntegerPrimitiveValue<AnnualLeaveRemainingDayNumber>{

	private static final long serialVersionUID = 8578961613409044770L;

	public AnnualLeaveRemainingDayNumber(Double rawValue) {
		super(rawValue);
	}

}
