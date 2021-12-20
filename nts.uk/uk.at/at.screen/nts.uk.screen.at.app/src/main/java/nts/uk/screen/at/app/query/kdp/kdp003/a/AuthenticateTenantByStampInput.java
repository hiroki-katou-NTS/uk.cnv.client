package nts.uk.screen.at.app.query.kdp.kdp003.a;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.gul.web.HttpClientIpAddress;
import nts.uk.ctx.sys.gateway.app.command.tenantlogin.ConnectDataSourceOfTenant;
import nts.uk.ctx.sys.gateway.dom.login.LoginClient;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.AuthenticateTenant;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthentication;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationFailureLog;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationFailureLogRepository;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationRepository;
import nts.uk.ctx.sys.gateway.dom.tenantlogin.TenantAuthenticationResult;
import nts.uk.shr.com.net.Ipv4Address;

/**
 * SQ: 打刻入力(共有)でテナントを認証する
 * UKDesign.UniversalK.就業.KDP_打刻.KDP003_打刻入力(氏名選択).A:打刻入力(氏名選択).メニュー別OCD.打刻入力(共有)でテナントを認証する
 * 
 * @author chungnt
 *
 */

@Stateless
public class AuthenticateTenantByStampInput {

	@Inject
	private TenantAuthenticationRepository tenantAuthenticationRepo;

	@Inject
	private TenantAuthenticationFailureLogRepository tenantAuthenticationFailureLogRepo;

	public boolean authenticateTenantByStampInput(AuthenticateTenantInput param) {

		// ログインクライアントの生成
		val loginClient = new LoginClient(Ipv4Address.parse(HttpClientIpAddress.get(param.reques)),
				param.reques.getHeader("user-agent"));

		// Step3: Call Command: テナント認証する

		RequireImpl require = new RequireImpl();

		TenantAuthenticationResult tenantAuthResult = ConnectDataSourceOfTenant.connect(require, loginClient,
				param.contactCode, param.password);

		return tenantAuthResult.isSuccess();
	}

	@RequiredArgsConstructor
	private class RequireImpl implements AuthenticateTenant.Require {
		@Override
		public Optional<TenantAuthentication> getTenantAuthentication(String tenantCode) {
			return tenantAuthenticationRepo.find(tenantCode);
		}

		@Override
		public void insert(TenantAuthenticationFailureLog failureLog) {
			tenantAuthenticationFailureLogRepo.insert(failureLog);
		}
	}
}
