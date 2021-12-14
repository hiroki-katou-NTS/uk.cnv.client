package nts.uk.ctx.sys.gateway.ws.sendmail;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.gateway.app.command.sendmail.SendMailForgetPassMobileCommandHandler;
import nts.uk.ctx.sys.gateway.app.command.sendmail.SendMailInfoCommand;
import nts.uk.ctx.sys.gateway.app.command.sendmail.SendMailInfoCommandHandler;
import nts.uk.ctx.sys.gateway.app.command.sendmail.SendMailInfoFormGCommand;
import nts.uk.ctx.sys.gateway.app.command.sendmail.SendMailInfoFormGCommandHandler;
import nts.uk.ctx.sys.gateway.app.command.sendmail.dto.SendMailReturnDto;

/**
 * The Class SendMailWebService.
 */
@Path("ctx/sys/gateway/sendmail")
@Produces("application/json")
@Stateless
public class SendMailWebService extends WebService {
	
	/** The send mail info command handler. */
	@Inject 
	private SendMailInfoCommandHandler sendMailInfoCommandHandler;
	
	/** The send mail info form G command handler. */
	@Inject 
	private SendMailInfoFormGCommandHandler sendMailInfoFormGCommandHandler;
	
	/** The send mail info mobile command handler. */
	@Inject 
	private SendMailForgetPassMobileCommandHandler mobileSendMail;
	
	/**
	 * Submit send mail.
	 *
	 * @param command the command
	 * @return the string
	 */
	@POST
	@Path("submit")
	public SendMailReturnDto submitSendMail(SendMailInfoCommand command) {
		//sendMail
		return this.sendMailInfoCommandHandler.handle(command);
	}
	
	/**
	 * Submit send mail 2.
	 *
	 * @param command the command
	 * @return the send mail return dto
	 */
	@POST
	@Path("submit2")
	public List<SendMailReturnDto> submitSendMail2(SendMailInfoFormGCommand command) {
		//sendMailformG
		return this.sendMailInfoFormGCommandHandler.handle(command);
	}
	
	@POST
	@Path("mobile")
	public List<SendMailReturnDto> sendMailMobile(SendMailInfoFormGCommand command) {
		return this.mobileSendMail.handle(command);
	}
}
