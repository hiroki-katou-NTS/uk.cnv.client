package nts.uk.screen.com.ws.equipment.classificationmaster;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.com.app.command.equipment.classificationmaster.DeleteEquipmentClsCommandHandler;

@Path("com/screen/oem004/")
@Produces("application/json")
public class Oem004WebService extends WebService {

	@Inject
	private DeleteEquipmentClsCommandHandler deleteCommandHandler;
	
	@POST
	@Path("delete")
	public void delete(String command) {
		this.deleteCommandHandler.handle(command);
	}
	
}
