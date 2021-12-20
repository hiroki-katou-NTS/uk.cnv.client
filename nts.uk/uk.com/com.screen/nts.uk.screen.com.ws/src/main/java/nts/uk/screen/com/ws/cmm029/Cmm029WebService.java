package nts.uk.screen.com.ws.cmm029;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.com.app.command.cmm029.RegisterFunctionSettingCommand;
import nts.uk.screen.com.app.command.cmm029.RegisterFunctionSettingCommandHandler;
import nts.uk.screen.com.app.find.cmm029.DisplayDataDto;
import nts.uk.screen.com.app.find.cmm029.InitialDisplayScreenQuery;

@Path("com/screen/cmm029")
@Produces("application/json")
public class Cmm029WebService extends WebService {

	@Inject
	private RegisterFunctionSettingCommandHandler registerFunctionSettingCommandHandler;
	
	@Inject
	private InitialDisplayScreenQuery initialDisplayScreenQuery;
	
	@POST
	@Path("/initDisplay")
	public List<DisplayDataDto> initDisplay() {
		return this.initialDisplayScreenQuery.findDisplayData();
	}
	
	@POST
	@Path("/register")
	public void register(RegisterFunctionSettingCommand command) {
		this.registerFunctionSettingCommandHandler.handle(command);
	}
}
