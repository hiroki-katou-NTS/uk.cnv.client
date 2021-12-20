package nts.uk.ctx.sys.env.pub.maildestination;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailDestinationFunctionManageExport {

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
