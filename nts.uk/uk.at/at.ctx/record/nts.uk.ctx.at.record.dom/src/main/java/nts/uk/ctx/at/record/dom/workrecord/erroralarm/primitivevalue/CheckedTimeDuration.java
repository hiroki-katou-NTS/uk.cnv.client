/**
 * 5:43:49 PM Nov 9, 2017
 */
package nts.uk.ctx.at.record.dom.workrecord.erroralarm.primitivevalue;

import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

/**
 * @author hungnm
 *
 */
//チェック条件値時間
@TimeRange(min = "-999:59", max = "999:59")
public class CheckedTimeDuration extends TimeDurationPrimitiveValue<CheckedTimeDuration> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new time duration.
	 *
	 * @param timeAsMinutes the time as minutes
	 */
	public CheckedTimeDuration(int timeAsMinutes) {
		super(timeAsMinutes);
	}
	
	public String getTimeWithFormat(){
		return this.hour() + ":" + (this.minute() < 10 ? "0" + this.minute() : this.minute());
	}
	
}