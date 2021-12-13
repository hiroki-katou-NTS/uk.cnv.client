package nts.uk.ctx.at.schedule.ws.task.taskschedule;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.command.task.taskpalette.TaskPaletteAddCommandHandler;
import nts.uk.ctx.at.schedule.app.command.task.taskpalette.TaskPaletteCommand;
import nts.uk.ctx.at.schedule.app.command.task.taskpalette.TaskPaletteDeleteCommandHandler;
import nts.uk.ctx.at.schedule.app.find.schedule.task.taskpalette.Ksu003bRequest;
import nts.uk.ctx.at.schedule.app.find.schedule.task.taskpalette.TaskPaletteDto;
import nts.uk.ctx.at.schedule.app.find.schedule.task.taskpalette.TaskPaletteFinder;
import nts.uk.ctx.at.schedule.app.find.schedule.task.taskpalette.TaskPaletteOrgnizationDto;
/**
 * 
 * @author quytb
 *
 */

@Path("at/schedule/task/taskpalette")
@Produces(MediaType.APPLICATION_JSON)
public class TaskPaletteWebService extends WebService {
	@Inject
	private TaskPaletteFinder finder;
	
	@Inject
	private TaskPaletteAddCommandHandler taskPaletteAddCommandHandler;
	
	@Inject
	private TaskPaletteDeleteCommandHandler taskPaletteDeleteCommandHandler;
	
	
	@POST
	@Path("findOne")
	public TaskPaletteOrgnizationDto findOne(Ksu003bRequest request) {
		return this.finder.findOne(request.getTargetUnit(), request.getTargetId(), request.getPage(), request.getReferenceDate());
	}
	
	@POST
	@Path("getAll")
	public List<TaskPaletteDto> getAll(Ksu003bRequest request) {
		return this.finder.findTaskPalette(request.getTargetUnit(), request.getTargetId(), request.getReferenceDate());
	}

	@POST
	@Path("register")
	public void register(TaskPaletteCommand command) {
		this.taskPaletteAddCommandHandler.handle(command);
	}
	
	@POST
	@Path("remove")
	public void remove(TaskPaletteCommand command) {
		this.taskPaletteDeleteCommandHandler.handle(command);
	}
}
