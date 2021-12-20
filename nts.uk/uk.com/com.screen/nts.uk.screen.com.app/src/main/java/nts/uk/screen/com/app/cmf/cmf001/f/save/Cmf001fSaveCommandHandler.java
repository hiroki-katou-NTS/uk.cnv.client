package nts.uk.screen.com.app.cmf.cmf001.f.save;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.screen.com.app.cmf.cmf001.f.save.Cmf001fSaveCommand;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class Cmf001fSaveCommandHandler extends CommandHandler<Cmf001fSaveCommand>{

	@Inject
	private ExternalImportSettingRepository externalImportSettingRepo;
	
	@Override
	protected void handle(CommandHandlerContext<Cmf001fSaveCommand> context) {

		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		
		val setting = externalImportSettingRepo.get(Optional.empty(), companyId,  new ExternalImportCode(command.getCode()))
				.orElseThrow(() -> new BusinessException(new RawErrorMessage("")));

		val domain = command.toDomain();
		externalImportSettingRepo.registDomain(setting, domain);
	}

}
