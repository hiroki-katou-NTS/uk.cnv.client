package nts.uk.ctx.pr.core.dom.rule.employment.layout.allot;

import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;
import nts.uk.shr.com.primitive.CodePrimitiveValue;

/**
 * Code of Bonus Stmt
 */
@StringCharType(CharType.NUMERIC)
@StringMaxLength(2)
public class BonusStmtCode extends CodePrimitiveValue<BonusStmtCode>{

	public BonusStmtCode(String rawValue) {
		super(rawValue);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
