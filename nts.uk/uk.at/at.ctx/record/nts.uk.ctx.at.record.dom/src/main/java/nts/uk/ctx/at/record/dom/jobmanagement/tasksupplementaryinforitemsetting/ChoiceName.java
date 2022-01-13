package nts.uk.ctx.at.record.dom.jobmanagement.tasksupplementaryinforitemsetting;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 作業補足情報の選択肢名称
 * 
 * @author tutt
 *
 */
@StringMaxLength(80)
public class ChoiceName extends StringPrimitiveValue<ChoiceName> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new choice name.
	 *
	 * @param rawValue the raw value
	 */
	public ChoiceName(String rawValue) {
		super(rawValue);
	}
}
