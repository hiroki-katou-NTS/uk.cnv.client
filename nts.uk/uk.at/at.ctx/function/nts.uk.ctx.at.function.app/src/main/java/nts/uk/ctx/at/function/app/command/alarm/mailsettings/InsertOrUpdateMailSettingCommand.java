package nts.uk.ctx.at.function.app.command.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmListExecutionMailSetting;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class InsertOrUpdateMailSettingCommand {
    private List<AlarmExecutionMailSettingCommand> executionMailSettingCommand;
    private AlarmMailSendingRoleCommand alarmMailSendingRoleCommand;

    public List<AlarmListExecutionMailSetting> toDomains() {
        return this.executionMailSettingCommand.stream().map(AlarmExecutionMailSettingCommand::toDomain).collect(Collectors.toList());
    }
}
