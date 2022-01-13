package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.CharType;
import nts.arc.primitive.constraint.StringCharType;
import nts.arc.primitive.constraint.StringMaxLength;
/**
 * 作業コード
 * @author HieuLt
 *
 */
@StringMaxLength(20)
@StringCharType(CharType.ALPHA_NUMERIC)
public class TaskCode extends StringPrimitiveValue<TaskCode>{

	private static final long serialVersionUID = 1L;
	
	public TaskCode(String rawValue) {
		super(rawValue);
	}
	
}
