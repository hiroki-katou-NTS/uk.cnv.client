package nts.uk.screen.com.ws.cmf.cmf001;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.screen.com.app.cmf.cmf001.c.delete.Cmf001cDeleteCommand;
import nts.uk.screen.com.app.cmf.cmf001.c.delete.Cmf001cDeleteCommandHandler;
import nts.uk.screen.com.app.cmf.cmf001.c.get.GetImportableItemAndConstraint;
import nts.uk.screen.com.app.cmf.cmf001.c.get.ImportableItemDto;
import nts.uk.screen.com.app.cmf.cmf001.c.save.Cmf001cSaveCommand;
import nts.uk.screen.com.app.cmf.cmf001.c.save.Cmf001cSaveCommandHandler;

@Path("screen/com/cmf/cmf001")
@Produces(MediaType.APPLICATION_JSON)
public class Cmf001cWebService {

	@Inject
	private GetImportableItemAndConstraint importableItem;
	
	@POST
	@Path("importable-item/{settingCode}/{itemNo}")
	public ImportableItemDto getDomainConstraint(
			@PathParam("settingCode") String settingCode,
			@PathParam("itemNo") int itemNo) {
		
		return importableItem.get(settingCode, itemNo);
	}
	
	@Inject
	private Cmf001cSaveCommandHandler saveHandler;
	
	@POST
	@Path("save")
	public void save(Cmf001cSaveCommand command) {
		saveHandler.handle(command);
	}
	
	@Inject
	private Cmf001cDeleteCommandHandler deleteHandler;
	
	@POST
	@Path("delete")
	public void delete(Cmf001cDeleteCommand command) {
		deleteHandler.handle(command);
	}
}
