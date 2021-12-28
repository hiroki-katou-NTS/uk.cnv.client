package nts.uk.file.at.ws.manhoursummarytable;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.uk.file.at.app.export.manhoursummarytable.ManHourSummaryTableExportExcel;
import nts.uk.file.at.app.export.manhoursummarytable.ManHourSummaryTableQuery;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("at/function/kha/003/d/report/")
@Produces("application/json")
public class ManHourSummaryTableWebService extends WebService {
   @Inject
   private ManHourSummaryTableExportExcel exportService;

    @POST
    @Path("excel")
    public ExportServiceResult generate(ManHourSummaryTableQuery fileQuery) {
        return exportService.start(fileQuery);
    }
}
