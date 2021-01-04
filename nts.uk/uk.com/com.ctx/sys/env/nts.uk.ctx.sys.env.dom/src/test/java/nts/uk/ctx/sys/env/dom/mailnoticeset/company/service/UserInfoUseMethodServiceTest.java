package nts.uk.ctx.sys.env.dom.mailnoticeset.company.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.ContactUsageSetting;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.UserInformationUseMethod;
import nts.uk.ctx.sys.env.dom.mailnoticeset.company.service.UserInformationUseMethodService.Require;
import nts.uk.ctx.sys.env.dom.mailnoticeset.dto.EmployeeInfoContactImport;
import nts.uk.ctx.sys.env.dom.mailnoticeset.dto.PersonContactImport;

@RunWith(JMockit.class)
public class UserInfoUseMethodServiceTest {

	@Injectable
	private Require require;

	/**
	 * Test [pvt-1]利用チェックする: No1
	 */
	@Test
	public void testCheckUsageUse() {
		val res = UserInformationUseMethodService.checkUsage(ContactUsageSetting.USE, Optional.of(true));
		assertThat(res).isTrue();
	}
	
	/**
	 * Test [pvt-1]利用チェックする: No2
	 */
	@Test
	public void testCheckUsageNotUse() {
		val res = UserInformationUseMethodService.checkUsage(ContactUsageSetting.DO_NOT_USE, Optional.of(true));
		assertThat(res).isFalse();
	}
	
	/**
	 * Test [pvt-1]利用チェックする: No3
	 */
	@Test
	public void testCheckUsageIndividualSelect() {
		val res = UserInformationUseMethodService.checkUsage(ContactUsageSetting.INDIVIDUAL_SELECT, Optional.of(true));
		assertThat(res).isTrue();
	}
	
	/**
	 * Test [pvt-1]利用チェックする: No4
	 */
	@Test
	public void testCheckUsageIndividualSelect2() {
		val res = UserInformationUseMethodService.checkUsage(ContactUsageSetting.INDIVIDUAL_SELECT, Optional.of(false));
		assertThat(res).isFalse();
	}
	
	/**
	 * Test [pvt-1]利用チェックする: No5
	 */
	@Test
	public void testCheckUsageIndividualSelect3() {
		val res = UserInformationUseMethodService.checkUsage(ContactUsageSetting.INDIVIDUAL_SELECT, Optional.empty());
		assertThat(res).isFalse();
	}
	
	/**
	 * Test 連絡先情報を取得 with result is new ContactInformation();
	 */
	@Test
	public void testGetMethod() {
		val res = UserInformationUseMethodService.get(require, "mock-companyId-empty", "mock-employeeId", "mock-personalId");
		assertThat(res).isEqualToComparingFieldByField(new ContactInformation());
	}

	/**
	 * Test 連絡先情報を取得 with null field of settingContactInfo;
	 */
	@Test
	public void testGetMethodNullFieldSettingContactInfo() {
		UserInformationUseMethod userInformationUseMethod = UserInfoUseMethodServiceTestHelper.initUserInformationUseMethod(2, true, true);
		PersonContactImport personContactImport = UserInfoUseMethodServiceTestHelper.initPersonContactImport(true);
		EmployeeInfoContactImport employeeInfoContactImport = UserInfoUseMethodServiceTestHelper.initEmployeeInfoContactImport();
		new Expectations() {
			{
				require.getUserInfoByCid("mock-companyId");
				result = Optional.of(userInformationUseMethod);
			}
			{
				require.getByPersonalId("mock-personalId");
				result = Optional.of(personContactImport);
			}
			{
				require.getByContactInformation("mock-employeeId");
				result = Optional.of(employeeInfoContactImport);
			}
		};
		val res = UserInformationUseMethodService.get(require, "mock-companyId", "mock-employeeId", "mock-personalId");
		assertThat(res.getCompanyMobilePhoneNumber()).isEqualTo("");
		assertThat(res.getSeatDialIn().v()).isEqualTo("");
		assertThat(res.getSeatExtensionNumber().v()).isEqualTo("");
		assertThat(res.getCompanyEmailAddress()).isPresent();
		assertThat(res.getCompanyEmailAddress().get()).isEqualTo("");
		assertThat(res.getCompanyMobileEmailAddress()).isPresent();
		assertThat(res.getCompanyMobileEmailAddress().get()).isEqualTo("");
		
		assertThat(res.getPersonalMobilePhoneNumber()).isEqualTo("");
		assertThat(res.getPersonalEmailAddress()).isPresent();
		assertThat(res.getPersonalEmailAddress().get()).isEqualTo("");
		assertThat(res.getPersonalMobileEmailAddress()).isPresent();
		assertThat(res.getPersonalMobileEmailAddress().get()).isEqualTo("");
		assertThat(res.getEmergencyNumber1()).isEqualTo("");
		assertThat(res.getEmergencyNumber2()).isEqualTo("");
	}
	
