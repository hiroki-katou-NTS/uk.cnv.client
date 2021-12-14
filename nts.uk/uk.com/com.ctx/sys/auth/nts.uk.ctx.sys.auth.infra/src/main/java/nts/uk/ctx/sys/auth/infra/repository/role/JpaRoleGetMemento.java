/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.infra.repository.role;

import nts.uk.ctx.sys.auth.dom.role.ContractCode;
import nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleCode;
import nts.uk.ctx.sys.auth.dom.role.RoleGetMemento;
import nts.uk.ctx.sys.auth.dom.role.RoleName;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.auth.infra.entity.role.SacmtRole;

/**
 * The Class JpaRoleGetMemento.
 */
public class JpaRoleGetMemento implements RoleGetMemento {

	/** The entity. */
	private SacmtRole entity;

	/**
	 * Instantiates a new jpa role get memento.
	 *
	 * @param sacmtRole the sacmt role
	 */
	public JpaRoleGetMemento(SacmtRole sacmtRole) {
		this.entity = sacmtRole;
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getRoleId()
	 */
	@Override
	public String getRoleId() {
		return this.entity.getRoleId();
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getRoleCode()
	 */
	@Override
	public RoleCode getRoleCode() {
		return new RoleCode(this.entity.getCode());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getRoleType()
	 */
	@Override
	public RoleType getRoleType() {
		return RoleType.valueOf(this.entity.getRoleType());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getEmployeeReferenceRange()
	 */
	@Override
	public EmployeeReferenceRange getEmployeeReferenceRange() {
		return EmployeeReferenceRange.valueOf(this.entity.getReferenceRange());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getName()
	 */
	@Override
	public RoleName getName() {
		return new RoleName(this.entity.getName());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getContractCode()
	 */
	@Override
	public ContractCode getContractCode() {
		return new ContractCode(this.entity.contractCd);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getAssignAtr()
	 */
	@Override
	public RoleAtr getAssignAtr() {
		return RoleAtr.valueOf(this.entity.getAssignAtr());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.auth.dom.role.RoleGetMemento#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.entity.getCid();
	}

}
