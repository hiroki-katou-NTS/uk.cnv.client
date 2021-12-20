package nts.uk.ctx.sys.env.ws.mailnoticeset.maildestination;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.env.app.command.mailnoticeset.maildestination.MailDestinationFunctionManageCommand;
import nts.uk.ctx.sys.env.app.command.mailnoticeset.maildestination.RegisterMailDestinationFunctionManageCommandHandler;

@Path("sys/env/mailnoticeset/maildestination")
@Produces(MediaType.APPLICATION_JSON)
public class MailDestinationFunctionManageWebService extends WebService {

	@Inject
	private RegisterMailDestinationFunctionManageCommandHandler registerCommandHandler;
	
	@POST
	@Path("/register")
	public void register(MailDestinationFunctionManageCommand command) {
		this.registerCommandHandler.handle(command);
	}
}
