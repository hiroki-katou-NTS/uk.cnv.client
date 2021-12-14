package nts.uk.ctx.sys.auth.app.command.role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.auth.dom.role.ContractCode;
import nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleCode;
import nts.uk.ctx.sys.auth.dom.role.RoleName;
import nts.uk.ctx.sys.auth.dom.role.RoleType;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRoleCommand {
	/** The role id. */
	// Id
	private String roleId;

	/** The role code. */
	// コード RoleCode
	private String roleCode;

	/** The role type. */
	// ロール種類 RoleType
	private int roleType;

	/** The employee reference range. */
	// 参照範囲 EmployeeReferenceRange
	private int employeeReferenceRange;

	/** The name. */
	// ロール名称 RoleName
	private String name;

	/** The contract code. */
	// 契約コード ContractCode
	private String contractCode;

	/** The assign atr. */
	// 担当区分 RoleAtr
	private int assignAtr;

	/** The company id. */
	// 会社ID
	private String companyId;

	public UpdateRoleCommand(String roleId, String roleCode, int roleType, int employeeReferenceRange, String name,
			String contractCode, int assignAtr, String companyId) {
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
	
	public Role toDomain() {
		
		return new Role(
				this.roleId,
				new RoleCode(this.roleCode),
				EnumAdaptor.valueOf(this.roleType,RoleType.class),
				EnumAdaptor.valueOf(this.employeeReferenceRange,EmployeeReferenceRange.class),
				new RoleName(this.name),
				new ContractCode(this.contractCode),
				EnumAdaptor.valueOf(this.assignAtr,RoleAtr.class),
				this.companyId
				);
	}
	
}
