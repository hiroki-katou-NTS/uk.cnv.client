package nts.uk.ctx.sys.env.app.command.testsendmail;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.sys.env.app.command.mailserver.MailServerSaveCommand;
import nts.uk.ctx.sys.env.dom.mailserver.MailServer;
import nts.uk.ctx.sys.env.dom.mailserver.MailServerRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM050_メールサーバーの設定.A：メールサーバの設定.アルゴリズム.A：テスト送信前チェック.A：テスト送信前チェック
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CheckDataChangedCommandHandler extends CommandHandlerWithResult<MailServerSaveCommand, Boolean> {

	@Inject
	private MailServerRepository mailServerRepository;

	@Override
	protected Boolean handle(CommandHandlerContext<MailServerSaveCommand> context) {
		MailServerSaveCommand command = context.getCommand();
		String cid = AppContexts.user().companyId();
		MailServer domain = new MailServer(command);
		// 画面の入力項目が編集されているかのチェック
		Optional<MailServer> optDomain = this.mailServerRepository.findBy(cid);
		return optDomain.map(data -> data.compareTo(domain) != 0).orElse(true);
	}
}
