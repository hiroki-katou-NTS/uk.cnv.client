package nts.uk.ctx.at.record.app.command.workrecord.workmanagement.manhoursummarytable;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryTableFormatRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

/**
 * ＜＜Command＞＞ 工数集計表の複製登録する
 */
@Stateless
public class RegisterManHourSummaryTableCommandHandler extends CommandHandler<RegisterOrUpdateManHourSummaryTableCommand> {
    @Inject
    private ManHourSummaryTableFormatRepository repository;

    @Override
    protected void handle(CommandHandlerContext<RegisterOrUpdateManHourSummaryTableCommand> commandHandlerContext) {
        RegisterOrUpdateManHourSummaryTableCommand command = commandHandlerContext.getCommand();
        if (command == null) return;

        // Check duplicate
        val checkDuplicate = this.repository.get(AppContexts.user().companyId(), command.getCode());
        if (!checkDuplicate.isPresent()) {
            this.repository.insert(command.toDomain());
        } else {
            throw new BusinessException("Msg_2204");
        }
    }
}
