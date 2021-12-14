package nts.uk.file.at.ws.schedule.export;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.gul.excel.ExcelFileTypeException;
import nts.uk.file.at.app.schedule.export.WorkPlaceScheDataSource;
import nts.uk.file.at.app.schedule.export.WorkPlaceScheExportService;
import nts.uk.file.at.app.schedule.filemanagement.CapturedRawDataDto;
import nts.uk.file.at.app.schedule.filemanagement.ScreenQueryWorkPlaceCheckFile;
import nts.uk.file.at.app.schedule.filemanagement.WorkPlaceScheCheckFileParam;
import nts.uk.file.at.ws.schedule.export.dto.CaptureDataOutputDto;

@Path("wpl/schedule/report")
@Produces("application/json") 
public class WorkPlaceScheWebService extends WebService {
    
    @Inject
    private WorkPlaceScheExportService exportService;
    
    @Inject
    private ScreenQueryWorkPlaceCheckFile checkFileService;
    
    @POST
    @Path("print")
    public ExportServiceResult generate(WorkPlaceScheDataSource query) {
        return exportService.start(query);
    }
    
    @POST
    @Path("checkFileUpload")
    public CapturedRawDataDto checkFile(WorkPlaceScheCheckFileParam checkFileParam) throws Exception {
        try {
            return checkFileService.processingFile(checkFileParam);
        } catch (ExcelFileTypeException e) {
            throw e;
        }
    }
    
    @POST
    @Path("getCaptureData")
    public CaptureDataOutputDto getCaptureData(CaptureDataInput param) {
        return CaptureDataOutputDto.fromDomain(checkFileService.getCaptureData(param.getData(), param.isOverwrite()));
    }
}
