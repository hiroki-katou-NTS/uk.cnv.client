package nts.uk.ctx.at.function.app.command.alarm.mailsettings;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import lombok.val;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailAddress;
import nts.uk.ctx.at.function.dom.alarm.mailsettings.MailSettings;

@Value
public class MailSettingsCommand {

	private String subject;

	private String text;

	private List<String> mailAddressBCC;

	private List<String> mailAddressCC;

	private String mailRely;
	
	public MailSettings toDomain(){
		val mailSettingBCC = mailAddressBCC.stream().map(MailAddress::new).collect(Collectors.toList());
		val mailSettingCC = mailAddressCC.stream().map(MailAddress::new).collect(Collectors.toList());
		return new MailSettings(subject, text, mailSettingCC, mailSettingBCC, mailRely);
	}
	
}
