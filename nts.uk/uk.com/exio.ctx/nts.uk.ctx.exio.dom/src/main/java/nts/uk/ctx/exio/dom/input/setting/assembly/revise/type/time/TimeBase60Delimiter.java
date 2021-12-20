package nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.time;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.type.string.PaddingMethod;
import nts.gul.util.Either;

/**
 * 60進数表記時分データの区切り文字
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TimeBase60Delimiter {
	
	/** 無し */
	NONE(0, "Enum_TimeBase60Delimiter_NO_DELIMITER", ""), 

	/** 小数点 */
	DECIMAL_POINT(1, "Enum_TimeBase60Delimiter_SEPARATE_BY_DECIMAL", "\\."), 

	/** コロン */
	COLON(2, "Enum_TimeBase60Delimiter_SEPARATE_BY_COLON", ":");

	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;
	
	/** The character */
	public final String character;
	
	public static TimeBase60Delimiter valueOf(int value) {
		return EnumAdaptor.valueOf(value, TimeBase60Delimiter.class);
	}
	
	/**
	 * 区切り文字変換
	 * @param target
	 * @return
	 */
	public Either<ErrorMessage, Integer> toMinutes(String target) {
		switch(this) {
		case NONE:
			return convertNoDelimiter(target);
		case DECIMAL_POINT:
		case COLON:
			return convertDelimiter(target);
		default:
			throw new RuntimeException("区切り文字に対応する変換処理を実装してください。");
		}
		
	}
	
	// 区切り文字なし
	private Either<ErrorMessage, Integer> convertNoDelimiter(String target) {
		if (!target.matches("\\d+")) {
			// 整数でない場合
			return Either.left(new ErrorMessage("時間の区切り文字無しの設定ですが、受入データが整数ではありません。"));
		}
		
		if (target.length() < 4) {
			target = PaddingMethod.ZERO_BEFORE.complement(target, 4);
		}
		
		return convert(
				target.substring(0, 2),
				target.substring(2));
	}
	
	// 区切り文字あり
	private Either<ErrorMessage, Integer> convertDelimiter(String target) {
		// 区切り文字で文字列を2分割
		String[] strParts = target.split(character);
		
		if (strParts.length != 2) {
			return Either.left(new ErrorMessage("時間の区切り文字が正しく含まれていません。"));
		}
		
		return convert(strParts[0], strParts[1]);
	}

	private static Either<ErrorMessage, Integer> convert(String hourString, String minString) {
		if (minString.length() < 2) {
			return Either.left(new ErrorMessage("受入データの時間値が正しくありません。"));
		}
		
		int hour;
		int min;
		try {
			hour = Integer.parseInt(hourString);
			min = Integer.parseInt(minString);
		} catch (NumberFormatException ex) {
			return Either.left(new ErrorMessage("受入データの時間値が正しくありません。"));
		}
		
		// 分に当たる値が59以下であることを確認
		if(min >= 60) {
			return Either.left(new ErrorMessage("受入データの時間値が正しくありません。"));
		}
		
		// 時間を分に変換して加算
		return Either.right(hour * 60 + min);
	}
}
