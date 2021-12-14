package nts.uk.ctx.at.function.dom.alarmworkplace.sendemail;

import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingNormal;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmDto;

import java.util.List;
import java.util.Map;

public interface WorkplaceSendEmailService {

    String alarmWorkplacesendEmail(List<String> workplaceIds,
                                 List<ValueExtractAlarmDto> listValueExtractAlarmDto,
                                 MailSettingNormal mailSettingsNormal,
                                 String currentAlarmCode,
                                 boolean useAuthentication);

}
