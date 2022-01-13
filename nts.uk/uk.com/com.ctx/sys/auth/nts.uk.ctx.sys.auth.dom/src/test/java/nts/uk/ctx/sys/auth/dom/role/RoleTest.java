package nts.uk.ctx.sys.auth.dom.role;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import lombok.val;
import nts.arc.testing.assertion.NtsAssert;
/**
 * ロールのUTコード
 * @author lan_lt
 *
 */
public class RoleTest {
	private String roleId;
	private String companyId;
	private RoleCode roleCode;
	private RoleName roleName;
	private ContractCode contractCode;
	
	@Before
	public void initData() {
		this.roleId = "roleId";
		this.companyId = "companyId";
		this.roleCode = new RoleCode("roleCode");
		this.roleName = new RoleName("roleName");
		this.contractCode = new ContractCode("contractCode");
	}
	
	/**
	 * 担当区分　＝　一般
	 * 参照範囲　＝　部門（配下含む）
	 * 承認権限がある
	 */
	@Test
	public void createGeneralRole() {
		val employeeReferenceRange = EmployeeReferenceRange.DEPARTMENT_AND_CHILD;
		val approvalAuthority = Boolean.TRUE;
		{
			/**　ロール種類 = 個人情報**/
			val roleType = RoleType.PERSONAL_INFO;
			val generalRole = Role.createGeneralRoll(
						this.roleId, this.contractCode, this.companyId
					,	this.roleCode,	this.roleName
					,	roleType, employeeReferenceRange
					,	Optional.of(approvalAuthority)
					);
			
			assertThat(generalRole.getContractCode()).isEqualTo(this.contractCode);
			assertThat(generalRole.getCompanyId()).isEqualTo(this.companyId);
			
			assertThat(generalRole.getRoleId()).isEqualTo(this.roleId);
			assertThat(generalRole.getRoleCode()).isEqualTo(roleCode);
			assertThat(generalRole.getName()).isEqualTo(roleName);
			assertThat(generalRole.getRoleType()).isEqualTo(roleType);
			
			/** 担当区分　＝　一般　**/
			assertThat(generalRole.getAssignAtr()).isEqualTo(RoleAtr.GENERAL);
			assertThat(generalRole.getEmployeeReferenceRange()).isEqualTo(employeeReferenceRange);
			assertThat(generalRole.getApprovalAuthority().get()).isEqualTo(approvalAuthority);			
			
		}

		{
			/**　ロール種類 = 就業**/
			val roleType = RoleType.EMPLOYMENT;
			val roleAttendance = Role.createGeneralRoll(
					this.roleId, this.contractCode, this.companyId
				,	this.roleCode,	this.roleName
				,	RoleType.EMPLOYMENT, employeeReferenceRange
				,	Optional.of(approvalAuthority)
				);
			
			assertThat(roleAttendance.getContractCode()).isEqualTo(this.contractCode);
			assertThat(roleAttendance.getCompanyId()).isEqualTo(this.companyId);
			
			assertThat(roleAttendance.getRoleId()).isEqualTo(this.roleId);
			assertThat(roleAttendance.getRoleCode()).isEqualTo(roleCode);
			assertThat(roleAttendance.getName()).isEqualTo(roleName);
			assertThat(roleAttendance.getRoleType()).isEqualTo(roleType);
			
			/** 担当区分　＝　一般　**/
			assertThat(roleAttendance.getAssignAtr()).isEqualTo(RoleAtr.GENERAL);
			assertThat(roleAttendance.getEmployeeReferenceRange()).isEqualTo(employeeReferenceRange);
			assertThat(roleAttendance.getApprovalAuthority().get()).isEqualTo(approvalAuthority);
		}
		
	}
	
	/**
	 * ロール種類　＝　就業
	 * 担当区分　＝　一般
	 * 参照範囲　＝　全員
	 * 期待： runtime エラー
	 * 	 */
	@Test
	public void createGeneralRole_ReferenceRange_ALL_EMPLOYEE() {
		NtsAssert.systemError(() -> {
				Role.createGeneralRoll(
					this.roleId, this.contractCode, this.companyId
				,	this.roleCode,	this.roleName
				,	RoleType.PERSONAL_INFO, EmployeeReferenceRange.ALL_EMPLOYEE
				,	Optional.of(Boolean.TRUE)
				);	
		});
	}
	
	/**
	 * 担当区分 = 一般
	 * ロール種類 = 就業
	 * 承認権限がない
	 * 期待： runtime エラー
	 * 	 */
	@Test
	public void createGeneralRole_roleAttendance_approvalAuthority_empty() {
		NtsAssert.systemError(() -> {
				Role.createGeneralRoll(
					this.roleId, this.contractCode, this.companyId
				,	this.roleCode,	this.roleName
				,	RoleType.EMPLOYMENT, EmployeeReferenceRange.DEPARTMENT_AND_CHILD
				,	Optional.empty()
				);	
		});
	}
	
	/**
	 * 担当区分 = 担当
	 * ロール種類 = 就業
	 * 承認権限 = empty
	 */
	@Test
	public void createInChargeRole() {
		val roleType = RoleType.EMPLOYMENT;
		
		val inchargelRole = Role.createInChargeRoll(
					this.roleId,	this.contractCode
				,	this.companyId,	this.roleCode
				,	this.roleName,	roleType);
		
		assertThat(inchargelRole.getContractCode()).isEqualTo(this.contractCode);
		assertThat(inchargelRole.getCompanyId()).isEqualTo(this.companyId);
		
		assertThat(inchargelRole.getRoleId()).isEqualTo(this.roleId);
		assertThat(inchargelRole.getRoleCode()).isEqualTo(this.roleCode);
		assertThat(inchargelRole.getName()).isEqualTo(this.roleName);
		assertThat(inchargelRole.getRoleType()).isEqualTo(roleType);
		
		/** 担当区分　＝　担当　**/
		assertThat(inchargelRole.getAssignAtr()).isEqualTo(RoleAtr.INCHARGE);
		assertThat(inchargelRole.getEmployeeReferenceRange()).isEqualTo(EmployeeReferenceRange.ALL_EMPLOYEE);
		assertThat(inchargelRole.getApprovalAuthority()).isEmpty();

	}

}