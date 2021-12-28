package nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 作業補足情報の外部コード
 * 
 * @author tutt
 *
 */
@StringMaxLength(10)
@StringCharType(CharType.ALPHA_NUMERIC)
public class ExternalCode extends StringPrimitiveValue<ExternalCode> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new external code.
	 *
	 * @param rawValue the raw value
	 */
	public ExternalCode(String rawValue) {
		super(rawValue);
	}
}
