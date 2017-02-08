package nts.uk.ctx.pr.core.ws.insurance.social.healthrate;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.pr.core.app.insurance.social.health.command.RegisterHealthInsuranceCommand;
import nts.uk.ctx.pr.core.app.insurance.social.health.command.RegisterHealthInsuranceCommandHandler;
import nts.uk.ctx.pr.core.app.insurance.social.health.command.UpdateHealthInsuranceCommand;
import nts.uk.ctx.pr.core.app.insurance.social.health.command.UpdateHealthInsuranceCommandHandler;
import nts.uk.ctx.pr.core.app.insurance.social.healthrate.find.AvgEarnLevelMasterSettingDto;
import nts.uk.ctx.pr.core.app.insurance.social.healthrate.find.AvgEarnLevelMasterSettingFinder;
import nts.uk.ctx.pr.core.app.insurance.social.healthrate.find.HealthInsuranceAvgearnDto;
import nts.uk.ctx.pr.core.app.insurance.social.healthrate.find.HealthInsuranceAvgearnFinder;
import nts.uk.ctx.pr.core.app.insurance.social.healthrate.find.HealthInsuranceRateDto;
import nts.uk.ctx.pr.core.app.insurance.social.healthrate.find.HealthInsuranceRateFinder;

@Path("ctx/pr/core/insurance/social/healthrate")
@Produces("application/json")
public class HealthInsuranceRateWebService extends WebService {

	@Inject
	private AvgEarnLevelMasterSettingFinder avgEarnLevelMasterSettingFinder;
	@Inject
	private HealthInsuranceRateFinder healthInsuranceRateFinder;
	@Inject
	private HealthInsuranceAvgearnFinder healthInsuranceAvgearnFinder;
	@Inject
	private RegisterHealthInsuranceCommandHandler registerHealthInsuranceCommandHandler;
	@Inject
	private UpdateHealthInsuranceCommandHandler updateHealthInsuranceCommandHandler;

	@POST
	@Path("getAvgEarnLevelMasterSettingList")
	public List<AvgEarnLevelMasterSettingDto> getAvgEarnLevelMasterSettingList() {
		return avgEarnLevelMasterSettingFinder.findAll();
	}

	@POST
	@Path("findHealthInsuranceAvgearn/{id}")
	public List<HealthInsuranceAvgearnDto> findHealthInsuranceAvgearn(@PathParam("id") String id) {
		return healthInsuranceAvgearnFinder.find(id);
	}

	@POST
	@Path("findHealthInsuranceRate/{id}")
	public HealthInsuranceRateDto findHealthInsuranceRate(@PathParam("id") String id) {
		return healthInsuranceRateFinder.find(id).get();
	}

	@POST
	@Path("create")
	public void create(RegisterHealthInsuranceCommand command) {
		registerHealthInsuranceCommandHandler.handle(command);
	}

	@POST
	@Path("update")
	public void update(UpdateHealthInsuranceCommand command) {
		updateHealthInsuranceCommandHandler.handle(command);
	}
	
	@POST
	@Path("updateHealthInsuranceAvgearn")
	public void updateHealthInsuranceAvgearn() {
		
	}
}
