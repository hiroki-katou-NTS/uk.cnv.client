package nts.uk.ctx.at.function.app.command.alarm.mailsettings;

import java.util.List;

import lombok.Value;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;

@Value
public class MailSettingsCommand {

	private String subject;

	private String text;

	private List<String> mailAddressBCC;

	private List<String> mailAddressCC;

	private String mailRely;
	
	public MailSettings toDomain(){
		return new MailSettings(subject, text, mailAddressCC, mailAddressBCC, mailRely);
	}
	
}
