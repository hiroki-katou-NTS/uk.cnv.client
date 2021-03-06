package nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue;

import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

/**
 * PrimitiveValue: ζ―δΌζι
 *
 */
@TimeRange(min = "00:00", max = "999:59")
public class HolidayTime extends TimeDurationPrimitiveValue<HolidayTime> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new time duration.
	 *
	 * @param timeAsMinutes the time as minutes
	 */
	public HolidayTime(int timeAsMinutes) {
		super(timeAsMinutes);
	}
	
	public String getTimeWithFormat(){
		return this.hour() + ":" + (this.minute() < 10 ? "0" + this.minute() : this.minute());
	}
}

