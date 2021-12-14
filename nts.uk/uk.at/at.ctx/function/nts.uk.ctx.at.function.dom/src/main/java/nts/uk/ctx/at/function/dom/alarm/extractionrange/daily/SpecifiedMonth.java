package nts.uk.ctx.at.function.dom.alarm.extractionrange.daily;

/**
 * 日次の締め日指定月
 * @author thanhpv
 *
 */
public enum SpecifiedMonth {

	/**当月*/
	CURRENTMONTH(0, "当月"),
	
	/** 1ヶ月前 */
	ONEMONTHAGO(1, "1ヶ月前"),

	/** 2ヶ月前*/
	TWOMONTHAGO(2, "2ヶ月前"),
	
	/** 3ヶ月前*/
	THREEMONTHAGO(3, "3ヶ月前"),
	
	/** 4ヶ月前*/
	FOURMONTHAGO(4, "4ヶ月前"),
	
	/** 5ヶ月前*/
	FIVEMONTHAGO(5, "5ヶ月前"),
	
	/** 6ヶ月前*/
	SIXMONTHAGO(6, "6ヶ月前");
	
	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;
	
	private SpecifiedMonth(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
}
