package nts.uk.screen.com.app.cmf.cmf001.b.save;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class Cmf001bSaveCommandHandler extends CommandHandler<Cmf001bSaveCommand>{

	@Inject
	private ExternalImportSettingRepository externalImportSettingRepo;
	
	@Override
	protected void handle(CommandHandlerContext<Cmf001bSaveCommand> context) {

		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();

		//新規モード
		if(command.isNew()) {
			//コードの重複チェック
			if(externalImportSettingRepo.exist(companyId, command.getCode())) {
				throw new BusinessException("Msg_3");
			}
			val domain = command.toDomain();
			externalImportSettingRepo.insert(domain);
		} else {
			val domain = command.toDomain();
			externalImportSettingRepo.update(domain);
		}
	}

}
