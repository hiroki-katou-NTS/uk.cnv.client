package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;


import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerMaxValue;
import nts.arc.primitive.constraint.IntegerMinValue;

/**
 * Primitive: 社員時間単価
 */
@IntegerMinValue(0)
@IntegerMaxValue(9999999)
public class WorkingHoursUnitPrice extends IntegerPrimitiveValue<WorkingHoursUnitPrice> {

	public static final WorkingHoursUnitPrice ZERO = new WorkingHoursUnitPrice(0);

    private static final long serialVersionUID = 1L;
    public WorkingHoursUnitPrice(Integer rawValue) {
        super(rawValue);
    }

	public static WorkingHoursUnitPrice valueOf(int value) {
		return new WorkingHoursUnitPrice(value);
	}
}
