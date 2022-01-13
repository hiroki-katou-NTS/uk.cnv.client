package nts.uk.ctx.at.aggregation.ws.schedulecounter.employmentsetting;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.employmentsetting.CriterionAmountUsageSettingCommand;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.employmentsetting.CriterionAmountUsageSettingCommandHandler;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo.DeleteEstimatedEmploymentCommand;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo.DeleteEstimatedEmploymentCommandHandler;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo.RegisterEstimatedEmploymentCommand;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.initemploymentinfo.RegisterEstimatedEmploymentCommandHandler;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.employmentsetting.CriterionAmountUsageSettingDto;
/**
 * KML002 L
 * @author hoangnd
 *
 */
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.employmentsetting.EmploymentUsageSettingFinder;
import nts.uk.shr.com.context.AppContexts;

@Path("ctx/at/schedule/budget/employmentsetting")
@Produces("application/json")
public class EmploymentSettingWebService extends WebService {
	
	@Inject
	private EmploymentUsageSettingFinder employmentSettingFinder;
	
	@Inject
	private CriterionAmountUsageSettingCommandHandler criterionAmountUsageSettingCommandHandler;
	
	@Inject
	private RegisterEstimatedEmploymentCommandHandler registerEstimatedEmploymentCommandHandler;
	
	@Inject
	private DeleteEstimatedEmploymentCommandHandler deleteEstimatedEmploymentCommandHandler;
	
	@Path("getUsage")
	@POST
	public CriterionAmountUsageSettingDto init() {
		
		return employmentSettingFinder.getSetting(AppContexts.user().companyId());
	}
	
	@Path("registerUsage")
	@POST
	public void registerUseage(CriterionAmountUsageSettingCommand command) {
		
		criterionAmountUsageSettingCommandHandler.handle(command);
	}
	
	@Path("register")
	@POST
	public void register(RegisterEstimatedEmploymentCommand command) {		
		registerEstimatedEmploymentCommandHandler.handle(command);
	}
	
	@Path("remove")
	@POST
	public void remove(DeleteEstimatedEmploymentCommand command) {		
		deleteEstimatedEmploymentCommandHandler.handle(command);
	}
}
