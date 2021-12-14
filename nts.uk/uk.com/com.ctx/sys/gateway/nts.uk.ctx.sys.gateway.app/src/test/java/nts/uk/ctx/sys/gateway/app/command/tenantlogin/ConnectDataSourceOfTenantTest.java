package nts.uk.ctx.sys.gateway.app.command.tenantlogin;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import lombok.val;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Verifications;
import nts.arc.task.tran.AtomTask;
import nts.gul.util.value.MutableValue;
import nts.uk.ctx.sys.gateway.dom.login.LoginClient;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.AuthenticateTenant;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationFailureLog;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationResult;
import nts.uk.shr.com.net.Ipv4Address;
import nts.uk.shr.com.system.property.UKServerSystemProperties;
import nts.uk.shr.infra.data.TenantLocatorService;

public class ConnectDataSourceOfTenantTest {
	@Injectable
	private TenantAuthenticationResult tenantAuthenticateResult;
	
	@Injectable
	private AuthenticateTenant.Require require;
	
	private static class Dummy{
		static final LoginClient LOGIN_CLIENT = new LoginClient(Ipv4Address.parse("255.255.255.255"), "");
		static final String TENANT_CODE = "000000000000";
		static final String PASSWORD = "0";
		static final AtomTask ATOM_TASK= AtomTask.of(AtomTask.none());
	}
	
	@Test
	public void notUseTL() {
		
		MutableValue<Boolean> isCalledConnect = new MutableValue<>(false);
		MutableValue<Boolean> isCalledDisconnect = new MutableValue<>(false);
		
		new MockUp<UKServerSystemProperties>() {
			@Mock
			public boolean usesTenantLocator() {
				return false;
			}
		};
		assertThat(isCalledConnect.get()).isFalse();
		new MockUp<TenantLocatorService>() {
			@Mock
			public void connect(String tenantCode) {
				isCalledConnect.set(true);
			}
			@Mock
			public void disconnect() {
				isCalledDisconnect.set(true);
			}
		};
		
		new MockUp<AuthenticateTenant>() {
			@Mock
			public TenantAuthenticationResult authenticate(AuthenticateTenant.Require require, String tenantCode, String password, LoginClient loginClient) {
				return TenantAuthenticationResult.success();
			}
		};
		
		val result = ConnectDataSourceOfTenant.connect(require, Dummy.LOGIN_CLIENT, Dummy.TENANT_CODE, Dummy.PASSWORD);
		
		assertThat(result.isSuccess()).isTrue();
		assertThat(isCalledConnect.get()).isFalse();
		assertThat(isCalledDisconnect.get()).isFalse();
	}
	

	@Test
	public void useTL_fail() {
		
		MutableValue<Boolean> isCalledConnect = new MutableValue<>(false);
		MutableValue<Boolean> isCalledDisconnect = new MutableValue<>(false);
		
		new MockUp<UKServerSystemProperties>() {
			@Mock
			public boolean usesTenantLocator() {
				return true;
			}
		};
		assertThat(isCalledConnect.get()).isFalse();
		new MockUp<TenantLocatorService>() {
			@Mock
			public void connect(String tenantCode) {
				isCalledConnect.set(true);
			}
			@Mock
			public boolean isConnected() {
				return true;
			}
			@Mock
			public void disconnect() {
				isCalledDisconnect.set(true);
			}
		};
		
		new MockUp<AuthenticateTenant>() {
			@Mock
			public TenantAuthenticationResult authenticate(AuthenticateTenant.Require require, String tenantCode, String password, LoginClient loginClient) {
				return TenantAuthenticationResult.failedDueToIncorrectPassword(Dummy.ATOM_TASK);
			}
		};
		
		val result = ConnectDataSourceOfTenant.connect(require, Dummy.LOGIN_CLIENT, Dummy.TENANT_CODE, Dummy.PASSWORD);
		
		assertThat(result.isSuccess()).isFalse();
		assertThat(isCalledConnect.get()).isTrue();
		assertThat(isCalledDisconnect.get()).isTrue();
	}
	
	@Test
	public void useTL_success() {
		
		MutableValue<Boolean> isCalledConnect = new MutableValue<>(false);
		MutableValue<Boolean> isCalledDisconnect = new MutableValue<>(false);
		
		new MockUp<UKServerSystemProperties>() {
			@Mock
			public boolean usesTenantLocator() {
				return true;
			}
		};
		assertThat(isCalledConnect.get()).isFalse();
		new MockUp<TenantLocatorService>() {
			@Mock
			public void connect(String tenantCode) {
				isCalledConnect.set(true);
			}
			@Mock
			public boolean isConnected() {
				return true;
			}
			@Mock
			public void disconnect() {
				isCalledDisconnect.set(true);
			}
		};
		
		new MockUp<AuthenticateTenant>() {
			@Mock
			public TenantAuthenticationResult authenticate(AuthenticateTenant.Require require, String tenantCode, String password, LoginClient loginClient) {
				return TenantAuthenticationResult.success();
			}
		};
		
		val result = ConnectDataSourceOfTenant.connect(require, Dummy.LOGIN_CLIENT, Dummy.TENANT_CODE, Dummy.PASSWORD);
		
		assertThat(result.isSuccess()).isTrue();
		assertThat(isCalledConnect.get()).isTrue();
		assertThat(isCalledDisconnect.get()).isFalse();
	}
}
