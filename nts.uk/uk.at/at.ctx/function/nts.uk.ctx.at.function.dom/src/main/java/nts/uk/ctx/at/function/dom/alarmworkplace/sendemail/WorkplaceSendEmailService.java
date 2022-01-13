package nts.uk.ctx.at.function.dom.alarmworkplace.sendemail;

import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmListExecutionMailSetting;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettingNormal;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmDto;
import nts.uk.ctx.at.function.dom.alarm.sendemail.ValueExtractAlarmManualDto;

import java.util.List;
import java.util.Map;

public interface WorkplaceSendEmailService {

    String alarmWorkplacesendEmail(List<String> workplaceIds,
                                   List<ValueExtractAlarmDto> listValueExtractAlarmDto,
                                   MailSettingNormal mailSettingsNormal,
                                   String currentAlarmCode,
                                   boolean useAuthentication);

    Map<String, List<String>> alarmWorkplacesendEmail(Map<String, List<String>> administratorTarget,
                                                      List<ValueExtractAlarmManualDto> listValueExtractAlarmDto,
                                                      AlarmListExecutionMailSetting mailSettingsNormal,
                                                      String currentAlarmCode,
                                                      boolean useAuthentication);

    List<String> alarmWorkplacesendEmail(List<String> empList,
                                         List<ValueExtractAlarmManualDto> listValueExtractAlarmDto,
                                         AlarmListExecutionMailSetting mailSettingsNormal,
                                         String currentAlarmCode,
                                         boolean useAuthentication);

}
