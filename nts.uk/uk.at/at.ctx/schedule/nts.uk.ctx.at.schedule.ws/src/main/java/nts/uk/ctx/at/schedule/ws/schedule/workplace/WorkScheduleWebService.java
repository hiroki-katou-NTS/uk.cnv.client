package nts.uk.ctx.at.schedule.ws.schedule.workplace;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.command.schedule.workplace.ScheduleRegisterCommand;
import nts.uk.ctx.at.schedule.app.find.schedule.workplace.RegisterWorkScheduleOutput;
import nts.uk.ctx.at.schedule.app.find.schedule.workplace.ScheduleRegisterCommandHandler;

/**
 * @author anhnm
 *
 */
@Path("at/schedule/workschedulestate")
@Produces("application/json")
public class WorkScheduleWebService extends WebService {
    
    @Inject
    private ScheduleRegisterCommandHandler registerFinder;

    @POST
    @Path("register")
    public List<RegisterWorkScheduleOutput> registerSchedule(ScheduleRegisterCommand command) {
        return registerFinder.register(command);
    }
}
