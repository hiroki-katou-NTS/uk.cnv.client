package nts.uk.ctx.at.function.app.find.alarm.mailsettings;

import java.util.List;
import java.util.Optional;

import lombok.Data;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;

@Data
public class MailSettingsDto {

	private String subject;

	private String text;

	private List<String> mailAddressBCC;

	private List<String> mailAddressCC;

	private String mailRely;

	public MailSettingsDto(Optional<MailSettings> MailSettings) {
		this.subject = MailSettings.get().getSubject().get().v();
		this.text = MailSettings.get().getText().get().v();
		this.mailAddressBCC = MailSettings.get().getMailAddressBCC();
		this.mailAddressCC = MailSettings.get().getMailAddressCC();
		this.mailRely = MailSettings.get().getMailRely().get().v();
	}
	
}
