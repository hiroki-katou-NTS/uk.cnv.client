package nts.uk.ctx.bs.person.dom.person.currentdddress;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

@StringMaxLength(30)
public class HouseRent extends StringPrimitiveValue<HouseRent>{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	public HouseRent(String rawValue) {
		super(rawValue);
		// TODO Auto-generated constructor stub
	}

}
