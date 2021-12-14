package nts.uk.shr.infra.web.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.val;
import nts.arc.bean.SingletonBeansSoftCache;
import nts.arc.security.csrf.CsrfToken;
import nts.uk.shr.com.context.loginuser.LoginUserContextManager;
import nts.uk.shr.com.context.loginuser.SessionLowLayer;

/**
 * warファイル間でセッションを擬似的に共有するための仕組み
 * CookieにSessionContextの情報を暗号化して持ち回る
 */
public class SharingSessionFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		val sessionLowLayer = SingletonBeansSoftCache.get(SessionLowLayer.class);
		boolean isLoggedIn = sessionLowLayer.isLoggedIn();
		val httpRequest = (HttpServletRequest) request;

		/*
		 * Cookieでセッション情報がやってきて、
		 * 1.かつ、ログインセッション情報が無い場合
		 *   -> 主にcom.webからat.webへの初回の移動とかでありえる
		 * 
		 * 2.かつ、ログインセッション情報があり、それと一致しない場合
		 *   -> 主にcom.webからat.webへの移動前に会社切り替えとかやった場合
		 * 
		 * 上記1,2いずれの場合も、Cookieの情報が正しいとみなして、サーバ側のセッションを書き換える。
		 * ログインしているのにCookieがやってこないケースは無いと思われるが、もしあったとしてもサーバ上のセッション情報でそのまま動作すれば良い
		 */
		SessionContextCookie.getSessionContextFrom(httpRequest)
				.ifPresent(sessionContext -> {
					if (!isLoggedIn || !sessionContext.equals(createStringSessionContext())) {
						restoreSessionContext(sessionContext);
					}
				});
		
		chain.doFilter(request, response);
		
		// サーバ側の処理でセッション情報が変化しているかどうかに関わらず、Cookieの情報を最新化しておく。
		// この処理はxhtmlに対するリクエストのみ有効であり、WebAPI処理の場合には動作しない。
		// そちらはJaxRsResponseFilterに任せる。
		SessionContextCookie.updateCookie((HttpServletResponse) response);
	}


	@Override
	public void destroy() {
	}
	
	private static final String DELIMITER = "@";

	private static String createStringSessionContext() {
		String userContext = SingletonBeansSoftCache.get(LoginUserContextManager.class).toBase64();
		String csrfToken = CsrfToken.getFromSession();
		
		// '='はCookieに含めると誤作動を起こすようなので、置換しておく
		return (userContext + DELIMITER + csrfToken).replace('=', '*');
	}
	
	private static void restoreSessionContext(String sessionContextInCookie) {
		val parts = sessionContextInCookie.replace('*', '=').split(DELIMITER);
		if (parts.length != 2) {
			return;
		}
		SingletonBeansSoftCache.get(LoginUserContextManager.class).restoreBase64(parts[0]);
		CsrfToken.setToSession(parts[1]);
	}
}
