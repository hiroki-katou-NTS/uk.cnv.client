package nts.uk.ctx.at.auth.app.command.initswitchsetting;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSet;
import nts.uk.ctx.at.auth.dom.initswitchsetting.InitDisplayPeriodSwitchSetRepo;

@Stateless
public class DeleteInitDisplayPeriodSwitchSetHandler extends CommandHandler<DeleteInitDisplayPeriodSwitchSetCmd> {
	@Inject
	private InitDisplayPeriodSwitchSetRepo repo;

	@Override
	protected void handle(CommandHandlerContext<DeleteInitDisplayPeriodSwitchSetCmd> context) {
		DeleteInitDisplayPeriodSwitchSetCmd input = context.getCommand();
		Optional<InitDisplayPeriodSwitchSet> initDisplayPeriod =  repo.findByKey(input.getCompanyId(), input.getRoleId());
		if(initDisplayPeriod.isPresent()) {
			repo.deleteByRoleAndCompany(input.getCompanyId(), input.getRoleId());
		}
		
	}
}
