package nts.uk.ctx.sys.env.pub.maildestination;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.Export.RQ397：社員のメールアドレスを取得する.アルゴリズム.メール送信先
 */
@Data
@AllArgsConstructor
public class MailDestinationExport {

	/**
	 * メールアドレス詳細
	 */
	private MailAddressNotificationExport mailAddressNotification;
	
	/**
	 * 送信メール一覧
	 */
	private List<SentMailListExport> sentMailLists;
}
