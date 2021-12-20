package nts.uk.ctx.sys.auth.dom.role;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class RoleTypeTest {
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = 会社管理者
	 * excepted: true
	 */
	@Test
	public void testIsManagerRole_company_manager() {
		
		val roleType = RoleType.COMPANY_MANAGER;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isTrue();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = システム管理者
	 * excepted: true
	 */
	@Test
	public void testIsManagerRole_system_manager() {
		
		val roleType = RoleType.SYSTEM_MANAGER;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isTrue();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = グループ会社管理者
	 * excepted: true
	 */
	@Test
	public void testIsManagerRole_groupCompany_manager() {
		
		val roleType = RoleType.GROUP_COMAPNY_MANAGER;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isTrue();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = 就業
	 * excepted: false
	 */
	@Test
	public void testIsManagerRole_employment() {
		
		val roleType = RoleType.EMPLOYMENT;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isFalse();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = 給与
	 * excepted: false
	 */
	@Test
	public void testIsManagerRole_salary() {
		
		val roleType = RoleType.SALARY;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isFalse();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = 人事
	 * excepted: false
	 */
	@Test
	public void testIsManagerRole_human_resource() {
		
		val roleType = RoleType.HUMAN_RESOURCE;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isFalse();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = オフィスヘルパー
	 * excepted: false
	 */
	@Test
	public void testIsManagerRole_office_helper() {
		
		val roleType = RoleType.OFFICE_HELPER;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isFalse();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = マイナンバー
	 * excepted: false
	 */
	@Test
	public void testIsManagerRole_myNumber() {
		
		val roleType = RoleType.MY_NUMBER;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isFalse();
		
	}
	
	/**
	 * target: isManagerRole
	 * pattern: ロール種類 = 個人情報
	 * excepted: false
	 */
	@Test
	public void testIsManagerRole_personalInfo() {
		
		val roleType = RoleType.PERSONAL_INFO;
		
		//Act
		val actual = roleType.isManagerRole();
		
		//Assert
		assertThat( actual ).isFalse();
		
	}

}
