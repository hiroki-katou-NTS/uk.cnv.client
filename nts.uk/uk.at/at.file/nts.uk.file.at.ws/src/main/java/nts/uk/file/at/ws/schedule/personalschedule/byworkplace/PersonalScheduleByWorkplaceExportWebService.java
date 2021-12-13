package nts.uk.file.at.ws.schedule.personalschedule.byworkplace;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.infra.file.storage.StoredFileInfoRepository;
import nts.arc.layer.ws.WebService;
import nts.arc.system.ServerSystemProperties;
import nts.uk.file.at.app.export.schedule.personalschedulebyworkplace.PersonalScheduleByWkpQuery;
import nts.uk.file.at.app.export.schedule.personalschedulebyworkplace.PersonalScheduleByWorkplaceExportService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.*;

@Path("ctx/at/schedule/personal/by-workplace")
@Produces("application/json")
public class PersonalScheduleByWorkplaceExportWebService extends WebService {
    @Inject
    private PersonalScheduleByWorkplaceExportService exportService;

    @Inject
    private StoredFileInfoRepository fileInfoRepository;


    @POST
    @Path("export")
    public ExportServiceResult generate(PersonalScheduleByWkpQuery query) {
        return this.exportService.start(query);
    }

    @POST
    @Path("preview/{fileid}")
    public Response preview2(@PathParam("fileid") String fileId) {
        return this.fileInfoRepository.find(fileId).map(fileInfo -> {
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(ServerSystemProperties.fileStoragePath() + "\\" + fileInfo.getOriginalName());
                return Response.ok(fileInputStream, "text/html")
                        .encoding("UTF-8")
                        .header("Content-Disposition", "inline")
                        .build();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }).orElseGet(() -> Response.status(404).build());
    }
}
