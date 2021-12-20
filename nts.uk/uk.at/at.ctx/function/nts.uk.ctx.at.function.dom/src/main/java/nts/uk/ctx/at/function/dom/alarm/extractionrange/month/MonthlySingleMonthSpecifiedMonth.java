package nts.uk.ctx.at.function.dom.alarm.extractionrange.month;

/**
 * 月次の単月指定月
 * @author thanhpv
 *
 */
public enum MonthlySingleMonthSpecifiedMonth {

	/**当月*/
	CURRENT_MONTH(0, "当月"),

	/** 1ヶ月後 */
	ONE_MONTH_AGO(1, "１ヶ月前"),

	/** 2ヶ月前*/
	TWO_MONTH_AGO(2, "２ヶ月前"),

	/** 3ヶ月前*/
	THREE_MONTH_AGO(3, "３ヶ月前"),

	/** 4ヶ月前*/
	FOUR_MONTH_AGO(4, "４ヶ月前"),

	/** 5ヶ月前*/
	FIVE_MONTH_AGO(5, "５ヶ月前"),

	/** 6ヶ月前*/
	SIX_MONTH_AGO(6, "６ヶ月前");

	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;

	private MonthlySingleMonthSpecifiedMonth(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
}
