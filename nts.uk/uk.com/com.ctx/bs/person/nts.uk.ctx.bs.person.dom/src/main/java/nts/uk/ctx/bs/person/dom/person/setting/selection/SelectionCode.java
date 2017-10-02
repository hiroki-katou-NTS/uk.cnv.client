package nts.uk.ctx.bs.person.dom.person.setting.selection;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;


@IntegerRange(min = 1, max = 10)
public class SelectionCode extends IntegerPrimitiveValue<SelectionCode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SelectionCode(int rawValue) {
		super(rawValue);
		// TODO Auto-generated constructor stub
	}

}
