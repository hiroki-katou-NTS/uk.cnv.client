package nts.uk.ctx.exio.dom.input.validation;

import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.revise.reviseddata.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.validation.condition.system.ValidateSystemRange;
import nts.uk.ctx.exio.dom.input.validation.condition.user.ValidateUserRange;

/**
 * 値の検証　とは誰に何を指示することなのか知っている。
 */
public class ValidateData{
	
	/**
	 * 妥当な数値であるか検証する
	 */
	public static boolean validate(ValidateRequire require, ExecutionContext context, RevisedDataRecord record) {
		return ValidateSystemRange.validate(require, context, record)
			 && ValidateUserRange.validate(require, context, record);
	}
	
	public static interface ValidateRequire extends ValidateUserRange.UserRequire,
																				ValidateSystemRange.SystemRequire{
	}
}
