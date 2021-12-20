package nts.uk.ctx.sys.auth.app.command.cmm051;


import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * 職場管理者履歴を削除する
 */
@Stateless
public class DeleteWorkplaceAdmHistoryCommandHandler extends CommandHandler<DeleteWorkplaceAdmHistoryCommand> {
    @Inject
    private WorkplaceManagerRepository workplaceManagerRepository;

    @Override
    protected void handle(CommandHandlerContext<DeleteWorkplaceAdmHistoryCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        workplaceManagerRepository.delete(command.getWkpManagerId());
    }
}
