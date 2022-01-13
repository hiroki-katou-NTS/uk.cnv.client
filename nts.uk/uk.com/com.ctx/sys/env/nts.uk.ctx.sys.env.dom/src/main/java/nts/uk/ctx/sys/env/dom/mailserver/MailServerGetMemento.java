package nts.uk.ctx.sys.env.dom.mailserver;

public interface MailServerGetMemento {
	/**
	 * Gets the company id.
	 *
	 * @return the company id
	 */
	String getCompanyId();

	/**
	 * Gets use authentication
	 * 
	 * @return value
	 */
	UseAuthentication getUseAuthentication();

	/**
	 * Gets Encryption Method
	 * 
	 * @return Encryption Method
	 */
	EncryptionMethod getEncryptionMethod();

	/**
	 * Gets Authentication Method
	 * 
	 * @return Authentication Method
	 */
	AuthenticationMethod getAuthenticationMethod();

	/**
	 * Gets Email Authentication
	 * 
	 * @return Email Authentication
	 */
	MailAddress getEmailAuthentication();

	/**
	 * Gets password
	 * 
	 * @return password
	 */
	Password getPassword();
	
	/**
	 * Gets SMTP Information
	 * 
	 * @return SMTP Information
	 */
	SmtpInfo getSmtpInfo();
	
	/**
	 * Gets IMAP Information
	 * 
	 * @return password
	 */
	ImapInfo getImapInfo();
	
	/**
	 * Gets POP Information
	 * 
	 * @return POP Information
	 */
	PopInfo getPopInfo();
}
