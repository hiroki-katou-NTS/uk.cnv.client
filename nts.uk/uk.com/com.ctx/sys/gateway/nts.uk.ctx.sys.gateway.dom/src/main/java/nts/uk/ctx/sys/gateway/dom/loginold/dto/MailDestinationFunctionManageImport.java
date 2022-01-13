package nts.uk.ctx.sys.gateway.dom.loginold.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailDestinationFunctionManageImport {

	/**
	 * 機能ID
	 */
	private int functionId;

	/**
	 * 会社メールアドレスを利用する
	 */
	private int useCompanyMailAddress;

	/**
	 * 会社携帯メールアドレスを利用する
	 */
	private int useCompanyMobileMailAddress;

	/**
	 * 個人メールアドレスを利用する
	 */
	private int usePersonalMailAddress;

	/**
	 * 個人携帯メールアドレスを利用する
	 */
	private int usePersonalMobileMailAddress;
}
