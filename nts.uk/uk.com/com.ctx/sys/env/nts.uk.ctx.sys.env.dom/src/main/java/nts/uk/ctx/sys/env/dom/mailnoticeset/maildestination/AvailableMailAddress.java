package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Value;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.メール送信先機能.利用できるメールアドレス
 */
@Value
@AllArgsConstructor
public class AvailableMailAddress {

	/**
	 * 社員ID
	 */
	private String sid;
	
	/**
	 * 会社メールアドレス
	 */
	private Optional<MailAddress> optCompanyMailAddress;
	
	/**
	 * 会社携帯メールアドレス
	 */
	private Optional<MailAddress> optCompanyMobileMailAddress;
	
	/**
	 * 個人メールアドレス
	 */
	private Optional<MailAddress> optPersonalMailAddress;
	
	/**
	 * 個人携帯メールアドレス
	 */
	private Optional<MailAddress> optPersonalMobileMailAddress;
}
