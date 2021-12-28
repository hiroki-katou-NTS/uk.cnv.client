package nts.uk.ctx.at.auth.app.command.initswitchsetting;


import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSet;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSetRepo;

@Stateless
public class SaveInitDisplayPeriodSwitchSetCmdHandler extends CommandHandler<SaveInitDisplayPeriodSwitchSetCmd> {

	@Inject
	private InitDisplayPeriodSwitchSetRepo repo;

	@Override
	protected void handle(CommandHandlerContext<SaveInitDisplayPeriodSwitchSetCmd> context) {
		SaveInitDisplayPeriodSwitchSetCmd appCommand = context.getCommand();
		InitDisplayPeriodSwitchSet domain = appCommand.toDomain();
		repo.save(domain);
	}

}
