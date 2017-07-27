package nts.uk.ctx.bs.person.dom.person.role;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;
@StringMaxLength(30)
public class RoleName extends StringPrimitiveValue<RoleName>{

	public RoleName(String rawValue) {
		super(rawValue);
	}
	private static final long serialVersionUID = 1L;

}
