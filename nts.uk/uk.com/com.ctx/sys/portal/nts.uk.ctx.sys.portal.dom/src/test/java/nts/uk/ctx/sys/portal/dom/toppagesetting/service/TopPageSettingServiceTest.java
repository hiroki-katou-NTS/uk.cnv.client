package nts.uk.ctx.sys.portal.dom.toppagesetting.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.sys.portal.dom.enums.MenuClassification;
import nts.uk.ctx.sys.portal.dom.enums.System;
import nts.uk.ctx.sys.portal.dom.toppagesetting.LoginMenuCode;
import nts.uk.ctx.sys.portal.dom.toppagesetting.MenuLogin;
import nts.uk.ctx.sys.portal.dom.toppagesetting.SwitchingDate;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopMenuCode;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPagePersonSetting;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageRoleSetting;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageSettings;
import nts.uk.ctx.sys.portal.dom.webmenu.webmenulinking.RoleSetCode;

@RunWith(JMockit.class)
public class TopPageSettingServiceTest {
	
	private static final String COMPANY_ID = "companyId";
	private static final String EMPLOYEE_ID = "employeeId";
	private static final String ROLE_SET_CODE = "roleSetCode";
	private static final String TOP_MENU_CODE = "topMenuCode";
	private static final String LOGIN_MENU_CODE = "loginMenuCode";
	
	@Injectable
	private TopPageSettingService.Require require;
	
	@Mocked
	private static TopPageSettings topPageSetting = new TopPageSettings(
			new TopMenuCode(TOP_MENU_CODE),
			new MenuLogin(
				EnumAdaptor.valueOf(0, System.class),
				EnumAdaptor.valueOf(0, MenuClassification.class), 
				new LoginMenuCode(LOGIN_MENU_CODE)
			)
		);
	
	/**
	 * Test get top page settings 1.
	 * Test get topPageSettings by TopPageRoleSetting (TopPagePersonSetting is null and TopPageRoleSetting is not null)
	 */
	@Test
	public void testGetTopPageSettings1() {
		Optional<TopPageRoleSetting> topPageRoleSetting = Optional.of(new TopPageRoleSetting(
				COMPANY_ID, 
				new RoleSetCode(ROLE_SET_CODE),
				new LoginMenuCode(LOGIN_MENU_CODE), 
				new TopMenuCode(TOP_MENU_CODE), 
				EnumAdaptor.valueOf(0, MenuClassification.class), 
				EnumAdaptor.valueOf(0, System.class)));
		new Expectations() {
			{
				require.getTopPagePersonSetting(COMPANY_ID, EMPLOYEE_ID);
				result = Optional.empty();
				
				require.getRoleSetCode();
				result = Optional.of(ROLE_SET_CODE);
				
				require.getTopPageRoleSetting(COMPANY_ID, ROLE_SET_CODE);
				result = topPageRoleSetting;
			}
		};
		//Execute
		val instance = new TopPageSettingService();
		@SuppressWarnings("unchecked")
		val result = (Optional<TopPageSettings>) NtsAssert.Invoke.privateMethod(
				instance, 
				"getTopPageSettings", 
				require,
				COMPANY_ID,
				EMPLOYEE_ID);
		//Assertion
		assertThat(result).isNotEmpty();
	}
	
	/**
	 * Test get top page settings 2.
	 * Test get topPageSettings by TopPagePersonSetting (TopPagePersonSetting is not null)
	 */
	@Test
	public void testGetTopPageSettings2() {
		Optional<TopPagePersonSetting> topPagePersonSetting = Optional.of(new TopPagePersonSetting(
				EMPLOYEE_ID, 
				new LoginMenuCode(LOGIN_MENU_CODE), 
				new TopMenuCode(TOP_MENU_CODE), 
				EnumAdaptor.valueOf(0, MenuClassification.class), 
				EnumAdaptor.valueOf(0, System.class)));
		new Expectations() {
			{
				require.getTopPagePersonSetting(COMPANY_ID, EMPLOYEE_ID);
				result = topPagePersonSetting;
			}
		};
		
		//Execute
		val instance = new TopPageSettingService();
		@SuppressWarnings("unchecked")
		val result = (Optional<TopPageSettings>) NtsAssert.Invoke.privateMethod(
				instance, 
				"getTopPageSettings", 
				require,
				COMPANY_ID,
				EMPLOYEE_ID);
		//Assertion
		assertThat(result).isNotEmpty();
	}
	
	/**
	 * Test get top page settings 3.
	 * Test get topPageSettings if TopPagePersonSetting and TopPageRoleSetting are null
	 */
	@Test
	public void testGetTopPageSettings3() {
		new Expectations() {
			{
				require.getTopPagePersonSetting(COMPANY_ID, EMPLOYEE_ID);
				result = Optional.empty();
				
				require.getRoleSetCode();
				result = Optional.of(ROLE_SET_CODE);
				
				require.getTopPageRoleSetting(COMPANY_ID, ROLE_SET_CODE);
				result = Optional.empty();
			}
		};

		//Execute
		val instance = new TopPageSettingService();
		val testResult = NtsAssert.Invoke.privateMethod(
				instance, 
				"getTopPageSettings", 
				require,
				COMPANY_ID,
				EMPLOYEE_ID);
		@SuppressWarnings("unchecked")
		val result = (Optional<TopPageSettings>) testResult;
		//Assertion
		assertThat(result).isEmpty();
	}
	
	/**
	 * Test get top page settings 4.
	 * Test get topPageSettings if TopPagePersonSetting and RoleSetCode are null
	 */
	@Test
	public void testGetTopPageSettings4() {
		new Expectations() {
			{
				require.getTopPagePersonSetting(COMPANY_ID, EMPLOYEE_ID);
				result = Optional.empty();
				
				require.getRoleSetCode();
				result = Optional.empty();
			}
		};

		//Execute
		val instance = new TopPageSettingService();
		val testResult = NtsAssert.Invoke.privateMethod(
				instance, 
				"getTopPageSettings", 
				require,
				COMPANY_ID,
				EMPLOYEE_ID);
		@SuppressWarnings("unchecked")
		val result = (Optional<TopPageSettings>) testResult;
		//Assertion
		assertThat(result).isEmpty();
	}
	
}
