package nts.uk.screen.com.app.find.cdl011;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManageRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class Cdl011ScreenQuery {

	@Inject
	private MailDestinationFunctionManageRepository mailDestinationFunctionManageRepository;
	
	/**
	 * UKDesign.UniversalK.共通.CDL_共通ダイアログ.CDL011_メール送信先アドレスの設定ダイアログ.メニュー別OCD.メール送信先アドレス設定を取得する
	 */
	public MailDestinationFunctionManageDto findData(int functionId) {
		String cid = AppContexts.user().companyId();
		// 1. get(機能ID)
		return this.mailDestinationFunctionManageRepository.findByFunctionId(cid, functionId)
				.map(MailDestinationFunctionManageDto::fromDomain).orElse(null);
	}
}
