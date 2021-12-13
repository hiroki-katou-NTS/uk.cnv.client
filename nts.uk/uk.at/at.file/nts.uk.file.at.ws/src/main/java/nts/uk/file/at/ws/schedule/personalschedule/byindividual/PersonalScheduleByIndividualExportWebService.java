package nts.uk.file.at.ws.schedule.personalschedule.byindividual;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.PersonalScheduleByDateExportService;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.PersonalScheduleByDateQuery;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.PersonalScheduleByIndividualExportService;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.PersonalScheduleByIndividualQuery;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("ctx/at/schedule/personal/by-individual")
@Produces("application/json")
public class PersonalScheduleByIndividualExportWebService extends WebService {
    @Inject
    private PersonalScheduleByIndividualExportService exportService;

    @POST
    @Path("export")
    public ExportServiceResult generate(PersonalScheduleByIndividualQuery query) {
        return this.exportService.start(query);
    }
}
