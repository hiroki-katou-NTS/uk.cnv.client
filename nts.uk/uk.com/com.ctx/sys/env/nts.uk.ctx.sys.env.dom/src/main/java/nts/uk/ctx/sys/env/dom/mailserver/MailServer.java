/**
 * 
 */
package nts.uk.ctx.sys.env.dom.mailserver;

import java.util.Comparator;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * The Class MailServer.
 */

/**
 * Gets the imap info.
 *
 * @return the imap info
 */
@Getter
// メールサーバー								
public class MailServer extends AggregateRoot implements Comparable<MailServer> {

	/** The company id. */
	// 会社ID
	private String companyId;

	/** The use authentication. */
	// メール送信認証
	private UseAuthentication useAuthentication;

	/** The encryption method. */
	// 暗号化方式
	private EncryptionMethod encryptionMethod;

	/** The authentication method. */
	// 認証方式
	private AuthenticationMethod authenticationMethod;

	/** The email authentication. */
	// 認証用メールアドレス
	private MailAddress emailAuthentication;

	/** The password. */
	// パスワード
	private Password password;

	/** The smtp info. */
	// SMTP情報
	private SmtpInfo smtpInfo;

	/** The pop info. */
	// POP情報
	private PopInfo popInfo;

	/** The imap info. */
	// IMAP情報
	private ImapInfo imapInfo;

	/**
	 * Instantiates a new mail server.
	 *
	 * @param memento
	 *            the memento
	 */
	public MailServer(MailServerGetMemento memento) {
		this.companyId = memento.getCompanyId();
		this.useAuthentication = memento.getUseAuthentication();
		this.encryptionMethod = memento.getEncryptionMethod();
		this.authenticationMethod = memento.getAuthenticationMethod();
		this.emailAuthentication = memento.getEmailAuthentication();
		this.password = memento.getPassword();
		this.smtpInfo = memento.getSmtpInfo();
		this.popInfo = memento.getPopInfo();
		this.imapInfo = memento.getImapInfo();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(MailServerSetMemento memento) {
		memento.setCompanyId(this.companyId);
		memento.setUseAuthentication(this.useAuthentication);
		memento.setEncryptionMethod(this.encryptionMethod);
		memento.setAuthenticationMethod(this.authenticationMethod);
		memento.setEmailAuthentication(this.emailAuthentication);
		memento.setPassword(this.password);
		memento.setSmtpInfo(this.smtpInfo);
		memento.setPopInfo(this.popInfo);
		memento.setImapInfo(this.imapInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailServer other = (MailServer) obj;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		return true;
	}

	@Override
	public int compareTo(MailServer o) {
		return Comparator.comparing(MailServer::getCompanyId)
				.thenComparing(MailServer::getUseAuthentication)
				.thenComparing(MailServer::getEncryptionMethod)
				.thenComparing(MailServer::getAuthenticationMethod)
				.thenComparing(MailServer::getEmailAuthentication)
				.thenComparing(MailServer::getPassword)
				.thenComparing(MailServer::getSmtpInfo)
				.thenComparing(MailServer::getPopInfo)
				.thenComparing(MailServer::getImapInfo)
				.compare(this, o);
	}
}
