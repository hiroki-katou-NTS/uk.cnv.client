package nts.uk.ctx.at.shared.ws.worktime.filtercriteria;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.command.worktime.filtercriteria.RegisterKeywordCommand;
import nts.uk.ctx.at.shared.app.command.worktime.filtercriteria.RegisterKeywordCommandHandler;
import nts.uk.ctx.at.shared.app.find.worktime.dto.WorkTimeDto;
import nts.uk.ctx.at.shared.app.find.worktime.filtercriteria.SearchByDetailsParam;
import nts.uk.ctx.at.shared.app.find.worktime.filtercriteria.SearchByKeywordParam;
import nts.uk.ctx.at.shared.app.find.worktime.filtercriteria.WorkHoursFilterConditionFinder;

@Path("at/shared/worktime/filtercondition")
@Produces(MediaType.APPLICATION_JSON)
public class WorkHoursFilterConditionWebService extends WebService {

	@Inject
	private RegisterKeywordCommandHandler registerKeywordCommandHandler;
	
	@Inject
	private WorkHoursFilterConditionFinder workHoursFilterConditionFinder;
	
	@POST
	@Path("/registerKeyword")
	public void registerKeyword(RegisterKeywordCommand command) {
		this.registerKeywordCommandHandler.handle(command);
	}
	
	@POST
	@Path("/searchByKeyword")
	public List<WorkTimeDto> searchByKeyword(SearchByKeywordParam param) {
		return this.workHoursFilterConditionFinder.searchByKeyword(param);
	}
	
	@POST
	@Path("/searchByDetails")
	public List<WorkTimeDto> searchByDetails(SearchByDetailsParam param) {
		return this.workHoursFilterConditionFinder.searchByDetails(param);
	}
}
