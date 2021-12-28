/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.ws.mailserver;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.env.app.command.mailserver.MailServerSaveCommand;
import nts.uk.ctx.sys.env.app.command.mailserver.MailServerSaveCommandHandler;
import nts.uk.ctx.sys.env.app.command.testsendmail.MailServerTestCommand;
import nts.uk.ctx.sys.env.app.command.testsendmail.MailServerTestCommandHanlder;
import nts.uk.ctx.sys.env.app.command.testsendmail.CheckDataChangedCommandHandler;
import nts.uk.ctx.sys.env.app.find.mailserver.MailServerDto;
import nts.uk.ctx.sys.env.app.find.mailserver.MailServerFinder;

/**
 * The Class MailServerWs.
 */
@Path("sys/env/mailserver")
@Produces(MediaType.APPLICATION_JSON)
public class MailServerWs extends WebService {

	/** The save handler. */
	@Inject
	MailServerSaveCommandHandler saveHandler;

	/** The find handler. */
	@Inject
	MailServerFinder findHandler;
	
	
	/** The testhandler. */
	@Inject
	MailServerTestCommandHanlder testhandler;
	
	@Inject
	private CheckDataChangedCommandHandler checkDataChangedCommandHandler;
	
	/**
	 * Find.
	 *
	 * @return the mail server dto
	 */
	@Path("find")
	@POST
	public MailServerDto find() {
		return this.findHandler.find();
	}

	/**
	 * Save.
	 *
	 * @param command the command
	 */
	@Path("save")
	@POST
	public void save(MailServerSaveCommand command) {
		this.saveHandler.handle(command);
	}
	
	/**
	 * Test mail setting.
	 *
	 * @param command the command
	 */
	@Path("testMailSetting")
	@POST
	public void testMailSetting(MailServerTestCommand command) {
		this.testhandler.handle(command);
	}
	
	@Path("checkDataChanged")
	@POST
	public JavaTypeResult<Boolean> validatePreTest(MailServerSaveCommand command) {
		return new JavaTypeResult<>(this.checkDataChangedCommandHandler.handle(command));
	}
}
