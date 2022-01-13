/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.role;

import java.util.Optional;

/**
 * The Interface RoleSetMemento.
 */
public interface RoleSetMemento {
	
	/**
	 * Sets the role id.
	 *
	 * @param roleId the new role id
	 */
	void setRoleId(String roleId);

	/**
	 * Sets the role code.
	 *
	 * @param roleCode the new role code
	 */
	void setRoleCode(RoleCode roleCode);

	/**
	 * Sets the role type.
	 *
	 * @param roleType the new role type
	 */
	void setRoleType(RoleType roleType);

	/**
	 * Sets the employee reference range.
	 *
	 * @param employeeReferenceRange the new employee reference range
	 */
	void setEmployeeReferenceRange(EmployeeReferenceRange employeeReferenceRange);

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	void setName(RoleName name);

	/**
	 * Sets the contract code.
	 *
	 * @param contractCode the new contract code
	 */
	void setContractCode(ContractCode contractCode);

	/**
	 * Sets the assign atr.
	 *
	 * @param assignAtr the new assign atr
	 */
	void setAssignAtr(RoleAtr assignAtr);

	/**
	 * Sets the company id.
	 *
	 * @param companyId the new company id
	 */
	void setCompanyId(String companyId);

	void setApprovalAuthority(Optional<Boolean> bpprovalAuthority);
}
