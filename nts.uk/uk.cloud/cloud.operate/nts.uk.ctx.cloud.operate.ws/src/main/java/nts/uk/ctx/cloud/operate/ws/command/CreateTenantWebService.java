package nts.uk.ctx.cloud.operate.ws.command;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.cloud.operate.app.command.CreateTenantOnCloudCommand;
import nts.uk.ctx.cloud.operate.app.command.CreateTenantOnCloudCommandHandler;
import nts.uk.ctx.cloud.operate.app.command.GeneratePasswordCommand;
import nts.uk.ctx.cloud.operate.app.command.GeneratePasswordCommandHandler;

@Path("ctx/cld/operate/tenant")
@Produces("application/json")
public class CreateTenantWebService extends WebService{

	@Inject
	private CreateTenantOnCloudCommandHandler handler;

	@Inject
	private GeneratePasswordCommandHandler generatePasswordHandler;

	@POST
	@Path("regist")
	public void registTenant(CreateTenantOnCloudCommand command) {
		this.handler.handle(command);
	}

	@POST
	@Path("generatePassword")
	public void generatePassword(GeneratePasswordCommand command) {
		this.generatePasswordHandler.handle(command);
	}
}
