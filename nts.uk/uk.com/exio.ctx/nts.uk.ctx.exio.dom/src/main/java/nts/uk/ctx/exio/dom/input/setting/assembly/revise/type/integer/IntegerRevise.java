package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.integer;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.gul.text.StringUtil;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseValue;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.codeconvert.ExternalImportCodeConvert;
import nts.gul.util.Either;

/**
 * 整数型編集
 */
@AllArgsConstructor
@Getter
public class IntegerRevise implements ReviseValue {
	
	/** コード変換 */
	private Optional<ExternalImportCodeConvert> codeConvert;
	
	@Override
	public Either<ErrorMessage, ?> revise(String target) {
		
		if (StringUtil.isNullOrEmpty(target, false)) {
			return Either.right(null);
		}
		
		return codeConvert
			.map(conv -> conv.convert(target).map(v -> v.v()))
			.orElse(Either.right(target))
			.mapEither(str -> parseLong(str));
	}
	
	private static Either<ErrorMessage, Long> parseLong(String value) {
		
		return Either.tryCatch(() -> Long.parseLong(value), NumberFormatException.class)
				.mapLeft(ex -> new ErrorMessage("受入データが整数ではありません。"));
	}
}
