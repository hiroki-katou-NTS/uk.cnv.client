package nts.uk.ctx.pereg.ws.layout;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.pereg.app.command.layout.NewLayoutCommand;
import nts.uk.ctx.pereg.app.command.layout.NewLayoutCommandHandler;
import nts.uk.ctx.pereg.app.find.layoutdef.NewLayoutDto;
import nts.uk.ctx.pereg.app.find.layoutdef.NewLayoutFinder;

@Path("ctx/pereg/person/newlayout")
@Produces("application/json")
public class NewLayoutWebservices extends WebService {

	@Inject
	private NewLayoutFinder nLayoutFinder;

	@Inject
	private NewLayoutCommandHandler commandHandler;

	@POST
	@Path("get")
	public NewLayoutDto getNewLayout() {
		return nLayoutFinder.getLayout();
	}

	@POST
	@Path("save")
	public void addMaintenanceLayout(NewLayoutCommand command) {
		this.commandHandler.handle(command);
	}
	
	@POST
	@Path("get-layout-can-null")
	public NewLayoutDto getLayoutCanNull() {
		return nLayoutFinder.getLayoutCanNull();
	}
}
