package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.real;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.gul.text.StringUtil;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseValue;
import nts.uk.ctx.exio.dom.input.util.Either;

@AllArgsConstructor
@Value
public class RealRevise implements ReviseValue {
	
	/** 整数値を小数として受け入れる */
	private boolean isDecimalization;
	
	/** 桁数 */
	private Optional<DecimalDigitNumber> length;
	
	@Override
	public Either<ErrorMessage, ?> revise(String target) {
		
		if (StringUtil.isNullOrEmpty(target, false)) {
			return Either.right(null);
		}
		
		Either<ErrorMessage, BigDecimal> result;
		
		if(isDecimalization) {
			// 整数　→　小数編集する場合
			result = stringToIntToDecimal(target);
		}
		else {
			// 整数　→　小数編集しない場合
			result = Either.tryCatch(() -> new BigDecimal(target), NumberFormatException.class)
					.mapLeft(ex -> new ErrorMessage("受入データが数値ではありません。"));
		}
		
		return result.map(d -> (Object) d);
	}
	
	// 文字列　→　整数　→　実数変換
	private Either<ErrorMessage, BigDecimal> stringToIntToDecimal(String resultStr) {
		
		return Either.tryCatch(() -> Long.parseLong(resultStr), NumberFormatException.class)
				.mapLeft(ex -> new ErrorMessage("整数値を小数として受け入れる設定ですが、受入データに小数点が含まれています。"))
				.map(n -> decimalize(n));
	}
	
	// 小数化
	public BigDecimal decimalize(Long target) {
		BigDecimal result = BigDecimal.valueOf(target);
		// 10のLength乗で割ることで小数化する
		return result.divide(BigDecimal.valueOf(Math.pow(10, Double.valueOf(length.get().v()))));
	}
}
