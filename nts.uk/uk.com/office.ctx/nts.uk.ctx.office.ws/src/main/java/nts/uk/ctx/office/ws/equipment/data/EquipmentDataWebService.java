package nts.uk.ctx.office.ws.equipment.data;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.office.app.command.equipment.data.DeleteEquipmentDataCommand;
import nts.uk.ctx.office.app.command.equipment.data.DeleteEquipmentDataCommandHandler;
import nts.uk.ctx.office.app.command.equipment.data.EquipmentDataCommand;
import nts.uk.ctx.office.app.command.equipment.data.InsertEquipmentDataCommandHandler;
import nts.uk.ctx.office.app.command.equipment.data.UpdateEquipmentDataCommandHandler;

@Path("ctx/office/equipment/data/")
@Produces("application/json")
public class EquipmentDataWebService extends WebService {

	@Inject
	private InsertEquipmentDataCommandHandler insertEquipmentDataCommandHandler;
	
	@Inject
	private UpdateEquipmentDataCommandHandler updateEquipmentDataCommandHandler;
	
	@Inject
	private DeleteEquipmentDataCommandHandler deleteEquipmentDataCommandHandler;
	
	@POST
	@Path("insert")
	public void insert(EquipmentDataCommand command) {
		this.insertEquipmentDataCommandHandler.handle(command);
	}
	
	@POST
	@Path("update")
	public void update(EquipmentDataCommand command) {
		this.updateEquipmentDataCommandHandler.handle(command);
	}
	
	@POST
	@Path("delete")
	public void delete(DeleteEquipmentDataCommand command) {
		this.deleteEquipmentDataCommandHandler.handle(command);
	}
}