	/**
	 * Test 連絡先情報を取得 with use contact;
	 */
	@Test
	public void testGetMethodUseContact() {
		UserInformationUseMethod userInformationUseMethod = UserInfoUseMethodServiceTestHelper.initUserInformationUseMethod(1, false, false);
		PersonContactImport personContactImport = UserInfoUseMethodServiceTestHelper.initPersonContactImport(false);
		EmployeeInfoContactImport employeeInfoContactImport = UserInfoUseMethodServiceTestHelper.initEmployeeInfoContactImport();
		new Expectations() {
			{
				require.getUserInfoByCid("mock-companyId");
				result = Optional.of(userInformationUseMethod);
			}
			{
				require.getByPersonalId("mock-personalId");
				result = Optional.of(personContactImport);
			}
			{
				require.getByContactInformation("mock-employeeId");
				result = Optional.of(employeeInfoContactImport);
			}
		};
		val res = UserInformationUseMethodService.get(require, "mock-companyId", "mock-employeeId", "mock-personalId");
		
		assertThat(res.getCompanyMobilePhoneNumber()).isEqualTo(employeeInfoContactImport.getCellPhoneNo());
		assertThat(res.getSeatDialIn().v()).isEqualTo(employeeInfoContactImport.getSeatDialIn());
		assertThat(res.getSeatExtensionNumber().v()).isEqualTo(employeeInfoContactImport.getSeatExtensionNumber());
		assertThat(res.getCompanyEmailAddress()).isPresent();
		assertThat(res.getCompanyEmailAddress().get()).isEqualTo(employeeInfoContactImport.getMailAddress());
		assertThat(res.getCompanyMobileEmailAddress()).isPresent();
		assertThat(res.getCompanyMobileEmailAddress().get()).isEqualTo(employeeInfoContactImport.getMobileMailAddress());
		
		assertThat(res.getPersonalMobilePhoneNumber()).isEqualTo(personContactImport.getCellPhoneNo());
		assertThat(res.getPersonalEmailAddress()).isPresent();
		assertThat(res.getPersonalEmailAddress().get()).isEqualTo(personContactImport.getMailAddress());
		assertThat(res.getPersonalMobileEmailAddress()).isPresent();
		assertThat(res.getPersonalMobileEmailAddress().get()).isEqualTo(personContactImport.getMobileMailAddress());
		assertThat(res.getEmergencyNumber1()).isEqualTo(personContactImport.getEmergencyNumber1());
		assertThat(res.getEmergencyNumber2()).isEqualTo(personContactImport.getEmergencyNumber2());
		assertThat(res.getOtherContactsInfomation().size()).isEqualTo(5);
	}
	
