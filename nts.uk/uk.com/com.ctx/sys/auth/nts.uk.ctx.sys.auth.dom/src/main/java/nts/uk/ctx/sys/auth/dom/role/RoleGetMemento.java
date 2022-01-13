/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.role;

import java.util.Optional;

/**
 * The Interface RoleGetMemento.
 */
public interface RoleGetMemento {
	
	/**
	 * Gets the role id.
	 *
	 * @return the role id
	 */
	String getRoleId();

	/**
	 * Gets the role code.
	 *
	 * @return the role code
	 */
	RoleCode getRoleCode();

	/**
	 * Gets the role type.
	 *
	 * @return the role type
	 */
	RoleType getRoleType();

	/**
	 * Gets the employee reference range.
	 *
	 * @return the employee reference range
	 */
	EmployeeReferenceRange getEmployeeReferenceRange();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	RoleName getName();

	/**
	 * Gets the contract code.
	 *
	 * @return the contract code
	 */
	ContractCode getContractCode();

	/**
	 * Gets the assign atr.
	 *
	 * @return the assign atr
	 */
	RoleAtr getAssignAtr();

	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	String getCompanyId();

	/**
	 * Get ApprovalAuthority
	 *
	 * @return ApprovalAuthority
	 */
	Optional<Boolean> getApprovalAuthority();
}
