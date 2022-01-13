package nts.uk.ctx.sys.env.app.command.mailnoticeset.maildestination;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManage;
import nts.uk.ctx.sys.env.dom.mailnoticeset.maildestination.MailDestinationFunctionManageRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.システム.環境.メール通知設定.メール送信先機能.APP.メール送信先アドレス設定を登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterMailDestinationFunctionManageCommandHandler
		extends CommandHandler<MailDestinationFunctionManageCommand> {

	@Inject
	private MailDestinationFunctionManageRepository repository;

	@Override
	protected void handle(CommandHandlerContext<MailDestinationFunctionManageCommand> context) {
		String cid = AppContexts.user().companyId();
		MailDestinationFunctionManage domain = context.getCommand().toDomain();
		// 1. get(会社ID、機能ID)
		Optional<MailDestinationFunctionManage> optDomain = this.repository.findByFunctionId(cid,
				domain.getFunctionId().value);
		// 2. [メール送信利用管理　is　empty]: create()
		if (!optDomain.isPresent()) {
			this.repository.insert(domain);
		} else {
			// 3. [メール送信利用管理　is　not　empty]: set()
			this.repository.update(domain);
		}
	}
}
