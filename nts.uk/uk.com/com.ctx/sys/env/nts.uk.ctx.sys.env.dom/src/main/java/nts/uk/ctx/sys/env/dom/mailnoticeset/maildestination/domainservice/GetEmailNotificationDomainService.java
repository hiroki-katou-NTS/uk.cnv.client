package nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.domainservice;

import java.util.List;
import java.util.Optional;

import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.AvailableMailAddress;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.FunctionType;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.メール送信先機能.社員のメールアドレス通知を取得する
 */
public class GetEmailNotificationDomainService {

	/**
	 * [1] 取得する
	 * 
	 * @param require
	 * @param functionId 機能ID
	 * @param sids       List<社員ID>
	 * @return メールアドレス通知
	 */
	public static MailAddressNotification get(Require require, String cid, FunctionType functionId, List<String> sids) {
		Optional<MailDestinationFunctionManage> optManage = require.findByFunctionId(cid, functionId.value);
		if (!optManage.isPresent()) {
			return new MailAddressNotification();
		}
		List<AvailableMailAddress> mailAddresses = optManage.get().getAvailableMailAddresses(require, sids);
		return new MailAddressNotification(mailAddresses, optManage);
	}

	public interface Require extends MailDestinationFunctionManage.Require {

		// メール送信先機能管理Repository.get(会社ID, 機能ID)
		Optional<MailDestinationFunctionManage> findByFunctionId(String cid, int functionId);
	}
}
