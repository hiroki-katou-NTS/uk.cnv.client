package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;
/**
 * 割増項目名称
 * @author Doan Duy Hung
 *
 */
@StringMaxLength(30)
public class PremiumName extends StringPrimitiveValue<PremiumName>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5794442674634451699L;

	public PremiumName(String rawValue) {
		super(rawValue);
	}

}
