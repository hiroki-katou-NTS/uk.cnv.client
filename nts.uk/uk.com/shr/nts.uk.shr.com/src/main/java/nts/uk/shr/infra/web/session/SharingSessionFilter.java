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

		SessionContextCookie.restoreSessionIfNeeded(isLoggedIn, httpRequest);
		
		chain.doFilter(request, response);
		
		// サーバ側の処理でセッション情報が変化しているかどうかに関わらず、Cookieの情報を最新化しておく。
		// この処理はxhtmlに対するリクエストのみ有効であり、WebAPI処理の場合には動作しない。
		// そちらはJaxRsResponseFilterに任せる。
		SessionContextCookie.updateCookie((HttpServletResponse) response);
	}

	@Override
	public void destroy() {
	}
}
