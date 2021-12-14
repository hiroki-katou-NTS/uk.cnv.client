package nts.uk.ctx.office.ws.equipment;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.office.app.command.equipment.information.EquipmentInformationCommand;
import nts.uk.ctx.office.app.command.equipment.information.EquipmentInformationDeleteCommandHandler;
import nts.uk.ctx.office.app.command.equipment.information.EquipmentInformationInsertCommandHandler;
import nts.uk.ctx.office.app.command.equipment.information.EquipmentInformationUpdateCommandHandler;

@Path("ctx/office/equipment/information/")
@Produces("application/json")
public class EquipmentInformationWebService extends WebService {

	@Inject
	private EquipmentInformationInsertCommandHandler insertCommandHandler;
	
	@Inject
	private EquipmentInformationUpdateCommandHandler updateCommandHandler;
	
	@Inject
	private EquipmentInformationDeleteCommandHandler deleteCommandHandler;

	@POST
	@Path("insert")
	public void insert(EquipmentInformationCommand command) {
		this.insertCommandHandler.handle(command);
	}
	
	@POST
	@Path("update")
	public void update(EquipmentInformationCommand command) {
		this.updateCommandHandler.handle(command);
	}
	
	@POST
	@Path("delete/{code}")
	public void delete(@PathParam("code") String code) {
		this.deleteCommandHandler.handle(code);
	}
}
