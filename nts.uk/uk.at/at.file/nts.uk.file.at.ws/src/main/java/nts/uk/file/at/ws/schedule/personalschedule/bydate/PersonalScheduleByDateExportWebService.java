package nts.uk.file.at.ws.schedule.personalschedule.bydate;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.PersonalScheduleByDateExportService;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.PersonalScheduleByDateQuery;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("ctx/at/schedule/personal/by-date")
@Produces("application/json")
public class PersonalScheduleByDateExportWebService extends WebService {
    @Inject
    private PersonalScheduleByDateExportService exportService;

    @POST
    @Path("export")
    public ExportServiceResult generate(PersonalScheduleByDateQuery query) {
        return this.exportService.start(query);
    }
}
