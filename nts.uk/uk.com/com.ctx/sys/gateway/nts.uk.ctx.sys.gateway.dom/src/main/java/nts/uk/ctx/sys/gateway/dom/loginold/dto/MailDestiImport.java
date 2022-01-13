package nts.uk.ctx.sys.gateway.dom.loginold.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailDestiImport {

	/**
	 * メールアドレス詳細
	 */
	private MailAddressNotificationImport mailAddressNotification;
	
	/**
	 * 送信メール一覧
	 */
	private List<SentMailListImport> sentMailLists;
	
}
