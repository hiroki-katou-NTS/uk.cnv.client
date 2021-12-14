package nts.uk.ctx.office.dom.equipment.data;

import nts.arc.primitive.TimeDurationPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

/**
 * 利用時間項目
 */
@TimeRange(min = "00:00", max = "999:59")
public class UseTimeItem extends TimeDurationPrimitiveValue<UseTimeItem> {

	private static final long serialVersionUID = 1L;

	public UseTimeItem(boolean isNegative, int hourPart, int minutePart) {
		super(isNegative, hourPart, minutePart);
	}
}
