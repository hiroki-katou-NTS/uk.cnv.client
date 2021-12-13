package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.AvailableMailAddress;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.FunctionType;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManageTestHelper;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice.GetEmailNotificationDomainService.Require;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class GetEmailNotificationDomainServiceTest {

	@Injectable
	private Require require;

	/**
	 * [1] 取得する メール
	 * 送信先機能管理 == empty
	 */
	@Test
	public void testGetEmpty() {
		// when
		MailAddressNotification data = GetEmailNotificationDomainService.get(require,
				MailDestinationFunctionManageTestHelper.CID, FunctionType.ALARM_LIST,
				Arrays.asList(MailDestinationFunctionManageTestHelper.SID));
		
		// then
		assertThat(data.getMailAddresses()).isEmpty();
		assertThat(data.getMailDestinationFunctionManage()).isNotPresent();
	}
	
	/**
	 * [1] 取得する メール
	 * 送信先機能管理 != empty
	 * メールアドレスList != empty
	 */
	@Test
	public void testGetMailNotEmpty() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper.mockDomain(FunctionType.ALARM_LIST);
		domain.setUseCompanyMailAddress(NotUseAtr.USE);
		domain.setUsePersonalMailAddress(NotUseAtr.USE);
		
		new Expectations() {
			{
				require.findByFunctionId(MailDestinationFunctionManageTestHelper.CID, FunctionType.ALARM_LIST.value);
				result = Optional.of(domain);
			};
			{
				require.getEmployeeContactInfo(Arrays.asList(MailDestinationFunctionManageTestHelper.SID));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true));
			};
			{
				require.getPersonalContactInfo(Arrays.asList(MailDestinationFunctionManageTestHelper.SID));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
			};
		};
		
		// when
		MailAddressNotification results = GetEmailNotificationDomainService.get(require,
				MailDestinationFunctionManageTestHelper.CID, FunctionType.ALARM_LIST,
				Arrays.asList(sid));
		Optional<AvailableMailAddress> data = results.getMailAddresses().stream()
				.filter(result -> result.getSid().equals(sid)).findFirst();
		
		// then
		assertThat(results.getMailDestinationFunctionManage()).isPresent();
		assertThat(data).isPresent();
		assertThat(data.get().getOptCompanyMailAddress()).isPresent();
		assertThat(data.get().getOptPersonalMailAddress()).isPresent();
	}
	
	/**
	 * [1] 取得する メール
	 * 送信先機能管理 != empty
	 * メールアドレスList == empty
	 */
	@Test
	public void testGetMailEmpty() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper.mockDomain(FunctionType.ALARM_LIST);
		domain.setUseCompanyMailAddress(NotUseAtr.USE);
		domain.setUsePersonalMailAddress(NotUseAtr.USE);
		
		new Expectations() {
			{
				require.findByFunctionId(MailDestinationFunctionManageTestHelper.CID, FunctionType.ALARM_LIST.value);
				result = Optional.of(domain);
			};
			{
				require.getEmployeeContactInfo(Arrays.asList(MailDestinationFunctionManageTestHelper.SID));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(false));
			};
			{
				require.getPersonalContactInfo(Arrays.asList(MailDestinationFunctionManageTestHelper.SID));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(false));
			};
		};
		
		// when
		MailAddressNotification results = GetEmailNotificationDomainService.get(require,
				MailDestinationFunctionManageTestHelper.CID, FunctionType.ALARM_LIST,
				Arrays.asList(sid));
		Optional<AvailableMailAddress> data = results.getMailAddresses().stream()
				.filter(result -> result.getSid().equals(sid)).findFirst();
		
		// then
		assertThat(results.getMailDestinationFunctionManage()).isPresent();
		assertThat(data).isPresent();
		assertThat(data.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
}
