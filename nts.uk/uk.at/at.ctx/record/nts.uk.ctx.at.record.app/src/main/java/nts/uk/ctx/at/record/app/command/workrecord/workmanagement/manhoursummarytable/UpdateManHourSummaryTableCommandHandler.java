package nts.uk.ctx.at.record.app.command.workrecord.workmanagement.manhoursummarytable;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormatRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * ＜＜Command＞＞ 工数集計表の更新登録する
 */
@Stateless
public class UpdateManHourSummaryTableCommandHandler extends CommandHandler<RegisterOrUpdateManHourSummaryTableCommand> {
    @Inject
    private ManHourSummaryTableFormatRepository repository;

    @Override
    protected void handle(CommandHandlerContext<RegisterOrUpdateManHourSummaryTableCommand> commandHandlerContext) {
        RegisterOrUpdateManHourSummaryTableCommand command = commandHandlerContext.getCommand();
        if (command == null) return;

        this.repository.update(command.toDomain());
    }
}
