package nts.uk.ctx.at.function.dom.alarm.mailsettings;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;

/**
 * @author ThanhPV
 * アラームリストメール設定
 */

@Getter
public class MailSettings extends DomainObject {

	/**
	 * 件名
	 */
	private Optional<Subject> subject;
	/**
	 * 本文
	 */
	private Optional<Content> text;
	/**
	 * BBCメールアドレス
	 */
	private List<MailAddress> mailAddressBCC;
	/**
	 * CCメールアドレス
	 */
	private List<MailAddress> mailAddressCC;
	/**
	 * 返信用メールアドレス
	 */
	private Optional<MailAddress> mailRely;

	public MailSettings(String subject, String text,  List<MailAddress> mailAddressCC, List<MailAddress> mailAddressBCC,
			String mailRely) {
		super();
		this.subject = Optional.ofNullable(new Subject(subject));
		this.text = Optional.ofNullable(new Content(text));
		this.mailAddressBCC = mailAddressBCC;
		this.mailAddressCC = mailAddressCC;
		this.mailRely = Optional.ofNullable(new MailAddress(mailRely));
	}
}
