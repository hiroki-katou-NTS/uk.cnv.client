package nts.uk.ctx.at.schedule.ws.shift.weeklyworkday;


import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.find.shift.weeklyworkday.WeeklyWorkDayDto;
import nts.uk.ctx.at.schedule.app.find.shift.weeklyworkday.WeeklyWorkDayFinder;
import nts.uk.shr.com.context.AppContexts;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("at/schedule/shift/weeklywrkday")
@Produces(MediaType.APPLICATION_JSON)
public class WeeklyWorkDayWebService extends WebService {

    @Inject
    private WeeklyWorkDayFinder weeklyWorkDayFinder;

    @POST
    @Path("getAll")
    public WeeklyWorkDayDto getWeeklyWorkDay(){
        String companyId = AppContexts.user().companyId();
        return weeklyWorkDayFinder.getWeeklyWorkDay(companyId);
    }
}
