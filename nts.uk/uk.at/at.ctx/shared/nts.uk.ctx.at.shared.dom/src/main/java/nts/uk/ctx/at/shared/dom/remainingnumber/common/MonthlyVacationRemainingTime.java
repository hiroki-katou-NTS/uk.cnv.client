package nts.uk.ctx.at.shared.dom.remainingnumber.common;

import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

/**
 * 月別休暇残時間
 */
@TimeRange(min = "-999:59", max = "999:59")
public class MonthlyVacationRemainingTime extends TimeDurationPrimitiveValue<MonthlyVacationRemainingTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new amount value.
     *
     * @param rawValue the raw value
     */
    public MonthlyVacationRemainingTime(Integer rawValue) {
        super(rawValue);
    }
}
