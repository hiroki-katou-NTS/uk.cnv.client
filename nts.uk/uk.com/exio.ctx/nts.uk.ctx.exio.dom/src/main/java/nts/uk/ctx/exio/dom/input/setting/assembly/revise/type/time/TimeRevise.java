package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.gul.text.StringUtil;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseValue;
import nts.uk.ctx.exio.dom.input.util.Either;

/**
 * 時間・時刻型編集
 */
@AllArgsConstructor
@Getter
public class TimeRevise implements ReviseValue {
	
	/** 時分 */
	private HourlySegment hourly;
	
	/** 進数 */
	private Optional<TimeBaseNumber> baseNumber;
	
	/** 区切り文字 */
	private Optional<TimeBase60Delimiter> delimiter;
	
	/** 端数処理 */
	private Optional<TimeBase10Rounding> rounding;

	@Override
	public Either<ErrorMessage, ?> revise(String target) {
		
		if (StringUtil.isNullOrEmpty(target, false)) {
			return Either.right(null);
		}
		
		if (hourly == HourlySegment.MINUTE) {
			return Either.tryCatch(() -> Integer.parseInt(target), NumberFormatException.class)
					.mapLeft(ex -> new ErrorMessage("受入データが整数ではありません"))
					.map(min -> hourly.toMinutesDecimal(new BigDecimal(min)).longValue());
		}
		
		if (baseNumber.get() == TimeBaseNumber.SEXAGESIMAL) {
			// 60進数は、区切り文字を用いて時分→分変換
			return delimiter.get().toMinutes(target)
					.map(i -> (long) (int) i);
		} else {
			// 10進数は、分に変換して端数処理
			return Either.tryCatch(() -> hourly.toMinutesDecimal(new BigDecimal(target)), NumberFormatException.class) 
					.mapLeft(ex -> new ErrorMessage("受入データが10進数ではありません。"))
					.map(min -> (long) rounding.get().round(min));
		}
	}
}
