package nts.uk.ctx.exio.dom.input.setting.assembly.revise.codeconvert;

import nts.arc.primitive.PrimitiveValue;
import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;
import nts.uk.shr.com.primitive.ZeroPaddedCode;

/**
 * コード変換コード
 *
 */
@StringCharType(CharType.NUMERIC)
@StringMaxLength(3)
@ZeroPaddedCode
public class CodeConvertCode extends StringPrimitiveValue<PrimitiveValue<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Contructor
	 * 
	 * @param rawValue
	 */
	public CodeConvertCode(String rawValue) {
		super(rawValue);
		// TODO Auto-generated constructor stub
	}

}
