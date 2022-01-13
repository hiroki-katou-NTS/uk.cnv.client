package nts.uk.ctx.at.function.dom.alarm.sendemail;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.AlarmListExecutionMailSetting;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;

public interface SendEmailService {
	public String alarmSendEmail(String companyID, GeneralDate executeDate, List<String> listEmployeeTagetId,
			List<String> listManagerTagetId, List<ValueExtractAlarmDto> listValueExtractAlarmDto,
			MailSettingsParamDto mailSettingsParamDto,String currentAlarmCode,
			boolean useAuthentication, Optional<MailSettings> mailSettingPerson, Optional<MailSettings> mailSettingAdmins,
			Optional<String> senderAddress, List<ManagerTagetDto> managerTargetList, List<AlarmListExecutionMailSetting> alarmExeMailSetting);
}
