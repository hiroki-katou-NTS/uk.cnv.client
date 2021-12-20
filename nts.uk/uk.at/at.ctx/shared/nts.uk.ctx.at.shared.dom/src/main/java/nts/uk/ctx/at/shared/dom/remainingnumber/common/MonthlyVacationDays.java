package nts.uk.ctx.at.shared.dom.remainingnumber.common;


import nts.arc.primitive.HalfIntegerPrimitiveValue;
import nts.arc.primitive.constraint.HalfIntegerRange;

/**
 * 月別休暇付与日数
 */
@HalfIntegerRange(min = 0, max = 999.5)
public class MonthlyVacationDays extends HalfIntegerPrimitiveValue<MonthlyVacationDays> {


    /**
     * 使用日数
     */
    private static final long serialVersionUID = 1L;

    public MonthlyVacationDays(Double rawValue) {
        super(rawValue);
    }
}