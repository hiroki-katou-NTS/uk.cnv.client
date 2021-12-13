/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.role;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.AggregateRoot;
import nts.gul.text.IdentifierUtil;

/**
 * ロール
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.権限管理.ロール.ロール
 */
@Getter
@AllArgsConstructor
public class Role extends AggregateRoot {

	/** The role id. */
	// Id
	private String roleId;
	
	/** The contract code. */
	// 契約コード
	private ContractCode contractCode;
	
	/** The company id. */
	// 会社ID
	private String companyId;

	/** The role code. */
	// コード
	private RoleCode roleCode;
	
	/** The name. */
	// ロール名称
	private RoleName name;

	/** The role type. */
	// ロール種類
	private RoleType roleType;

	/** The assign atr. */
	// 担当区分
	private RoleAtr assignAtr;

	/** The employee reference range. */
	// 参照範囲
	private EmployeeReferenceRange employeeReferenceRange;
	
	/** 承認権限*/
	private Optional<Boolean> approvalAuthority;

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
		this.approvalAuthority = memento.getApprovalAuthority();
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
	
	/**
	 * 一般ロールを作る
	 * @param roleId ロールID
	 * @param contractCode 契約コード
	 * @param companyId 会社ID
	 * @param roleCode ロールコード
	 * @param roleName ロール名称	 
	 * @param roleType ロール種類
	 * @param employeeReferenceRange 参照範囲
	 * @param approvalAuthority 承認権限
	 * @return
	 */
	public static Role createGeneralRoll(String roleId
			,	ContractCode contractCode,	String companyId
			,	RoleCode roleCode,	RoleName roleName
			,	RoleType roleType,	EmployeeReferenceRange employeeReferenceRange
			,	Optional<Boolean> approvalAuthority) {
		
		if(employeeReferenceRange == EmployeeReferenceRange.ALL_EMPLOYEE) {
			throw new RuntimeException("担当区分が一般だった、参照範囲が全社員場合ダメです！");
		}
		
		if(roleType == RoleType.EMPLOYMENT && !approvalAuthority.isPresent()) {
			throw new RuntimeException("担当区分 が一般とロール種類 が 就業の場合は、承認権限が必要です。！");
		}
		
		return new Role(	roleId,		contractCode,
							companyId,	roleCode,
							roleName,	roleType,	
							RoleAtr.GENERAL,	employeeReferenceRange,
							approvalAuthority);
	}
	
	/**
	 * 担当ロールを作る
	 * @param roleId ロールID
	 * @param contractCode 契約コード
	 * @param companyId 会社ID
	 * @param roleCode ロールコード
	 * @param roleName ロール名称
	 * @param roleType ロール種類
	 * @return
	 */
	public static Role createInChargeRoll(String roleId,	ContractCode contractCode
			,	String companyId,	RoleCode roleCode
			,	RoleName roleName,	RoleType roleType) {
		
		return new Role(	roleId,		contractCode,
				companyId,	roleCode,
				roleName,	roleType,	
				RoleAtr.INCHARGE,	EmployeeReferenceRange.ALL_EMPLOYEE,
				Optional.empty());
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
		memento.setApprovalAuthority(this.approvalAuthority);
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


}
