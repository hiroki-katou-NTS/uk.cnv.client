package nts.uk.ctx.office.dom.equipment.data;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * 利用文字項目
 */
@StringMaxLength(200)
public class UseTextItem extends StringPrimitiveValue<UseTextItem> {

	private static final long serialVersionUID = 1L;

	public UseTextItem(String rawValue) {
		super(rawValue);
	}
}
