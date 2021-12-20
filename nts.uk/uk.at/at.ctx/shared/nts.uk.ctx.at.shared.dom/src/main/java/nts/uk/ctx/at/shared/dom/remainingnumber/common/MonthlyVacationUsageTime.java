package nts.uk.ctx.at.shared.dom.remainingnumber.common;

import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

/**
 * PrimitiveValue: 月別休暇使用時間
 *
 */
@TimeRange(min = "00:00", max = "999:59")
public class MonthlyVacationUsageTime extends TimeDurationPrimitiveValue<MonthlyVacationUsageTime> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new time duration.
     *
     * @param timeAsMinutes the time as minutes
     */
    public MonthlyVacationUsageTime(int timeAsMinutes) {
        super(timeAsMinutes);
    }

    public String getTimeWithFormat(){
        return this.hour() + ":" + (this.minute() < 10 ? "0" + this.minute() : this.minute());
    }
}
