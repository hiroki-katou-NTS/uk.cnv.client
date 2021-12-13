package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.AvailableMailAddress;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.メール送信先機能.メールアドレス通知
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MailAddressNotification {

	/**
	 * メールアドレスList
	 */
	private List<AvailableMailAddress> mailAddresses = Collections.emptyList();
	
	/**
	 * メール送信先機能管理
	 */
	private Optional<MailDestinationFunctionManage> mailDestinationFunctionManage = Optional.empty();
}
