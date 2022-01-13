/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

/**
 * The Enum ExtraTimeItemNo.
 */
// 割増時間項目NO
public enum ExtraTimeItemNo {
	/** The one. */
	ONE(1, "ONE"),
	
	/** The two. */
	TWO(2, "TWO"),
	
	/** The three. */
	THREE(3, "THREE"),
	
	/** The four. */
	FOUR(4, "FOUR"),
	
	/** The five. */
	FIVE(5, "FIVE"),
	
	/** The six. */
	SIX(6, "SIX"),
	
	/** The seven. */
	SEVEN(7, "SEVEN"),
	
	/** The eight. */
	EIGHT(8, "EIGTH"),
	
	/** The nine. */
	NINE(9, "NINE"),
	
	/** The ten. */
	TEN(10, "TEN");

	/** The value. */
	public int value;

	/** The description. */
	public String description;

	/** The Constant values. */
	private final static ExtraTimeItemNo[] values = ExtraTimeItemNo.values();

	/**
	 * Instantiates a new breakdown item no.
	 *
	 * @param value the value
	 * @param description the description
	 */
	private ExtraTimeItemNo(int value, String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * Value of.
	 *
	 * @param value the value
	 * @return the breakdown item no
	 */
	public static ExtraTimeItemNo valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (ExtraTimeItemNo val : ExtraTimeItemNo.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}
}
