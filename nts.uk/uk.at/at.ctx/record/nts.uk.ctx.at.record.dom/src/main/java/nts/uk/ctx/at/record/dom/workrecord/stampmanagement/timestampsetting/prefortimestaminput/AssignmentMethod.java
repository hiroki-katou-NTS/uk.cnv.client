package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

/**
 * 作業指定方法
 * @author chungnt
 *
 */

public enum AssignmentMethod {

	/** 指定なし */
	UNSPECIFIED(0,"指定なし"),

	/** 打刻時に選択 */
	SELECT_AT_THE_TIME_OF_STAMPING(1,"打刻時に選択");
	
	/** The value. */
	public int value;
	
	/** The value. */
	public String nameId;

	/** The Constant values. */
	private final static AssignmentMethod[] values = AssignmentMethod.values();

	/**
	 * Instantiates a new closure id.
	 *
	 * @param value
	 *            the value
	 * @param description
	 *            the description
	 */
	private AssignmentMethod(int value,String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

	/**
	 * Value of.
	 *
	 * @param value
	 *            the value
	 * @return the use division
	 */
	public static AssignmentMethod valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (AssignmentMethod val : AssignmentMethod.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}
}
