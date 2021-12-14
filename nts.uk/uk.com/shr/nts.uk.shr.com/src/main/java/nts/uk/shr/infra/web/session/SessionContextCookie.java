package nts.uk.shr.infra.web.session;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerResponseContext;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import nts.arc.bean.SingletonBeansSoftCache;
import nts.arc.security.csrf.CsrfToken;
import nts.uk.shr.com.context.loginuser.LoginUserContextManager;
import nts.uk.shr.com.context.loginuser.SessionLowLayer;

/**
 * warファイル間でセッションを擬似的に共有するための仕組み
 * CookieにSessionContextの情報を暗号化して持ち回る
 */
@Slf4j
public class SessionContextCookie {

	private static final String COOKIE_SESSION_CONTEXT = "nts.uk.sescon";
	
	public static Optional<String> getSessionContextFrom(HttpServletRequest httpRequest) {
		
		if (httpRequest.getCookies() == null) {
			return Optional.empty();
		}
		
		return Arrays.asList(httpRequest.getCookies()).stream()
				.filter(c -> c.getName().equals(COOKIE_SESSION_CONTEXT))
				.map(c -> c.getValue())
				.findFirst();
	}

	/**
	 * CookieのSessionContext情報を更新する
	 * WebAPI(JAX-RS)のレスポンスではヘッダの追記ができない
	 * xhtmlへのリクエスト専用
	 * @param response
	 */
	public static void updateCookie(HttpServletResponse response) {
		response.addHeader("Set-Cookie", createNewCookieFromSession());
	}
	
	/**
	 * CookieのSessionContext情報を更新する
	 * JAX-RS用
	 * @param responseContext
	 */
	public static void updateCookie(ContainerResponseContext responseContext) {
		
		// responseContext.getCookies().put()は使えない模様。
		// getCookiesの内容は既に確定済みであり、書き換えても反映されないように見える。
		// その代わり、以下のように直接ヘッダを追加することは有効だった。
		responseContext.getHeaders().add("Set-Cookie", createNewCookieFromSession());
	}

	private static String createNewCookieFromSession() {
		val session = SingletonBeansSoftCache.get(SessionLowLayer.class);
		
		if (!session.isLoggedIn()) {
			// 「ログインしていない」と「ログアウトした」の区別は、ここではできない
			// いずれにせよCookieを無効化する
			return buildDeleteCookieHeaderValue();
		}
		
		// WildflyのHTTPセッションと同じ期限
		return buildSetCookieHeaderValue(session.secondsSessionTimeout());
	}
	
	public static void restoreSessionFromCookie(HttpServletRequest httpRequest, boolean isLoggedIn) {

		Arrays.asList(httpRequest.getCookies()).stream()
				.filter(c -> c.getName().equals(COOKIE_SESSION_CONTEXT))
				.map(c -> c.getValue())
				.findFirst()
				.ifPresent(sessionContext -> {
					if (!isLoggedIn || !sessionContext.equals(createStringSessionContext())) {
						restoreSessionContext(sessionContext);
					}
				});
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
			log.error("Invalid session context cookie: " + sessionContextInCookie);
			return;
		}
		
		try {
			SingletonBeansSoftCache.get(LoginUserContextManager.class).restoreBase64(parts[0]);
			CsrfToken.setToSession(parts[1]);
		} catch (Exception ex) {
			// 何らかの理由で破損したCookieから復元しようとした場合は、復元せずにスルー（エラーは起こさないようにしておく）
			// 一応、ログには出力しておく
			log.error("SessionContextCookieの復元に失敗", ex);
			return;
		}
	}
	
	private static String buildSetCookieHeaderValue(int secondsSessionTimeout) {

		return buildSetCookieHeaderValue(
				t -> t.plusSeconds(secondsSessionTimeout),
				createStringSessionContext());
	}
	
	private static String buildDeleteCookieHeaderValue() {
		
		// Expires属性でCookieを削除するには、過去の日付を指定する
		return buildSetCookieHeaderValue(
				t -> t.minusYears(1), // いつでもいいが、とりあえず１年前
				"none"); // ダミーの値
	}
	
	private static String buildSetCookieHeaderValue(UnaryOperator<OffsetDateTime> shiftFromNow, String value) {
		
		// IE11のバージョンによって、Max-Age属性が機能しないため、Expires属性を使う必要がある。
		// ただ、標準のCookieクラスはsetMaxAgeしかないので、やむを得ずSet-Cookieヘッダを直接書き出している
		
		String expiresFormat = shiftFromNow.apply(OffsetDateTime.now(ZoneOffset.UTC))
				.format(DateTimeFormatter.ofPattern("EEE, d MMM uuuu kk:mm:ss", Locale.ENGLISH))
				+ " GMT";
		
		return new StringBuilder()
				.append(COOKIE_SESSION_CONTEXT)
				.append("=")
				.append(value)
				.append("; Path=/; HttpOnly; Expires=")
				.append(expiresFormat)
				.toString();
	}
}
