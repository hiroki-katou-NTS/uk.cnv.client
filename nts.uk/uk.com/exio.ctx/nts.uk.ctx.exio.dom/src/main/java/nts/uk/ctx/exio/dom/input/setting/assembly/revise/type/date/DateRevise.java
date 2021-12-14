package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.date;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.arc.error.BusinessException;
import nts.gul.text.StringUtil;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseValue;
import nts.uk.ctx.exio.dom.input.util.Either;

/**
 * 日付型編集
 */
@AllArgsConstructor
@Value
public class DateRevise implements ReviseValue {
	
	/** 日付形式 */
	private ExternalImportDateFormat dateFormat;
	
	@Override
	public Either<ErrorMessage, ?> revise(String target) {
		
		if (StringUtil.isNullOrEmpty(target, false)) {
			return Either.right(null);
		}
		
		return Either.tryCatch(() -> dateFormat.fromString(target), BusinessException.class)
				.mapLeft(ErrorMessage::of);
	}
}
