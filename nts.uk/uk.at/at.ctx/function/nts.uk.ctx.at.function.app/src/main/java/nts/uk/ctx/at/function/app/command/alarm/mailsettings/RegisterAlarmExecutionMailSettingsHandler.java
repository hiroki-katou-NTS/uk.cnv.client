package nts.uk.ctx.at.function.app.command.alarm.mailsettings;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmListExecutionMailSetting;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmListExecutionMailSettingRepository;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmMailSendingRole;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmMailSendingRoleRepository;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.IndividualWkpClassification;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * アラームリスト実行メール設定（職場別）を登録する
 *
 * @author rafiqul.islam
 */
@Stateless
public class RegisterAlarmExecutionMailSettingsHandler extends CommandHandler<RegisterAlarmExecutionMailSettingsCommand> {

    @Inject
    private AlarmListExecutionMailSettingRepository mailSettingRepository;

    @Inject
    private AlarmMailSendingRoleRepository roleRepository;


    @Override
    protected void handle(CommandHandlerContext<RegisterAlarmExecutionMailSettingsCommand> commandContext) {
        RegisterAlarmExecutionMailSettingsCommand command = commandContext.getCommand();
        List<RegisterAlarmExecutionMailSettingsCommand.AlarmListExecutionMailCmd> mailSettings = command.getMailSettingList();
        List<AlarmListExecutionMailSetting> exMailList = mailSettingRepository.getByCId(AppContexts.user().companyId(), IndividualWkpClassification.WORKPLACE.value);
        List<AlarmListExecutionMailSetting> settingList = mailSettings.stream().map(x -> {
            return command.toMailSettingDomain(x);
        }).collect(Collectors.toList());
        if (!exMailList.isEmpty()) {
            mailSettingRepository.updateAll(settingList);
        } else {
            mailSettingRepository.insertAll(settingList);
        }
        Optional<AlarmMailSendingRole> sendingRoleDB = roleRepository.find(AppContexts.user().companyId(), IndividualWkpClassification.WORKPLACE.value);
        if (!sendingRoleDB.isPresent()) {
            roleRepository.insert(command.toRoleDomain());
        } else {
            roleRepository.update(command.toRoleDomain());
        }
    }
}
