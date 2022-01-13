package nts.uk.ctx.at.record.dom.daily.ouen;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * @author thanhpv
 * @name 応援勤務枠No
 */
@IntegerRange(max = 20, min = 1)
public class SupportFrameNo extends IntegerPrimitiveValue<SupportFrameNo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SupportFrameNo(int rawValue) {
		super(rawValue);
		// TODO Auto-generated constructor stub
	}
	
}
