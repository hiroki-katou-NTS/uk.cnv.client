package nts.uk.ctx.at.shared.ws.scherec.taskmanagement.taskmaster;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.shared.app.find.scherec.taskmanagement.taskmaster.TaskMasterFinder;
import nts.uk.ctx.at.shared.app.query.task.TaskDto;
import nts.uk.ctx.at.shared.app.query.task.TaskMasterDto;

@Path("at/shared/scherec/taskmanagement/taskmaster")
@Produces("application/json")
public class TaskMasterWebService extends WebService {
    @Inject
    private TaskMasterFinder finder;

    @POST
    @Path("tasks")
    public List<TaskMasterDto> getListTask(TaskDto request) {
    	return finder.getListTask(request);
    }
}
