package nts.uk.ctx.at.schedule.ws.task.taskschedule;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.command.task.taskschedule.TaskScheduleAllDaySaveCommandHandler;
import nts.uk.ctx.at.schedule.app.command.task.taskschedule.TaskScheduleCommand;
import nts.uk.ctx.at.schedule.app.command.task.taskschedule.TaskScheduleTimeZoneSaveCommandHandler;
import nts.uk.ctx.at.schedule.app.find.task.taskschedule.test.Ksu003cTestFinder;
import nts.uk.ctx.at.schedule.app.find.task.taskschedule.test.Ksu003cTestRequest;

/**
 * 
 * @author quytb
 *
 */

@Path("at/schedule/task/taskschedule")
@Produces(MediaType.APPLICATION_JSON)
public class TaskScheduleWebService extends WebService{
	
	/** for get data screen test ksu003c */
	@Inject
	private Ksu003cTestFinder finder; 
	
	@Inject
	private TaskScheduleAllDaySaveCommandHandler taskScheduleAllDaySaveCommandHandler;
	
	@Inject
	private TaskScheduleTimeZoneSaveCommandHandler taskScheduleTimeZoneSaveCommandHandler;
	
	@POST
	@Path("register")
	public void register(TaskScheduleCommand command) {
		if(command.getMode() == 0) {
			 this.taskScheduleAllDaySaveCommandHandler.handle(command);
		} else if(command.getMode() == 1) {
			this.taskScheduleTimeZoneSaveCommandHandler.handle(command);
		}		
	}
	
	
	/** get data cho màn test ksu003c*/
	@POST
	@Path("getAvailableEmpWorkSchedule")
	public List<String> getListEmp(Ksu003cTestRequest request) {
		return 	finder.getEmpWithWorkSchedule(request.getEmpIds(), request.toDate());
	}
	
}
