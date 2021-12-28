package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * お気に入り作業名称
 * 
 * @author tutt
 *
 */
@StringMaxLength(20)
public class FavoriteTaskName extends StringPrimitiveValue<FavoriteTaskName> {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new favorite task name.
	 *
	 * @param rawValue the raw value
	 */
	public FavoriteTaskName(String rawValue) {
		super(rawValue);
	}
}
