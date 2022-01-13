package nts.uk.ctx.at.function.ws.alarm.mailsettings;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.function.app.command.alarm.mailsettings.RegisterAlarmExecutionMailSettingsCommand;
import nts.uk.ctx.at.function.app.command.alarm.mailsettings.RegisterAlarmExecutionMailSettingsHandler;
import nts.uk.ctx.at.function.app.find.alarm.mailsettings.AlarmMailSendingRoleDto;
import nts.uk.ctx.at.function.app.find.alarm.mailsettings.MailSettingDto;
import nts.uk.ctx.at.function.app.find.alarm.mailsettings.MailSettingFinder;
import nts.uk.ctx.at.function.app.find.alarm.mailsettings.MailSettingsInformationDto;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.IndividualWkpClassification;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.PersonalManagerClassification;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author rafiq
 * アラームリスト実行メール設定を取得する
 */

@Path("at/function/alarm/exmail/settings")
@Produces("application/json")
public class MailSettingByWorkPlace extends WebService {

    @Inject
    private MailSettingFinder finder;
    @Inject
    private RegisterAlarmExecutionMailSettingsHandler registerCommand;

    @POST
    @Path("init")
    public MailSettingsInformationDto init() {
        AlarmMailSendingRoleDto alarmMailSendingRoleDto = finder.getRole(IndividualWkpClassification.WORKPLACE.value);
        MailSettingDto mailSettingDto = finder.getConfigured(PersonalManagerClassification.EMAIL_SETTING_FOR_ADMIN.value,
                IndividualWkpClassification.WORKPLACE.value
        );
        return new MailSettingsInformationDto(alarmMailSendingRoleDto, mailSettingDto);
    }

    @POST
    @Path("register")
    public void register(RegisterAlarmExecutionMailSettingsCommand command) {
        registerCommand.handle(command);
    }
}
