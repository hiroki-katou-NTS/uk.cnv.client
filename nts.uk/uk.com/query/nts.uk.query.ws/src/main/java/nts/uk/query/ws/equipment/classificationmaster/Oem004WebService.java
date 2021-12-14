package nts.uk.query.ws.equipment.classificationmaster;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.query.app.equipment.classificationmaster.EquipmentClassificationDto;
import nts.uk.query.app.equipment.classificationmaster.EquipmentClassificationListQuery;
import nts.uk.shr.com.context.AppContexts;

@Path("query/equipment/classificationmaster/")
@Produces("application/json")
public class Oem004WebService extends WebService {

	@Inject
	private EquipmentClassificationListQuery query;
	
	@POST
	@Path("getAll")
	public List<EquipmentClassificationDto> getAll() {
		return this.query.get(AppContexts.user().contractCode());
	}
}
