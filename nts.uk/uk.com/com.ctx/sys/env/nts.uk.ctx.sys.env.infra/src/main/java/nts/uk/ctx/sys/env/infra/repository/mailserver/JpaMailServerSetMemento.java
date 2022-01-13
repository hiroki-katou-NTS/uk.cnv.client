/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.infra.repository.mailserver;

import nts.uk.ctx.sys.env.dom.mailserver.AuthenticationMethod;
import nts.uk.ctx.sys.env.dom.mailserver.EncryptionMethod;
import nts.uk.ctx.sys.env.dom.mailserver.ImapInfo;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;
import nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento;
import nts.uk.ctx.sys.env.dom.mailserver.Password;
import nts.uk.ctx.sys.env.dom.mailserver.PopInfo;
import nts.uk.ctx.sys.env.dom.mailserver.SmtpInfo;
import nts.uk.ctx.sys.env.dom.mailserver.UseAuthentication;
import nts.uk.ctx.sys.env.infra.entity.mailserver.SevmtMailServer;

/**
 * The Class JpaMailServerSetMemento.
 */
public class JpaMailServerSetMemento implements MailServerSetMemento {
	
	/** The sevst mail server. */
	private SevmtMailServer sevstMailServer;

	/**
	 * Instantiates a new jpa mail server set memento.
	 *
	 * @param sevstMailServer
	 *            the sevst mail server
	 */
	public JpaMailServerSetMemento(SevmtMailServer sevstMailServer) {
		this.sevstMailServer = sevstMailServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#setCompanyId(java.
	 * lang.String)
	 */
	@Override
	public void setCompanyId(String companyId) {
		this.sevstMailServer.setCid(companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#
	 * setUseAuthentication(nts.uk.ctx.sys.env.dom.mailserver.UseAuthentication)
	 */
	@Override
	public void setUseAuthentication(UseAuthentication useAuthentication) {
		this.sevstMailServer.setUseAuth((short) useAuthentication.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerSetMemento#
	 * setEncryptionMethod(nts.uk.ctx.sys.env.dom.mailserver.EncryptionMethod)
	 */
	@Override
	public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
		this.sevstMailServer.setEncryptMethod((short) encryptionMethod.value);
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
		this.sevstMailServer.setAuthMethod((short) authenticationMethod.value);
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
		this.sevstMailServer.setEmailAuth(emailAuthentication.v());
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
		this.sevstMailServer.setPassword(password.v());
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
		this.sevstMailServer.setSmtpServer(smtpInfo.getServer().v());
		this.sevstMailServer.setSmtpPort(smtpInfo.getPort().v());
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
		this.sevstMailServer.setImapServer(imapInfo.getServer().v());
		this.sevstMailServer.setImapUse((short) imapInfo.getUseServer().value);
		this.sevstMailServer.setImapPort(imapInfo.getPort().v());
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
		this.sevstMailServer.setPopServer(popInfo.getServer().v());
		this.sevstMailServer.setPopUse((short) popInfo.getUseServer().value);
		this.sevstMailServer.setPopPort(popInfo.getPort().v());
	}

}
