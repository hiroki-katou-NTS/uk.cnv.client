package nts.uk.ctx.bs.employee.ws.groupcommonmaster;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.bs.employee.app.command.groupcommonmaster.SaveGroupCommonMasterCommand;
import nts.uk.ctx.bs.employee.app.command.groupcommonmaster.SaveGroupCommonMasterCommandHandler;
import nts.uk.ctx.bs.employee.app.find.groupcommonmaster.GroupCommonItemDto;
import nts.uk.ctx.bs.employee.app.find.groupcommonmaster.GroupCommonMasterDto;
import nts.uk.ctx.bs.employee.app.find.groupcommonmaster.GroupCommonMasterFinder;

/**
 * 
 * @author sonnlb
 *
 */
@Path("bs/employee/groupcommonmaster")
@Produces(MediaType.APPLICATION_JSON)
public class GroupCommonMasterWebServices extends WebService {

	@Inject
	private GroupCommonMasterFinder commonFinder;

	@Inject
	private SaveGroupCommonMasterCommandHandler saveHandler;

	@POST
	@Path("getmaster")
	public List<GroupCommonMasterDto> getMaster() {
		return this.commonFinder.getMaster();
	}

	@POST
	@Path("getitems/{commonMasterId}")
	public List<GroupCommonItemDto> getItems(@PathParam("commonMasterId") String commonMasterId) {
		return this.commonFinder.getItems(commonMasterId);
	}

	@POST
	@Path("savemaster")
	public void getCurrentHistoryItem(SaveGroupCommonMasterCommand command) {
		this.saveHandler.handle(command);
	}

}
