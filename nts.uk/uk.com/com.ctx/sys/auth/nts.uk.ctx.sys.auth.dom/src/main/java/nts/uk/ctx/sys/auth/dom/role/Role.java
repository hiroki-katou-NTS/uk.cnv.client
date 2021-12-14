/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.role;

import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.text.IdentifierUtil;

/**
 * ロール
 * The Class Role.
 */
@Getter
public class Role extends AggregateRoot {

	/** The role id. */
	// Id
	private String roleId;

	/** The role code. */
	// コード
	private RoleCode roleCode;

	/** The role type. */
	// ロール種類
	private RoleType roleType;

	/** The employee reference range. */
	// 参照範囲
	private EmployeeReferenceRange employeeReferenceRange;

	/** The name. */
	// ロール名称
	private RoleName name;

	/** The contract code. */
	// 契約コード
	private ContractCode contractCode;

	/** The assign atr. */
	// 担当区分
	private RoleAtr assignAtr;

	/** The company id. */
	// 会社ID
	private String companyId;

	/**
	 * Instantiates a new role.
	 *
	 * @param memento the memento
	 */
	public Role(RoleGetMemento memento) {
		if(memento.getRoleId() == null || memento.getRoleId().equals("")){
			this.roleId = IdentifierUtil.randomUniqueId();
		} else{
			this.roleId = memento.getRoleId();
		}
		this.roleCode = memento.getRoleCode();
		this.roleType = memento.getRoleType();
		this.employeeReferenceRange = memento.getEmployeeReferenceRange();
		this.name = memento.getName();
		this.contractCode = memento.getContractCode();
		this.assignAtr = memento.getAssignAtr();
		this.companyId = memento.getCompanyId();
	}
	
	/**
	 * Checks if is system manager.
	 *
	 * @return true, if is system manager
	 */
	public boolean isSystemManager() {
		return this.roleType.value == RoleType.SYSTEM_MANAGER.value;
	}
	
	/**
	 * Checks if is general.
	 *
	 * @return true, if is general
	 */
	public boolean isGeneral() {
		return this.assignAtr.value == RoleAtr.GENERAL.value;
	}
	
	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(RoleSetMemento memento) {
		memento.setRoleId(this.roleId);
		memento.setRoleCode(this.roleCode);
		memento.setRoleType(this.roleType);
		memento.setEmployeeReferenceRange(this.employeeReferenceRange);
		memento.setName(this.name);
		memento.setContractCode(this.contractCode);
		memento.setAssignAtr(this.assignAtr);
		memento.setCompanyId(this.companyId);
	}
		
	public boolean canInsert(){
		if(this.roleType == RoleType.SYSTEM_MANAGER) throw new BusinessException("MSG_501");
		return true;
	}
	
	public boolean canUpdate(){
		if(this.roleType == RoleType.SYSTEM_MANAGER) throw new BusinessException("MSG_502");
		return true;
	}
	
	public boolean canDelete(){
		if(this.roleType == RoleType.SYSTEM_MANAGER) throw new BusinessException("MSG_503");
		return true;
	}

	public Role(String roleId,RoleCode roleCode, RoleType roleType, EmployeeReferenceRange employeeReferenceRange,
			RoleName name, ContractCode contractCode, RoleAtr assignAtr, String companyId) {
		super();
		this.roleId = roleId;
		this.roleCode = roleCode;
		this.roleType = roleType;
		this.employeeReferenceRange = employeeReferenceRange;
		this.name = name;
		this.contractCode = contractCode;
		this.assignAtr = assignAtr;
		this.companyId = companyId;
	}
	
}
