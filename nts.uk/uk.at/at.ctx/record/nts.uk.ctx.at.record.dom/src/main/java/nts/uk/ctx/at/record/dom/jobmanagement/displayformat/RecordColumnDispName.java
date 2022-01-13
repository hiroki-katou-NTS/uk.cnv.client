package nts.uk.ctx.at.record.dom.jobmanagement.displayformat;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 実績欄表示名称
 * 
 * @author tutt
 *
 */
@StringMaxLength(4)
public class RecordColumnDispName extends StringPrimitiveValue<RecordColumnDispName> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new choice name.
	 *
	 * @param rawValue the raw value
	 */
	public RecordColumnDispName(String rawValue) {
		super(rawValue);
	}
}
