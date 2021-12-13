/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.role;

/**
 * The Enum RoleType.
 */
public enum RoleType {

	/** The system manager. */
	// システム管理者
	SYSTEM_MANAGER(0, "Enum_RoleType_systemManager", "システム管理者"),

	/** The company manager. */
	// 会社管理者
	COMPANY_MANAGER(1, "Enum_RoleType_companyManager", "会社管理者"),

	/** The group comapny manager. */
	// グループ会社管理者
	GROUP_COMAPNY_MANAGER(2, "Enum_RoleType_groupCompanyManager", "グループ会社管理者"),

	/** The employment. */
	// 就業
	EMPLOYMENT(3, "Enum_RoleType_employment", "就業"),

	/** The salary. */
	// 給与
	SALARY(4, "Enum_RoleType_salary", "給与"),

	/** The human resource. */
	// 人事
	HUMAN_RESOURCE(5, "Enum_RoleType_humanResource", "人事"),

	/** The office helper. */
	// オフィスヘルパー
	OFFICE_HELPER(6, "Enum_RoleType_officeHelper", "オフィスヘルパー"),

	/** The my number. */
	// マイナンバー
	MY_NUMBER(7, "Enum_RoleType_myNumber", "マイナンバー"),

	/** The personal info. */
	// 個人情報
	PERSONAL_INFO(8, "Enum_RoleType_personalInfo", "個人情報");

	/** The value. */
	public final int value;

	/** The name id. */
	public final String nameId;

	/** The description. */
	public final String description;

	/** The Constant values. */
	private final static RoleType[] values = RoleType.values();

	/**
	 * Instantiates a new role type.
	 *
	 * @param value the value
	 * @param nameId the name id
	 * @param description the description
	 */
	private RoleType(int value, String nameId, String description) {
		this.value = value;
		this.nameId = nameId;
		this.description = description;
	}

	/**
	 * Value of.
	 *
	 * @param value the value
	 * @return the role type
	 */
	public static RoleType valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (RoleType val : RoleType.values) {
			if (val.value == value) {
				return val;
			}
		}

		// Not found.
		return null;
	}
	
	/**
	 * 管理者ロールか
	 * @return
	 */
	public boolean isManagerRole() {
		switch (this) {
			case SYSTEM_MANAGER:
			case COMPANY_MANAGER:
			case GROUP_COMAPNY_MANAGER:
				return true;
			default:
				return false;
		}
	}
}
