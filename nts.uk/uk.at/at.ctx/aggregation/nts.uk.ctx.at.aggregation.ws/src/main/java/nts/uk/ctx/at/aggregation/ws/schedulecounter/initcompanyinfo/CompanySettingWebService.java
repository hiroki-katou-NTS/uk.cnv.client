package nts.uk.ctx.at.aggregation.ws.schedulecounter.initcompanyinfo;


import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo.CompanyInfoRegisterCommand;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initcompanyinfo.CompanyInfoRegisterCommandHandler;

/**
 * KML002 H
 * @author hoangnd
 *
 */

@Path("ctx/at/schedule/budget/initCompanyInfo")
@Produces("application/json")
public class CompanySettingWebService extends WebService {
	
	
	@Inject
	private CompanyInfoRegisterCommandHandler registerHandler;
	
	
	@Path("register")
	@POST
	public void register(CompanyInfoRegisterCommand command) {
		
		registerHandler.handle(command);
	}
	
}
