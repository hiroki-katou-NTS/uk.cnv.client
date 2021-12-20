package nts.uk.ctx.at.function.app.command.holidaysremaining;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.function.dom.holidaysremaining.repository.HolidaysRemainingManagementRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

@Stateless
@Transactional
public class RemoveHdRemainManageCommandHandler extends CommandHandler<HdDeleteRemainManageCommand> {

	@Inject
	private HolidaysRemainingManagementRepository holidaysRemainingManagementRepository;

	@Override
	protected void handle(CommandHandlerContext<HdDeleteRemainManageCommand> context) {
		val command = context.getCommand();
		holidaysRemainingManagementRepository.remove(command.getLayOutId());
	}
}
