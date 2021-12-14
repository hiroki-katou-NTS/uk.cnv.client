package nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace;

import lombok.AllArgsConstructor;

/**
 * システム区分
 * @author dan_pv
 *
 */
@AllArgsConstructor
public enum CCG001SystemType {

	/** 個人情報 */
	PERSONAL_INFORMATION(1),

	/** 就業 */
	EMPLOYMENT(2),

	/** 給与 */
	SALARY(3),

	/** 人事 */
	HUMAN_RESOURCES(4),

	/** 管理者 */
	ADMINISTRATOR(5);

	/** The value. */
	public final int value;

}