package nts.uk.ctx.office.dom.equipment.achievement;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.TimeRange;

@TimeRange(min = "0:00", max = "999:59")
public class UsageItemtime extends StringPrimitiveValue<UsageItemtime>{
	private static final long serialVersionUID = 1L;
	
	public UsageItemtime(String rawValue) {
		super(rawValue);
	}

}
