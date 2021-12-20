package nts.uk.screen.com.ws.cdl011;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.com.app.find.cdl011.Cdl011ScreenQuery;
import nts.uk.screen.com.app.find.cdl011.MailDestinationFunctionManageDto;

@Path("screen/com/cdl011")
@Produces(MediaType.APPLICATION_JSON)
public class Cdl001WebService extends WebService {

	@Inject
	private Cdl011ScreenQuery screenQuery;
	
	@POST
	@Path("/findData/{functionId}")
	public MailDestinationFunctionManageDto findData(@PathParam("functionId") int functionId) {
		return this.screenQuery.findData(functionId);
	}
}
