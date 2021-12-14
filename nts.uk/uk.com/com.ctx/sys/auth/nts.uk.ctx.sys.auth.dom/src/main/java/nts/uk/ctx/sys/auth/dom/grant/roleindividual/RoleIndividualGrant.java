/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.grant.roleindividual;

import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * ロール個人別付与
 * The Class RoleIndividualGrant.
 */
@Getter
@Setter

public class RoleIndividualGrant extends AggregateRoot {

	/** The user id. */
	// ユーザID
	private String userId;

	/** The role id. */
	// ロールID
	private String roleId;

	/** The company id. */
	// 会社ID
	private String companyId;

	/** The role type. */
	// ロール種類
	private RoleType roleType;

	/** The valid period. */
	// 有効期間
	private DatePeriod validPeriod;
	
	public RoleIndividualGrant(String userId, String roleId, String companyId, RoleType roleType, DatePeriod validPeriod) {
		super();
		this.userId = userId;
		this.roleId = roleId;
		this.companyId = companyId;
		this.roleType = roleType;
		this.validPeriod = validPeriod;
	}
	
	public static RoleIndividualGrant createFromJavaType(String userId, String roleId, String companyId,int roleType, GeneralDate validPeriodStart,GeneralDate validPeriodEnd) {
		return new RoleIndividualGrant(userId,
			/**roleID*/
			roleId,
			companyId,
			EnumAdaptor.valueOf(roleType, RoleType.class),
			new  DatePeriod(validPeriodStart, validPeriodEnd) );
	}

}
