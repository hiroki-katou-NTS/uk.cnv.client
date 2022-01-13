package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.adapter;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.Import.社員のメールアドレス
 */
@Data
@AllArgsConstructor
public class EmployeeMailAddressImport {

	/**
	 * 社員ID
	 */
	private String sid;
	
	/**
	 * 会社メールアドレス
	 */
	private Optional<MailAddress> companyMailAddress;
	
	/**
	 * 会社携帯メールアドレス
	 */
	private Optional<MailAddress> companyMobileMailAddress;
}
