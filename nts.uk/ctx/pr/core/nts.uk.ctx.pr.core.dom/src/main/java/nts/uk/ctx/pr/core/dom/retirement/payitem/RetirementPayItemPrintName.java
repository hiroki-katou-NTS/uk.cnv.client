package nts.uk.ctx.pr.core.dom.retirement.payitem;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;
/**
 * 退職金項目印刷名称
 * @author Doan Duy Hung
 *
 */

@StringMaxLength(20)
@StringCharType(CharType.ALPHA_NUMERIC)
public class RetirementPayItemPrintName extends StringPrimitiveValue<RetirementPayItemPrintName>{
	public RetirementPayItemPrintName(String value) {
		super(value);
		// TODO Auto-generated constructor stub
	}
}
