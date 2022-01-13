package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.Import.個人のメールアドレス
 */
@Data
@AllArgsConstructor
public class PersonalMailAddressImport {

	/**
	 * 社員ID
	 */
	private String sid;
	
	/**
	 * 個人メールアドレス
	 */
	private Optional<MailAddress> personalMailAddress;
	
	/**
	 * 個人携帯メールアドレス
	 */
	private Optional<MailAddress> personalMobileMailAddress;
}
