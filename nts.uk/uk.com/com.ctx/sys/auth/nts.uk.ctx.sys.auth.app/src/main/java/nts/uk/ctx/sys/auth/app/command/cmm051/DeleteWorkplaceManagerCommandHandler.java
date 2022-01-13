package nts.uk.ctx.sys.auth.app.command.cmm051;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * 職場管理者を削除する
 */
@Stateless
public class DeleteWorkplaceManagerCommandHandler extends CommandHandler<DeleteWorkplaceManagerCommand> {
    @Inject
    private WorkplaceManagerRepository workplaceManagerRepository;

    @Override
    protected void handle(CommandHandlerContext<DeleteWorkplaceManagerCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        workplaceManagerRepository.deleteByWorkplaceIdAndSid(command.getWorkplaceId(), command.getSid());
    }
}
