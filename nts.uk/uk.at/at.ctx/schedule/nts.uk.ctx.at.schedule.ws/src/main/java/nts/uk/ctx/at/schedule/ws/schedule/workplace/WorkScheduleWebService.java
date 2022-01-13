package nts.uk.ctx.at.schedule.ws.schedule.workplace;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExecutionInfor;
import nts.uk.ctx.at.schedule.app.command.schedule.workplace.ScheduleRegisterCommand;
import nts.uk.ctx.at.schedule.app.find.schedule.workplace.ScheduleRegister;

/**
 * @author anhnm
 *
 */
@Path("at/schedule/workschedulestate")
@Produces("application/json")
public class WorkScheduleWebService extends WebService {
    
    @Inject
    private ScheduleRegister scheduleRegister;

    @POST
    @Path("register")
    public ExecutionInfor registerSchedule(ScheduleRegisterCommand command) {
        return scheduleRegister.handle(command);
    }
}
