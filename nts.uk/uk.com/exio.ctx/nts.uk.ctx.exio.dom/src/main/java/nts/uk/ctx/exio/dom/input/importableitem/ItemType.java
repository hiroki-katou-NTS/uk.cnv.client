package nts.uk.ctx.exio.dom.input.importableitem;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;

import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.gul.text.StringUtil;
import nts.uk.ctx.exio.dom.input.errors.ErrorMessage;
import nts.uk.ctx.exio.dom.input.util.Either;

/**
 * 
 * 項目型
 *
 */
public enum ItemType {
	/**
	 * 0: 文字型
	 */
	STRING(0, "Enum_ExternalImportItemType_CHARACTER"),
	/**
	 * 1: 整数型
	 */
	INT(1, "Enum_ExternalImportItemType_INT"),
	/**
	 * 2: 実数型
	 */
	REAL(2, "Enum_ExternalImportItemType_REAL"),
	/**
	 * 3: 日付型
	 */
	DATE(3, "Enum_ExternalImportItemType_DATE"),
	/**
	 * 4: 時間型
	 */
	TIME_DURATION(4, "Enum_ExternalImportItemType_TIME_DURATION"),
	/**
	 * 5: 時刻型
	 */
	TIME_POINT(5, "Enum_ExternalImportItemType_TIME_POINT");

	
	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;

	private ItemType(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
	
	/**
	 * 文字列を項目方に対応する型に変換する
	 * @param value
	 * @return
	 */
	public Either<ErrorMessage, ?> parse(String value) {
		switch(this) {
		case INT:
		case TIME_DURATION:
		case TIME_POINT:
			if (StringUtil.isNullOrEmpty(value, false)) return Either.right(null);
			return Either.tryCatch(() -> Long.parseLong(value), NumberFormatException.class)
					.mapLeft(ex -> new ErrorMessage("整数ではありません。"));
		case REAL:
			if (StringUtil.isNullOrEmpty(value, false)) return Either.right(null);
			return Either.tryCatch(() -> new BigDecimal(value), NumberFormatException.class)
					.mapLeft(ex -> new ErrorMessage("数値ではありません。"));
		case DATE:
			if (StringUtil.isNullOrEmpty(value, false)) return Either.right(null);
			return Either.tryCatch(() -> GeneralDate.fromString(value, "yyyy/MM/dd"), DateTimeParseException.class)
					.mapLeft(ex -> new ErrorMessage("日付データはYYYY/MM/DDの形式にしてください。"));
		case STRING:
			return Either.right(value);
		default:
			throw new RuntimeException("unknown: " + this);
		}
	}
	
	public String getResourceText() {
		return I18NText.getText(this.nameId);
	}
}
