package nts.uk.ctx.sys.portal.dom.enums;

/**
 * The Enum UseType.
 */
public enum UseDivision {

	/** The Not use. */
	NotUse(0),

	/** The Use. */
	Use(1);

	/** The value. */
	public int value;

	/** The Constant values. */
	private final static UseDivision[] values = UseDivision.values();

	/**
	 * Instantiates a new use type.
	 *
	 * @param value the value
	 */
	private UseDivision(int value) {
		this.value = value;
	}

	/**
	 * Value of.
	 *
	 * @param value the value
	 * @return the use type
	 */
	public static UseDivision valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (UseDivision val : UseDivision.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}
}