package nts.uk.ctx.sys.auth.app.command.person.role;

import lombok.Data;
import nts.uk.ctx.sys.auth.dom.role.ContractCode;
import nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleAtr;
import nts.uk.ctx.sys.auth.dom.role.RoleCode;
import nts.uk.ctx.sys.auth.dom.role.RoleGetMemento;
import nts.uk.ctx.sys.auth.dom.role.RoleName;
import nts.uk.ctx.sys.auth.dom.role.RoleType;

import java.util.Optional;

@Data
public class SavePersonRoleCommand  {
	
	private Boolean createMode;
	/** The role id. */
	// Id
	private String roleId;

	/** The role code. */
	// コード
	private String roleCode;

	/** The employee reference range. */
	// 参照範囲
	private Integer employeeReferenceRange;

	/** The name. */
	// ロール名称
	private String name;
	/** The assign atr. */
	// 担当区分
	private Integer assignAtr;
	
	/** 未来日参照権限 */
	private Boolean referFutureDate;
	
	public Role toDomain(String companyId,String contractCode){
		return new Role(new RoleGetMementoImpl(companyId, contractCode, this));
	}
	
	public class RoleGetMementoImpl implements RoleGetMemento{
		
		private String companyId;
		private String contractCode;
		private SavePersonRoleCommand command;
		
		public RoleGetMementoImpl(String companyId,String contractCode, SavePersonRoleCommand command){
			this.companyId= companyId;
			this.contractCode= contractCode;
			this.command= command;
		}

		@Override
		public String getRoleId() {
			return this.command.getRoleId();
		}

		@Override
		public RoleCode getRoleCode() {
			return new  RoleCode(this.command.getRoleCode());
		}

		@Override
		public RoleType getRoleType() {
			return RoleType.PERSONAL_INFO;
		}

		@Override
		public EmployeeReferenceRange getEmployeeReferenceRange() {
			return EmployeeReferenceRange.valueOf(this.command.getEmployeeReferenceRange());
		}

		@Override
		public RoleName getName() {
			return new RoleName(this.command.getName());
		}

		@Override
		public ContractCode getContractCode() {
			return new ContractCode(this.contractCode);
		}

		@Override
		public RoleAtr getAssignAtr() {
			return RoleAtr.valueOf(this.command.getAssignAtr());
		}

		@Override
		public String getCompanyId() {
			return this.companyId;		
		}

		@Override
		public Optional<Boolean> getApprovalAuthority() {
			return Optional.empty();
		}

	}
}
