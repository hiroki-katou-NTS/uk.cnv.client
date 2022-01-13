package nts.uk.ctx.sys.portal.dom.enums;

/**
 * The Enum UseType.
 */
public enum UseDivision {

	/** The Not use. */
	NotUse(0, "利用しない"),

	/** The Use. */
	Use(1, "利用する");

	/** The value. */
	public int value;
	
	public String nameId;

	/** The Constant values. */
	private final static UseDivision[] values = UseDivision.values();

	/**
	 * Instantiates a new use type.
	 *
	 * @param value the value
	 */
	private UseDivision(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
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