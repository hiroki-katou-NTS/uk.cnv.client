package nts.uk.ctx.at.record.app.command.workrecord.workmanagement.manhoursummarytable;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormatRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * ＜＜Command＞＞ 工数集計表の削除する
 */
@Stateless
public class DeleteManHourSummaryTableCommandHandler extends CommandHandler<DeleteManHourSummaryTableCommand> {
    @Inject
    private ManHourSummaryTableFormatRepository repository;

    @Override
    protected void handle(CommandHandlerContext<DeleteManHourSummaryTableCommand> commandHandlerContext) {
        DeleteManHourSummaryTableCommand command = commandHandlerContext.getCommand();
        if (command == null) return;

        this.repository.delete(AppContexts.user().companyId(), command.getCode());
    }
}
