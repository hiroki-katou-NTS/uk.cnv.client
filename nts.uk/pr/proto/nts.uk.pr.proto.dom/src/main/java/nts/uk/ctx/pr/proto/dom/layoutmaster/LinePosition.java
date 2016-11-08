package nts.uk.ctx.pr.proto.dom.layoutmaster;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerMaxValue;

@IntegerMaxValue(99)
public class LinePosition extends IntegerPrimitiveValue<LinePosition> {

	public LinePosition(Integer rawValue) {
		super(rawValue);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
