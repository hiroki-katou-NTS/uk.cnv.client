package nts.uk.ctx.sys.auth.dom.roleset;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.uk.ctx.sys.auth.dom.role.RoleType;

public class RoleSetTest {
	/**
	 * 就業ロール  not empty
	 * 個人情報ロール not empty
	 */
	@Test
	public void createRoleSet_case_1() {
		String cid = "cid";
		String roleSetCd = "01";
		String roleSetName = "roleSet";
		Optional<String> attendanceRoleId = Optional.of("attendanceRoleId");
		Optional<String>  personInfoRoleId = Optional.of("personalInfoRoleId");
		RoleSet roleSet = RoleSet.create(cid, roleSetCd, roleSetName, attendanceRoleId, personInfoRoleId);
		assertThat(roleSet.getCompanyId()).isEqualTo(cid);
		assertThat(roleSet.getRoleSetCd().v()).isEqualTo(roleSetCd);
		assertThat(roleSet.getRoleSetName().v()).isEqualTo(roleSetName);
		assertThat(roleSet.getPersonInfRoleId().get()).isEqualTo(personInfoRoleId.get());
		assertThat(roleSet.getEmploymentRoleId().get()).isEqualTo(attendanceRoleId.get());
		assertThat(roleSet.getHRRoleId()).isEmpty();
		assertThat(roleSet.getMyNumberRoleId()).isEmpty();
		assertThat(roleSet.getOfficeHelperRoleId()).isEmpty();
		assertThat(roleSet.getSalaryRoleId()).isEmpty();
		
	} 
	
	/**
	 * 就業ロール = empty
	 * 個人情報ロール = empty
	 */
	@Test
	public void createRoleSet_case_2() {
		String cid = "cid";
		String roleSetCd = "01";
		String roleSetName = "roleSet";
		RoleSet roleSet = RoleSet.create(cid, roleSetCd, roleSetName, Optional.empty(), Optional.empty());
		assertThat(roleSet.getCompanyId()).isEqualTo(cid);
		assertThat(roleSet.getRoleSetCd().v()).isEqualTo(roleSetCd);
		assertThat(roleSet.getRoleSetName().v()).isEqualTo(roleSetName);
		assertThat(roleSet.getPersonInfRoleId()).isEmpty();
		assertThat(roleSet.getEmploymentRoleId()).isEmpty();
		assertThat(roleSet.getHRRoleId()).isEmpty();
		assertThat(roleSet.getMyNumberRoleId()).isEmpty();
		assertThat(roleSet.getOfficeHelperRoleId()).isEmpty();
		assertThat(roleSet.getSalaryRoleId()).isEmpty();
		
	} 
	
	/**
	 * get roleID by roleType
	 * 全部ロールIDが ない場合は　empty を返す
	 */
	@Test
	public void getRoleIDByRoleType_empty() {
		RoleSet roleSet = Helper.createRoleSet(Optional.empty(), Optional.empty()
				, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		
		//就業ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.EMPLOYMENT);
			assertThat(result).isEmpty();
		}

		//個人情報ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.PERSONAL_INFO);
			assertThat(result).isEmpty();
		}
		
		//給与ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.SALARY);
			assertThat(result).isEmpty();
		}
		
		//人事ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.HUMAN_RESOURCE);
			assertThat(result).isEmpty();
		}		
		
		//マイナンバー
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.MY_NUMBER);
			assertThat(result).isEmpty();
		}
		
		//オフィスヘルパ
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.OFFICE_HELPER);
			assertThat(result).isEmpty();
		}
		
	}
	
	/**
	 * get roleID by roleType
	 * 全部ロールIDがnot empty
	 */
	@Test
	public void getRoleIDByRoleType() {
		Optional<String> attendanceRoleId = Optional.of("attendanceRoleId");
		Optional<String>  personInfRoleId = Optional.of("personalInfoRoleId");
		Optional<String>  salaryRoleId = Optional.of("salaryRoleId");
		Optional<String>  hRRoleId = Optional.of("hRRoleId");
		Optional<String>  myNumberRoleId = Optional.of("myNumberRoleId");
		Optional<String>  officeHelperRoleId = Optional.of("officeHelperRoleId");
		RoleSet roleSet = Helper.createRoleSet(attendanceRoleId, personInfRoleId
				, salaryRoleId, hRRoleId, myNumberRoleId, officeHelperRoleId);
		
		//就業ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.EMPLOYMENT);
			assertThat(result).isEqualTo(attendanceRoleId.get());
		}

		//個人情報ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.PERSONAL_INFO);
			assertThat(result).isEqualTo(personInfRoleId.get());
		}
		
		//給与ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.SALARY);
			assertThat(result).isEqualTo(salaryRoleId.get());
		}
		
		//人事ロール
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.HUMAN_RESOURCE);
			assertThat(result).isEqualTo(hRRoleId.get());
		}		
		
		//マイナンバー
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.MY_NUMBER);
			assertThat(result).isEqualTo(myNumberRoleId.get());
		}
		
		//オフィスヘルパ
		{
			String result = roleSet.getRoleIDByRoleType(RoleType.OFFICE_HELPER);
			assertThat(result).isEqualTo(officeHelperRoleId.get());
		}
		
	}
	
	public static class Helper{
		
		public  static RoleSet createRoleSet(Optional<String> attendanceRoleId
				, Optional<String> personInfRoleId
				, Optional<String> salaryRoleId
				, Optional<String> hRRoleId
				, Optional<String> myNumberRoleId
				, Optional<String> officeHelperRoleId) {
			
			return new RoleSet("cid",	new RoleSetCode("roleSetCode")
					,	new RoleSetName("roleSetName")
					,	attendanceRoleId,	personInfRoleId,	salaryRoleId
					,	hRRoleId,	myNumberRoleId,	officeHelperRoleId);
		}
	}
}
