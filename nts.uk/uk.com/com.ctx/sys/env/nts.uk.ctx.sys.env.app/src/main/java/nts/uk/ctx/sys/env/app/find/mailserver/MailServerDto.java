/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.app.find.mailserver;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.sys.env.app.command.mailserver.ImapInfoDto;
import nts.uk.ctx.sys.env.app.command.mailserver.PopInfoDto;
import nts.uk.ctx.sys.env.app.command.mailserver.SmtpInfoDto;
import nts.uk.ctx.sys.env.dom.mailserver.AuthenticationMethod;
import nts.uk.ctx.sys.env.dom.mailserver.EncryptionMethod;
import nts.uk.ctx.sys.env.dom.mailserver.ImapInfo;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;
import nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento;
import nts.uk.ctx.sys.env.dom.mailserver.Password;
import nts.uk.ctx.sys.env.dom.mailserver.PopInfo;
import nts.uk.ctx.sys.env.dom.mailserver.SmtpInfo;
import nts.uk.ctx.sys.env.dom.mailserver.UseAuthentication;

/**
 * The Class MailServerDto.
 */
@Getter
@Setter
public class MailServerDto implements MailServerSetMemento {

	/** The use auth. */
	private int useAuth;

	/** The encryption method. */
	private int encryptionMethod;

	/** The authentication method. */
	private int authMethod;

	/** The email authencation. */
	private String emailAuthencation;

	/** The password. */
	private String password;

	/** The smtp dto. */
	private SmtpInfoDto smtpDto;

	/** The pop dto. */
	private PopInfoDto popDto;

	/** The imap dto. */
	private ImapInfoDto imapDto;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#setCompanyId(java.
	 * lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#
	 * setUseAuthentication(nts.uk.ctx.sys.env.dom.mailserver.UseAuthentication)
	 */
	@Override
	public void setUseAuthentication(UseAuthentication useAuthentication) {
		this.useAuth = useAuthentication.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#
	 * setEncryptionMethod(nts.uk.ctx.sys.env.dom.mailserver.EncryptionMethod)
	 */
	@Override
	public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
		this.encryptionMethod = encryptionMethod.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#
	 * setAuthenticationMethod(nts.uk.ctx.sys.env.dom.mailserver.
	 * AuthenticationMethod)
	 */
	@Override
	public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
		this.authMethod = authenticationMethod.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#
	 * setEmailAuthentication(nts.uk.ctx.sys.env.dom.mailserver.
	 * EmailAuthentication)
	 */
	@Override
	public void setEmailAuthentication(MailAddress emailAuthentication) {
		this.emailAuthencation = emailAuthentication.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#setPassword(nts.uk
	 * .ctx.sys.env.dom.mailserver.Password)
	 */
	@Override
	public void setPassword(Password password) {
		this.password = password.v();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#setSmtpInfo(nts.uk
	 * .ctx.sys.env.dom.mailserver.SmtpInfo)
	 */
	@Override
	public void setSmtpInfo(SmtpInfo smtpInfo) {
		this.smtpDto = new SmtpInfoDto(smtpInfo.getServer().v(), smtpInfo.getPort().v());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#setImapInfo(nts.uk
	 * .ctx.sys.env.dom.mailserver.ImapInfo)
	 */
	@Override
	public void setImapInfo(ImapInfo imapInfo) {
		this.imapDto = new ImapInfoDto(imapInfo.getServer().v(),
				imapInfo.getUseServer().value, imapInfo.getPort().v());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#setPopInfo(nts.uk.
	 * ctx.sys.env.dom.mailserver.PopInfo)
	 */
	@Override
	public void setPopInfo(PopInfo popInfo) {
		this.popDto = new PopInfoDto(popInfo.getServer().v(),
				popInfo.getUseServer().value, popInfo.getPort().v());
	}

}
