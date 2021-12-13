package nts.uk.ctx.at.record.ws.dailyperformanceprocessing.creationprocess;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.app.command.dailyperformanceprocessing.creationprocess.RegisterCreatingDailyResultsConditionCommandHandler;
import nts.uk.ctx.at.record.app.find.dailyperformanceprocessing.creationprocess.CreateFutureDayCheckParam;
import nts.uk.ctx.at.record.app.find.dailyperformanceprocessing.creationprocess.CreatingDailyResultsConditionFinder;

@Path("at/record/createDailyResultsCondtion")
@Produces("application/json")
public class CreateDailyResultsConditionWebService extends WebService {

	@Inject
	private CreatingDailyResultsConditionFinder finder;
	
	@Inject
	private RegisterCreatingDailyResultsConditionCommandHandler registerCommandHandler;
	
	@POST
	@Path("/isCreatingFutureDay")
	public JavaTypeResult<Boolean> isCreatingFutureDay(CreateFutureDayCheckParam param) {
		return new JavaTypeResult<>(this.finder.isCreatingFutureDay(param));
	}
	
	@POST
	@Path("/get")
	public JavaTypeResult<Boolean> getCreatingDailyResultsCondition() {
		return new JavaTypeResult<>(this.finder.getCreatingDailyResultsCondition());
	}
	
	@POST
	@Path("/save/{value}")
	public void save(@PathParam("value") Integer value) {
		this.registerCommandHandler.handle(value);
	}
}
