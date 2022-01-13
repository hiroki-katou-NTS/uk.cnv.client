/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.infra.repository.mailserver;

import lombok.Setter;
import nts.uk.ctx.sys.env.dom.mailserver.AuthenticationMethod;
import nts.uk.ctx.sys.env.dom.mailserver.EncryptionMethod;
import nts.uk.ctx.sys.env.dom.mailserver.ImapInfo;
import nts.uk.ctx.sys.env.dom.mailserver.MailAddress;
import nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento;
import nts.uk.ctx.sys.env.dom.mailserver.Password;
import nts.uk.ctx.sys.env.dom.mailserver.PopInfo;
import nts.uk.ctx.sys.env.dom.mailserver.Port;
import nts.uk.ctx.sys.env.dom.mailserver.Server;
import nts.uk.ctx.sys.env.dom.mailserver.SmtpInfo;
import nts.uk.ctx.sys.env.dom.mailserver.UseAuthentication;
import nts.uk.ctx.sys.env.dom.mailserver.UseServer;
import nts.uk.ctx.sys.env.infra.entity.mailserver.SevmtMailServer;

/**
 * The Class JpaMailServerGetMemento.
 */
public class JpaMailServerGetMemento implements MailServerGetMemento {
	
	/**
	 * Sets the sevst mail server.
	 *
	 * @param sevstMailServer the new sevst mail server
	 */
	@Setter
	private SevmtMailServer sevstMailServer;
	
	/**
	 * Instantiates a new jpa mail server get memento.
	 *
	 * @param sevstMailServer the sevst mail server
	 */
	public JpaMailServerGetMemento(SevmtMailServer sevstMailServer) {
		this.sevstMailServer = sevstMailServer;
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return new String(this.sevstMailServer.getCid());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getUseAuthentication()
	 */
	@Override
	public UseAuthentication getUseAuthentication() {
		return UseAuthentication.valueOf((int) this.sevstMailServer.getUseAuth());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getEncryptionMethod()
	 */
	@Override
	public EncryptionMethod getEncryptionMethod() {
		return EncryptionMethod.valueOf((int) this.sevstMailServer.getEncryptMethod());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getAuthenticationMethod()
	 */
	@Override
	public AuthenticationMethod getAuthenticationMethod() {
		return AuthenticationMethod.valueOf((int) this.sevstMailServer.getAuthMethod());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getEmailAuthentication()
	 */
	@Override
	public MailAddress getEmailAuthentication() {
		return new MailAddress(this.sevstMailServer.getEmailAuth());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getPassword()
	 */
	@Override
	public Password getPassword() {
		return new Password(this.sevstMailServer.getPassword());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getSmtpInfo()
	 */
	@Override
	public SmtpInfo getSmtpInfo() {
		return new SmtpInfo(new Server(this.sevstMailServer.getSmtpServer()),  
						new Port(this.sevstMailServer.getSmtpPort()));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getImapInfo()
	 */
	@Override
	public ImapInfo getImapInfo() {
		return new ImapInfo(new Server(this.sevstMailServer.getImapServer()),
				UseServer.valueOf((int)this.sevstMailServer.getImapUse()),
				new Port(this.sevstMailServer.getImapPort()));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.env.dom.mailserver.MailServerGetMemento#getPopInfo()
	 */
	@Override
	public PopInfo getPopInfo() {
		return new PopInfo(new Server(this.sevstMailServer.getPopServer()),
				UseServer.valueOf((int)this.sevstMailServer.getPopUse()),
				new Port(this.sevstMailServer.getPopPort()));
	}
	
}
