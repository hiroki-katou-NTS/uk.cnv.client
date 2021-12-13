package nts.uk.screen.com.app.find.cdl011;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;

@Data
@AllArgsConstructor
public class MailDestinationFunctionManageDto {
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

	public static MailDestinationFunctionManageDto fromDomain(MailDestinationFunctionManage domain) {
		return new MailDestinationFunctionManageDto(domain.getFunctionId().value,
				domain.getUseCompanyMailAddress().value, domain.getUseCompanyMobileMailAddress().value,
				domain.getUsePersonalMailAddress().value, domain.getUsePersonalMobileMailAddress().value);
	}
}
