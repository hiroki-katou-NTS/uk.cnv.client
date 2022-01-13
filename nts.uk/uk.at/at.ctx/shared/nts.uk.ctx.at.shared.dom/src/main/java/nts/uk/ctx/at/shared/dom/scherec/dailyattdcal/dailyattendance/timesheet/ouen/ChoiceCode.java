package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 作業補足情報の選択肢コード
 * 
 * @author tutt
 *
 */
@StringMaxLength(10)
@StringCharType(CharType.ALPHA_NUMERIC)
public class ChoiceCode extends StringPrimitiveValue<ChoiceCode> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new choice code.
	 *
	 * @param rawValue the raw value
	 */
	public ChoiceCode(String rawValue) {
		super(rawValue);
	}
}
