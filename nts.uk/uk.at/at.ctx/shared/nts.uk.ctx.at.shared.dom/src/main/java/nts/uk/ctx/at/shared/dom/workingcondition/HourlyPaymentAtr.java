package nts.uk.ctx.at.shared.dom.workingcondition;

/**
 * The Enum HourlyPpaymentAtr.
 */
// 時給者区分
public enum HourlyPaymentAtr {

	// 時給者
	HOURLY_PAY(0, "Enum_UseAtr_Hourly_Pay", "時給者"),

	// 時給者以外
	OOUTSIDE_TIME_PAY(1, "Enum_UseAtr_Outside_Time_Pay", "時給者以外");
	
	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;

	/** The description. */
	public final String description;

	/** The Constant values. */
	private final static HourlyPaymentAtr[] values = HourlyPaymentAtr.values();

	/**
	 * Instantiates a new implement atr.
	 *
	 * @param value the value
	 * @param nameId the name id
	 * @param description the description
	 */
	private HourlyPaymentAtr(int value, String nameId, String description) {
		this.value = value;
		this.nameId = nameId;
		this.description = description;
	}

	/**
	 * Value of.
	 *
	 * @param value the value
	 * @return the implement atr
	 */
	public static HourlyPaymentAtr valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (HourlyPaymentAtr val : HourlyPaymentAtr.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}

	/**
	 * 時給者か
	 * @return 時給者である
	 */
	public boolean isHourlyPay() {
		return this.equals(HOURLY_PAY);
	}
}
