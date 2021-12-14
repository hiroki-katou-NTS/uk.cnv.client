package nts.uk.ctx.at.function.dom.alarm.sendemail;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;

public interface SendEmailService {
	public String alarmSendEmail(String companyID, GeneralDate executeDate, List<String> listEmployeeTagetId,
			List<String> listManagerTagetId, List<ValueExtractAlarmDto> listValueExtractAlarmDto,
			MailSettingsParamDto mailSettingsParamDto,String currentAlarmCode,
			boolean useAuthentication,Optional<MailSettings> mailSetting,Optional<MailSettings> mailSettingAdmins,Optional<String> senderAddress);
}
