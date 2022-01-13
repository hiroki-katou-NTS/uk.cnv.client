package nts.uk.ctx.at.function.dom.alarm.extractionrange.month;

/**
 * 月次の締め月指定月
 * @author thanhpv
 *
 */
public enum MonthlyClosingMonthSpecifiedMonth {

	/**当月*/
	CURRENT_MONTH(0, "当月"),

	/** 1ヶ月後 */
	ONE_MONTH_LATER(1, "１ヶ月後"),

	/** 2ヶ月後*/
	TWO_MONTH_LATER(2, "２ヶ月後"),

	/** 3ヶ月後*/
	THREE_MONTH_LATER(3, "３ヶ月後"),

	/** 4ヶ月後*/
	FOUR_MONTH_LATER(4, "４ヶ月後"),

	/** 5ヶ月後*/
	FIVE_MONTH_LATER(5, "５ヶ月後"),

	/** 6ヶ月後*/
	SIX_MONTH_LATER(6, "６ヶ月後");

	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;

	private MonthlyClosingMonthSpecifiedMonth(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
}
