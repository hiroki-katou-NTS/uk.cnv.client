package nts.uk.ctx.at.function.app.command.alarm.mailsettings;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmListExecutionMailSettingRepository;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmMailSendingRoleRepository;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.IndividualWkpClassification;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class InsertOrUpdateMailSettingCommandHandler extends CommandHandler<InsertOrUpdateMailSettingCommand> {
    @Inject
    private AlarmListExecutionMailSettingRepository mailSettingRepo;

    @Inject
    private AlarmMailSendingRoleRepository mailSendingRoleRepo;

    @Override
    protected void handle(CommandHandlerContext<InsertOrUpdateMailSettingCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        if (command == null) return;
        val mailSendRoleCommand = command.getAlarmMailSendingRoleCommand();
        String companyId = AppContexts.user().companyId();

        val mailSettings = mailSettingRepo.getByCId(companyId, IndividualWkpClassification.INDIVIDUAL.value);
        if (mailSettings.isEmpty())
            mailSettingRepo.insertAll(command.toDomains());
        else
            mailSettingRepo.updateAll(command.toDomains());

        val mailSendingRole = mailSendingRoleRepo.find(companyId, IndividualWkpClassification.INDIVIDUAL.value);
        if (mailSendingRole.isPresent())
            mailSendingRoleRepo.update(mailSendRoleCommand.toDomain());
        else
            mailSendingRoleRepo.insert(mailSendRoleCommand.toDomain());
    }
}
