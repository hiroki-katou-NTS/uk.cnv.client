package nts.uk.ctx.at.shared.dom.common.time;

import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

/**
 * 作業合計乖離時間
 * @author hoangnd
 *
 */
@TimeRange(min = "0:01", max = "999:59")
public class TotalWorkDivergenceTime extends TimeDurationPrimitiveValue<TotalWorkDivergenceTime> {
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	public TotalWorkDivergenceTime(int timeAsMinutes) {
		super(timeAsMinutes);
	}
	
	@Override
	protected Integer reviseRawValue(Integer rawValue) {
		if (rawValue == null) return super.reviseRawValue(0);
		if (rawValue > 999 * 60 + 59) rawValue = 999 * 60 + 59;
		if (rawValue < 1) rawValue = 1;
		return super.reviseRawValue(rawValue);
	}

}
