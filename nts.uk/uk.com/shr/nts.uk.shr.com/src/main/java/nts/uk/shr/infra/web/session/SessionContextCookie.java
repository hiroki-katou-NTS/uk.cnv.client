package nts.uk.shr.infra.web.session;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerResponseContext;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import nts.arc.bean.SingletonBeansSoftCache;
import nts.arc.security.csrf.CsrfToken;
import nts.gul.util.Either;
import nts.uk.shr.com.context.loginuser.LoginUserContextManager;
import nts.uk.shr.com.context.loginuser.SessionLowLayer;
import nts.uk.shr.infra.data.TenantLocatorService;

/**
 * warファイル間でセッションを擬似的に共有するための仕組み
 * CookieにSessionContextの情報を暗号化して持ち回る
 */
@Slf4j
public class SessionContextCookie {

	private static final String COOKIE_SESSION_CONTEXT = "nts.uk.sescon";

	public static void restoreSessionIfNeeded(boolean isLoggedIn, HttpServletRequest httpRequest) {

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
		getSessionContextFrom(httpRequest)
				.ifPresent(sessionContext -> {
					if (!isLoggedIn || !sessionContext.equals(Values.fromSession().toCookieString())) {
						restoreSessionContext(sessionContext);
					}
				});
	}

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

	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Values {

		private static final String DELIMITER = "@";
		private static final char BASE64EQUAL_ALTER = '*';

		private final String userContext;
		private final String csrfToken;
		private final String dataSource;

		public static Values fromSession() {

			String userContext = SingletonBeansSoftCache.get(LoginUserContextManager.class).toBase64()
					.orElse(null);

			String csrfToken = Optional.ofNullable(CsrfToken.getFromSession())
					.orElse(null);

			String dataSource = TenantLocatorService.getConnectedDataSource();
			if ("".equals(dataSource)) {
				dataSource = null;
			}

			return new Values(userContext, csrfToken, dataSource);
		}

		public static Either<Void, Values> fromCookie(String cookie) {

			val parts = cookie.replace(BASE64EQUAL_ALTER, '=').split(DELIMITER);
			if (parts.length != 3) {
				log.error("Invalid session context cookie: " + cookie);
				return Either.left(null);
			}

			return Either.right(new Values(parts[0], parts[1], parts[2]));
		}

		void restoreSession() {

			try {

				SingletonBeansSoftCache.get(LoginUserContextManager.class).restoreBase64(userContext);
				CsrfToken.setToSession(csrfToken);
				TenantLocatorService.connectDataSource(dataSource);

			} catch (Exception ex) {
				// 何らかの理由で破損したCookieから復元しようとした場合は、復元せずにスルー（エラーは起こさないようにしておく）
				// 一応、ログには出力しておく
				log.error("SessionContextCookieの復元に失敗", ex);
				return;
			}
		}

		String toCookieString() {
			val values = Arrays.asList(userContext, csrfToken, dataSource);
			return values.stream()
					.collect(Collectors.joining(DELIMITER))
					.replace('=', BASE64EQUAL_ALTER);
		}
	}

	public static void restoreSessionContext(String sessionContextInCookie) {

		Values.fromCookie(sessionContextInCookie).ifRight(v -> {
			v.restoreSession();
		});
	}
	
	private static String buildSetCookieHeaderValue(int secondsSessionTimeout) {

		return buildSetCookieHeaderValue(
				t -> t.plusSeconds(secondsSessionTimeout),
				Values.fromSession().toCookieString());
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
