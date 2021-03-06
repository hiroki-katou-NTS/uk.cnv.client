package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 打刻ページ名称
 * @author phongtq
 *
 */

@StringMaxLength(20)
public class StampPageName extends StringPrimitiveValue<StampPageName>{
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param rawValue
	 */
	public StampPageName(String rawValue) {
		super(rawValue);
	}
}