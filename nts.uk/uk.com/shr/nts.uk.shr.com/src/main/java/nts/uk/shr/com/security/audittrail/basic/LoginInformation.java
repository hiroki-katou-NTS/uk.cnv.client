package nts.uk.shr.com.security.audittrail.basic;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.uk.shr.com.context.AppContexts;

/**
 * ログイン情報
 */
@RequiredArgsConstructor
@Getter
public class LoginInformation {

	/** IPアドレス */
	private final Optional<String> ipAddress;
	
	/** Windowsホスト名 */
	private final Optional<String> pcName;
	
	/** Windowsユーザ名 */
	private final Optional<String> account;
	
	/**
	 * Constructs.
	 * @param ipAddress nullable
	 * @param pcName nullable
	 * @param account nullable
	 */
	public LoginInformation(String ipAddress, String pcName, String account) {
		this(Optional.ofNullable(ipAddress), Optional.ofNullable(pcName), Optional.ofNullable(account));
	}
	
	public static LoginInformation byAppContexts() {
		val request = AppContexts.requestedWebApi();
		return new LoginInformation(
				request.getRequestIpAddress(),
				request.getRequestPcName(),
				AppContexts.windowsAccount().getUserName());
	}
}
