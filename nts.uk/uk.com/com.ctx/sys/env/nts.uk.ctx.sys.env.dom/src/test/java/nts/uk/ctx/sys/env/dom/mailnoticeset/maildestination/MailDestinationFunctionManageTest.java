package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage.Require;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@RunWith(JMockit.class)
public class MailDestinationFunctionManageTest {

	@Injectable
	private Require require;

	@Test
	public void testGetters() {
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper.mockDomain(FunctionType.LOGIN);
		NtsAssert.invokeGetters(domain);
	}

	/**
	 * [1] 利用できるメールアドレスを求める 
	 * 全てのチェックボックスがfalseになる。
	 */
	@Test
	public void testGetAvalableMailAddressesNotUse() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.ALARM_LIST);
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
		
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠会社メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testGetAvalableMailAddressesCompanyHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.ALARM_LIST);
		domain.setUseCompanyMailAddress(NotUseAtr.USE);

		new Expectations() {
			{
				require.getEmployeeContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠会社メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testGetAvalableMailAddressesCompanyNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.ALARM_LIST);
		domain.setUseCompanyMailAddress(NotUseAtr.USE);
		
		new Expectations() {
			{
				require.getEmployeeContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(false));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠会社携帯メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testGetAvalableMailAddressesCompanyMobileHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.LOGIN);
		domain.setUseCompanyMobileMailAddress(NotUseAtr.USE);

		new Expectations() {
			{
				require.getEmployeeContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠会社携帯メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testGetAvalableMailAddressesCompanyMobileNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.LOGIN);
		domain.setUseCompanyMobileMailAddress(NotUseAtr.USE);
		
		new Expectations() {
			{
				require.getEmployeeContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(false));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠個人メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testGetAvalableMailAddressesPersonalHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.LOGIN);
		domain.setUsePersonalMailAddress(NotUseAtr.USE);

		new Expectations() {
			{
				require.getPersonalContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠個人メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testGetAvalableMailAddressesPersonalNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.LOGIN);
		domain.setUsePersonalMailAddress(NotUseAtr.USE);
		
		new Expectations() {
			{
				require.getPersonalContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(false));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠個人携帯メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testGetAvalableMailAddressesPersonalMobileHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.LOGIN);
		domain.setUsePersonalMobileMailAddress(NotUseAtr.USE);

		new Expectations() {
			{
				require.getPersonalContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isPresent();
	}
	
	/**
	 * [1] 利用できるメールアドレスを求める 
	 * if ＠個人携帯メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testGetAvalableMailAddressesPersonalMobileNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.LOGIN);
		domain.setUsePersonalMobileMailAddress(NotUseAtr.USE);
		
		new Expectations() {
			{
				require.getPersonalContactInfo(Arrays.asList(sid));
				result = Arrays.asList(MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(false));
			};
		};
		
		// when
		List<AvailableMailAddress> datas = domain.getAvailableMailAddresses(require, Arrays.asList(sid));
		Optional<AvailableMailAddress> result = datas.stream().filter(data -> data.getSid().equals(sid)).findFirst();
				
		// then
		assertThat(result).isPresent();
		assertThat(result.get().getOptCompanyMailAddress()).isNotPresent();
		assertThat(result.get().getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMailAddress()).isNotPresent();
		assertThat(result.get().getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * 全てのチェックボックスがfalseになる。
	 */
	@Test
	public void testSummarizeAvailableMailsNotUsed() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * 社員のメールアドレス == null
	 */
	@Test
	public void testSummarizeAvaliableMailsEmployeeContactNull() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				null, MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * 個人のメールアドレス == null
	 */
	@Test
	public void testSummarizeAvaliableMailsPersonalContactNull() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true), null);
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠会社メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testSummarizeAvailableMailsCompanyHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUseCompanyMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠会社メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testSummarizeAvailableMailsCompanyNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUseCompanyMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(false),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠会社携帯メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testSummarizeAvailableMailsCompanyMobileHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUseCompanyMobileMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠会社携帯メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testSummarizeAvailableMailsCompanyMobileNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUseCompanyMobileMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(false),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠個人メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testSummarizeAvailableMailsPersonalHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUsePersonalMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠個人メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testSummarizeAvailableMailsPersonalNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUsePersonalMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(false));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠個人携帯メールアドレスを利用する == する
	 * メールアドレスのデータがある。
	 */
	@Test
	public void testSummarizeAvailableMailsPersonalMobileHasMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUsePersonalMobileMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(true));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isPresent();
	}
	
	/**
	 * [prv-1] 利用できるメールアをまとめる
	 * if ＠個人携帯メールアドレスを利用する == する
	 * メールアドレスのデータがない。
	 */
	@Test
	public void testSummarizeAvailableMailsPersonalMobileNoMail() {
		// given
		String sid = MailDestinationFunctionManageTestHelper.SID;
		MailDestinationFunctionManage domain = MailDestinationFunctionManageTestHelper
				.mockDomain(FunctionType.REQUEST);
		domain.setUsePersonalMobileMailAddress(NotUseAtr.USE);
		
		// when
		AvailableMailAddress data = NtsAssert.Invoke.privateMethod(domain, "summarizeAvailableMails", sid, 
				MailDestinationFunctionManageTestHelper.mockEmployeeMailAddress(true),
				MailDestinationFunctionManageTestHelper.mockPersonalMailAddress(false));
		
		// then
		assertThat(data.getSid()).isEqualTo(sid);
		assertThat(data.getOptCompanyMailAddress()).isNotPresent();
		assertThat(data.getOptCompanyMobileMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMailAddress()).isNotPresent();
		assertThat(data.getOptPersonalMobileMailAddress()).isNotPresent();
	}
}
