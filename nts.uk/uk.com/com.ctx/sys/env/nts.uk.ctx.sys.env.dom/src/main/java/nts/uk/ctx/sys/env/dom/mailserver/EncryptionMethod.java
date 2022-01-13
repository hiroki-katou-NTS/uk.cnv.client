/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.dom.mailserver;

/**
 * The Enum EncryptionMethod.
 */
// 暗号化方式
public enum EncryptionMethod {

	/** The none. */
	NONE(0, "Enum_EncryptionMethod_NONE"),

	/** The ssl. */
	SSL(1, "Enum_EncryptionMethod_SSL"),

	/** The tsl. */
	TLS(2, "Enum_EncryptionMethod_TLS");

	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;

	/** The Constant values. */
	private final static EncryptionMethod[] values = EncryptionMethod.values();

	/**
	 * Instantiates a new rounding.
	 *
	 * @param value
	 *            the value
	 * @param nameId
	 *            the name id
	 */
	private EncryptionMethod(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

	/**
	 * Value of.
	 *
	 * @param value
	 *            the value
	 * @return the rounding
	 */
	public static EncryptionMethod valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (EncryptionMethod val : EncryptionMethod.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}
}
