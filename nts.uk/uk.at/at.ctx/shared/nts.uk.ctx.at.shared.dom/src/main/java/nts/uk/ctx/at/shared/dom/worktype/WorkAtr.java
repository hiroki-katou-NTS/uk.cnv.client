package nts.uk.ctx.at.shared.dom.worktype;

import lombok.AllArgsConstructor;

@AllArgsConstructor
/** 1日午前午後区分 */
public enum WorkAtr {	
	// 1日
	OneDay(0),

	// 午前
	Monring(1),
	
	// 午後
	Afternoon(2);

	/** The value. */
	public final int value;

	/** The Constant values. */
	private final static WorkAtr[] values = WorkAtr.values();
	
	/**
	 * 1日であるか判定する
	 * @return　1日である
	 */
	public boolean isOneDay() {
		return this.equals(OneDay);
	}
	
	/**
	 * 午後であるか判定する
	 * @return 午後である
	 */
	public boolean isAfterNoon() {
		return this.equals(Afternoon);
	}
	
	/**
	 * 午後であるか判定する
	 * @return 午後である
	 */
	public boolean isMorning() {
		return this.equals(Monring);
	}
	
	/**
	 * Value of.
	 *
	 * @param value the value
	 * @return the workAtr
	 */
	public static WorkAtr valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (WorkAtr val : WorkAtr.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}
}
