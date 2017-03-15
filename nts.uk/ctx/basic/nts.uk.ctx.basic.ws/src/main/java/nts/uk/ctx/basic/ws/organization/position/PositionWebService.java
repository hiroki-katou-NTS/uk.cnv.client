package nts.uk.ctx.basic.ws.organization.position;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.basic.app.command.organization.position.AddHistoryCommand;
import nts.uk.ctx.basic.app.command.organization.position.AddHistoryCommandHandler;
import nts.uk.ctx.basic.app.command.organization.position.AddPositionCommand;
import nts.uk.ctx.basic.app.command.organization.position.AddPositionCommandHandler;
import nts.uk.ctx.basic.app.command.organization.position.DeleteHistoryCommand;
import nts.uk.ctx.basic.app.command.organization.position.DeleteHistoryCommandHandler;
import nts.uk.ctx.basic.app.command.organization.position.DeletePositionCommand;
import nts.uk.ctx.basic.app.command.organization.position.DeletePositionCommandHandler;
import nts.uk.ctx.basic.app.command.organization.position.UpdateHistoryCommand;
import nts.uk.ctx.basic.app.command.organization.position.UpdateHistoryCommandHandler;
import nts.uk.ctx.basic.app.command.organization.position.UpdatePositionCommand;
import nts.uk.ctx.basic.app.command.organization.position.UpdatePositionCommandHandler;
import nts.uk.ctx.basic.app.find.organization.position.JobHistDto;
import nts.uk.ctx.basic.app.find.organization.position.JobHistFinder;
import nts.uk.ctx.basic.app.find.organization.position.JobTitleDto;
import nts.uk.ctx.basic.app.find.organization.position.JobTitleFinder;

@Path("basic/position")
@Produces("application/json")
public class PositionWebService extends WebService {

	@Inject
	private JobTitleFinder positionFinder;
	@Inject
	private JobHistFinder histFinder;
	 @Inject
	 private AddPositionCommandHandler addPosition;
	@Inject
	private UpdatePositionCommandHandler updatePosition;
	@Inject
	private DeletePositionCommandHandler deletePosition;
	@Inject
	private DeleteHistoryCommandHandler deleteHistoryCommandHandler;
	@Inject
	private UpdateHistoryCommandHandler updateHistoryCommandHandler;
	 @Inject
	 private AddHistoryCommandHandler addHistoryCommandHandler;
	
	@POST
	@Path("findallposition/{historyId}")
	public List<JobTitleDto> findAllPosition(@PathParam("historyId") String historyId) {
		List<JobTitleDto> t = this.positionFinder.findAllPosition(historyId);
		return this.positionFinder.findAllPosition(historyId);
	}

	@POST
	@Path("findall}")
	public List<JobTitleDto> findAll() {

		return this.positionFinder.findAll();
	}

	@POST
	@Path("getallhist")
	public List<JobHistDto> init() {
		// List<JobHistDto> i =null;
		// i = histFinder.init();
		// System.out.println("==" + i);
		return this.histFinder.init();
	}
	// @POST
	// @Path("findposition/{jobCode}/{historyId}")
	// public Optional<PositionDto> find(@PathParam("jobCode") String
	// jobCode,@PathParam("historyId") String historyId){
	// return this.finder.find(jobCode, historyId);
	// }

	 @POST
	 @Path("addPosition")
	 public void add(AddPositionCommand command){
	 this.addPosition.handle(command);
	 }

	@POST
	@Path("updatePosition")
	public void update(UpdatePositionCommand command) {
		this.updatePosition.handle(command);
	}

	@POST
	@Path("deletePosition")
	public void deletePosition(DeletePositionCommand command) {
		this.deletePosition.handle(command);
	}
	
	@POST
	@Path("deleteHist")
	public void deletePosition(DeleteHistoryCommand command) {
		this.deleteHistoryCommandHandler.handle(command);
	}
	
	@POST
	@Path("updateHist")
	public void update(UpdateHistoryCommand command) {
		this.updateHistoryCommandHandler.handle(command);
	}
	
	@POST
	@Path("addHist")
	public void update(AddHistoryCommand command) {
		this.addHistoryCommandHandler.handle(command);
	}

}
