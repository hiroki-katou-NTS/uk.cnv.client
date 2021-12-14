package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import lombok.AllArgsConstructor;

/**
 * 社員検索の呼び出し元システム種類
 * 
 */
@AllArgsConstructor
public enum EmployeeSearchCallSystemType {

	/** 就業 */
	EMPLOYMENT(0),

	/** 人事 */
	HUMAN_RESOURCE(1),

	/** 給与 */
	SALARY(2),

	/** 個人情報 */
	PERSONAL_INFO(3);

	/** The value. */
	public final int value;

}
