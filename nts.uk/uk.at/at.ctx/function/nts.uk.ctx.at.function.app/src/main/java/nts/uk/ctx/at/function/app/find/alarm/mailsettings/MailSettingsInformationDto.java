package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.function.dom.adapter.alarm.MailExportRolesDto;

import java.util.List;

/*
 *メール設定情報
 */
@Data
@AllArgsConstructor
public class MailSettingsInformationDto {
    AlarmMailSendingRoleDto sendingRole;
    MailSettingDto mailSettings;
}