	/**
	 * Test 連絡先情報を取得 with notuse contact;
	 */
	@Test
	public void testGetMethodNotUseContact() {
		UserInformationUseMethod userInformationUseMethod = UserInfoUseMethodServiceTestHelper.initUserInformationUseMethod(0, false, false);
		PersonContactImport personContactImport = UserInfoUseMethodServiceTestHelper.initPersonContactImport(false);
		EmployeeInfoContactImport employeeInfoContactImport = UserInfoUseMethodServiceTestHelper.initEmployeeInfoContactImport();
		new Expectations() {
			{
				require.getUserInfoByCid("mock-companyId");
				result = Optional.of(userInformationUseMethod);
			}
			{
				require.getByPersonalId("mock-personalId");
				result = Optional.of(personContactImport);
			}
			{
				require.getByContactInformation("mock-employeeId");
				result = Optional.of(employeeInfoContactImport);
			}
		};
		val res = UserInformationUseMethodService.get(require, "mock-companyId", "mock-employeeId", "mock-personalId");
		
		assertThat(res.getCompanyMobilePhoneNumber()).isEqualTo("");
		assertThat(res.getSeatDialIn().v()).isEqualTo("");
		assertThat(res.getSeatExtensionNumber().v()).isEqualTo("");
		assertThat(res.getCompanyEmailAddress()).isPresent();
		assertThat(res.getCompanyEmailAddress().get()).isEqualTo("");
		assertThat(res.getCompanyMobileEmailAddress()).isPresent();
		assertThat(res.getCompanyMobileEmailAddress().get()).isEqualTo("");
		
		assertThat(res.getPersonalMobilePhoneNumber()).isEqualTo("");
		assertThat(res.getPersonalEmailAddress()).isPresent();
		assertThat(res.getPersonalEmailAddress().get()).isEqualTo("");
		assertThat(res.getPersonalMobileEmailAddress()).isPresent();
		assertThat(res.getPersonalMobileEmailAddress().get()).isEqualTo("");
		assertThat(res.getEmergencyNumber1()).isEqualTo("");
		assertThat(res.getEmergencyNumber2()).isEqualTo("");
		assertThat(res.getOtherContactsInfomation().size()).isEqualTo(5);
	}
	
	/**
	 * Test 連絡先情報を取得 with null otherContact;
	 */
	@Test
	public void testGetMethodNullOtherContact() {
		UserInformationUseMethod userInformationUseMethod = UserInfoUseMethodServiceTestHelper.initUserInformationUseMethod(2, true, false);
		PersonContactImport personContactImport = UserInfoUseMethodServiceTestHelper.initPersonContactImport(true);
		EmployeeInfoContactImport employeeInfoContactImport = UserInfoUseMethodServiceTestHelper.initEmployeeInfoContactImport();
		new Expectations() {
			{
				require.getUserInfoByCid("mock-companyId");
				result = Optional.of(userInformationUseMethod);
			}
			{
				require.getByPersonalId("mock-personalId");
				result = Optional.of(personContactImport);
			}
			{
				require.getByContactInformation("mock-employeeId");
				result = Optional.of(employeeInfoContactImport);
			}
		};
		val res = UserInformationUseMethodService.get(require, "mock-companyId", "mock-employeeId", "mock-personalId");
		
		assertThat(res.getOtherContactsInfomation()).isEmpty();
	}
	
	/**
	 * Test 連絡先情報を取得 with employeeInfoContactImport is not present
	 */
	@Test
	public void testGetMethodNotPresentEmpInfoAndPersonContact() {
		UserInformationUseMethod userInformationUseMethod = UserInfoUseMethodServiceTestHelper.initUserInformationUseMethod(1, false, false);
		new Expectations() {
			{
				require.getUserInfoByCid("mock-companyId");
				result = Optional.of(userInformationUseMethod);
			}
		};
		val res = UserInformationUseMethodService.get(require, "mock-companyId", "mock-employeeId", "mock-personalId");
		
		assertThat(res.getCompanyMobilePhoneNumber()).isNull();
		assertThat(res.getSeatDialIn()).isNull();
		assertThat(res.getSeatExtensionNumber()).isNull();
		assertThat(res.getCompanyEmailAddress()).isNull();
		assertThat(res.getCompanyMobileEmailAddress()).isNull();
		
		assertThat(res.getPersonalMobilePhoneNumber()).isNull();
		assertThat(res.getPersonalEmailAddress()).isNull();
		assertThat(res.getPersonalMobileEmailAddress()).isNull();
		assertThat(res.getEmergencyNumber1()).isNull();
		assertThat(res.getEmergencyNumber2()).isNull();
	}

}
