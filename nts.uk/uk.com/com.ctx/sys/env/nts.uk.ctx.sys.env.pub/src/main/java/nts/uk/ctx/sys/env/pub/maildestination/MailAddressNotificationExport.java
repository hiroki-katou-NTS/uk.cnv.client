package nts.uk.ctx.sys.env.pub.maildestination;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailAddressNotificationExport {

	/**
	 * メールアドレスList
	 */
	private List<AvailableMailAddressExport> mailAddresses;
	
	/**
	 * メール送信先機能管理
	 */
	private Optional<MailDestinationFunctionManageExport> mailDestinationFunctionManage;
}
