package nts.uk.ctx.at.shared.dom.employeeworkway.medicalworkstyle;

import lombok.AllArgsConstructor;

/**
 * @author ThanhNX 免許区分
 */
@AllArgsConstructor
public enum LicenseClassification {

	/**
	 * 看護師
	 */
	NURSE(0, "Enum_LicenseClassification_NURSE"),

	/**
	 * 准看護師
	 */
	NURSE_ASSOCIATE(1, "Enum_LicenseClassification_NURSE_ASSOCIATE"),

	/**
	 * 看護補助者
	 */
	NURSE_ASSIST(2, "Enum_LicenseClassification_NURSE_ASSIST");

	public final int value;

	public final String nameId;

	private final static LicenseClassification[] values = LicenseClassification.values();

	public static LicenseClassification valueOf(Integer value) {
		if (value == null) {
			return null;
		}

		for (LicenseClassification val : LicenseClassification.values) {
			if (val.value == value) {
				return val;
			}
		}

		return null;
	}
	
}