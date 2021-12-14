/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.workflow.dom.approvermanagement.approvalroot.output;

/**
 * The Enum JobClassification.
 */
// 本務兼務区分
public enum JobClassification {

	/** The Principal. */
	// 本務
	Principal(0, "Enum_JobClassification_Principal"),

	/** The Concurrent. */
	// 兼務
	Concurrent(1, "Enum_JobClassification_Concurrent");

	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;

	/** The Constant values. */

	/** The Constant values. */
	private final static JobClassification[] values = JobClassification.values();

	/**
	 * Instantiates a new job classification.
	 *
	 * @param value
	 *            the value
	 * @param nameId
	 *            the name id
	 */
	private JobClassification(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

	/**
	 * Value of.
	 *
	 * @param value
	 *            the value
	 * @return the job classification
	 */
	public static JobClassification valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (JobClassification val : JobClassification.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}
}
