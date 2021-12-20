package nts.uk.screen.com.app.cmf.cmf001.c.delete;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.app.input.setting.FromCsvBaseSettingToDomainRequireImpl;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.ImportSettingBaseType;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItemRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class Cmf001cDeleteCommandHandler extends CommandHandler<Cmf001cDeleteCommand> {

	@Inject
	private ExternalImportSettingRepository settingRepo;

	@Inject
	private ReviseItemRepository reviseItemRepo;

	@Inject
	private FileStorage fileStorage;
	
	@Override
	protected void handle(CommandHandlerContext<Cmf001cDeleteCommand> context) {
		
		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		ImportingDomainId domainId = ImportingDomainId.valueOf(command.getDomainId());

		val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
		val setting = settingRepo.get(Optional.of(require), companyId, command.getExternalImportCode()).get();
		val withReset = (setting.getBaseType() != ImportSettingBaseType.CSV_BASE);
		setting.getAssembly(domainId).getMapping().setNoSetting(command.getItemNo(), withReset);
		
		settingRepo.update(setting);
		reviseItemRepo.delete(companyId, command.getExternalImportCode(), domainId, command.getItemNo());
	}

}
