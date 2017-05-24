package nts.uk.ctx.pr.report.dom.payment.comparing.confirm;

import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;
import nts.uk.shr.com.primitive.CodePrimitiveValue;

@StringMaxLength(36)
@StringCharType(CharType.ALPHA_NUMERIC)
public class PersonID extends CodePrimitiveValue<PersonID> {

	private static final long serialVersionUID = 1L;

	public PersonID(String rawValue) {
		super(rawValue);
	}
}
