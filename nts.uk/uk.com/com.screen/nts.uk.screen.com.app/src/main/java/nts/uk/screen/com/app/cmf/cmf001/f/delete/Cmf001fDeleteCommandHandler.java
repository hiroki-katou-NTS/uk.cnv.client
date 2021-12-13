package nts.uk.screen.com.app.cmf.cmf001.f.delete;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItemRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class Cmf001fDeleteCommandHandler extends CommandHandler<Cmf001fDeleteCommand>{

	@Inject
	private ExternalImportSettingRepository repo;
	
	@Inject
	private ReviseItemRepository reviseRepo;

	@Override
	protected void handle(CommandHandlerContext<Cmf001fDeleteCommand> context) {
		String companyId = AppContexts.user().companyId();
		val command = context.getCommand();
		repo.deleteDomain(companyId, command.getCode(), command.getDomainId());
		reviseRepo.delete(companyId, new ExternalImportCode(command.getCode()));
	}
	
}
