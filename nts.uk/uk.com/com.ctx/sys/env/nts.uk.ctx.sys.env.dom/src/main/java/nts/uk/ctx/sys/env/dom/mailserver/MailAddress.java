package nts.uk.ctx.sys.env.dom.mailserver;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * メールアドレス
 * @author thanhpv
 *
 */
@StringMaxLength(256)
@StringCharType(CharType.ANY_HALF_WIDTH)
public class MailAddress extends StringPrimitiveValue<MailAddress>{

	private static final long serialVersionUID = 1L;

	public MailAddress(String rawValue) {
		super(rawValue);
	}
}
