package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.function.dom.adapter.alarm.MailExportRolesDto;

import java.util.List;

@AllArgsConstructor
@Getter
public class AlarmMailSettingDataDto {
    private List<AlarmExecutionMailSettingDto> alarmExecutionMailSetting;
    private AlarmMailSendingRoleDto alarmMailSendingRole;
    private List<MailExportRolesDto> roleList;
}
