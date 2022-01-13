/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.app.command.testsendmail;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.gul.mail.send.MailContents;
import nts.uk.shr.com.mail.MailSender;

/**
 * The Class MailServerTestCommandHanlder.
 */
@Stateless
public class MailServerTestCommandHanlder  extends CommandHandler<MailServerTestCommand> {
	
	/** The mail sender. */
	@Inject
	private MailSender mailSender; 

	/* (non-Javadoc)
	 * @see nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command.CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<MailServerTestCommand> context) {
		
		MailServerTestCommand command = context.getCommand();
		
		MailContents contents = new MailContents(command.getContents().getSubject(), command.getContents().getBody());
		
		try {
			this.mailSender.send(command.getMailFrom(), command.getMailTo(), contents);
		} catch (Exception e) {
			throw new BusinessException("Msg_1057");
		}
		
	}

}
