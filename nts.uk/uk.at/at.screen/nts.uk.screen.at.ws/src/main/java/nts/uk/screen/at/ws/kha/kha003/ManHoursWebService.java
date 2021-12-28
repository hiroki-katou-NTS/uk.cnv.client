package nts.uk.screen.at.ws.kha.kha003;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.record.app.command.workrecord.workmanagement.manhoursummarytable.*;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.ManHourSummaryData;
import nts.uk.screen.at.app.kha003.ManHourSummaryTableFormatDto;
import nts.uk.screen.at.app.kha003.ManHoursDto;
import nts.uk.screen.at.app.kha003.a.ManHourSummaryLayoutScreenQuery;
import nts.uk.screen.at.app.kha003.a.ManHoursListScreenQuery;
import nts.uk.screen.at.app.kha003.b.CreateManHourSummaryData;
import nts.uk.screen.at.app.kha003.b.ManHourPeriod;
import nts.uk.screen.at.app.kha003.d.AggregationResultQuery;
import nts.uk.screen.at.app.kha003.d.CreateAggregationManHourResult;
import nts.uk.screen.at.app.kha003.d.ManHourAggregationResultDto;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourAggregationResultExportService;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourDataSummaryQuery;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("at/screen/kha003/")
@Produces("application/json")
public class ManHoursWebService extends WebService {
    @Inject
    private ManHoursListScreenQuery initScreen;

    @Inject
    private ManHourSummaryLayoutScreenQuery summaryLayoutInfo;

    @Inject
    private RegisterManHourSummaryTableCommandHandler register;

    @Inject
    private UpdateManHourSummaryTableCommandHandler update;

    @Inject
    private RegisterManHourSummaryTableDuplicateCommandHandler registerDuplicate;

//    @Inject
//    private UpdateManHourSummaryTableDuplicateCommandHandler updateDuplicate;

    @Inject
    private DeleteManHourSummaryTableCommandHandler delete;

    @Inject
    private CreateManHourSummaryData createManHour;

    @Inject
    private CreateAggregationManHourResult aggregationResult;

    @Inject
    private ManHourAggregationResultExportService exportCsvService;

    @POST
    @Path("a/init")
    public ManHoursDto getInitScreen() {
        return this.initScreen.getManHoursList();
    }

    @POST
    @Path("a/find/{code}")
    public ManHourSummaryTableFormatDto getManHourSummaryLayout(@PathParam("code") String code) {
        return this.summaryLayoutInfo.get(code);
    }

    @POST
    @Path("a/register")
    public void registerManHourSummaryTable(RegisterOrUpdateManHourSummaryTableCommand command) {
        this.register.handle(command);
    }

    @POST
    @Path("a/update")
    public void updateManHourSummaryTable(RegisterOrUpdateManHourSummaryTableCommand command) {
        this.update.handle(command);
    }

    @POST
    @Path("a/delete")
    public void deleteManHourSummaryTable(DeleteManHourSummaryTableCommand command) {
        this.delete.handle(command);
    }

    @POST
    @Path("b/get-data")
    public ManHourSummaryData createManHourSummaryData(ManHourPeriod param) {
        return this.createManHour.get(param);
    }

    @POST
    @Path("d/aggregation-result")
    public ManHourAggregationResultDto aggregationResult(AggregationResultQuery param) {
        return this.aggregationResult.get(param.getCode(), param.getMasterNameInfo(), param.toWorkDetailData(),
                param.getPeriod().getDateList(), param.getPeriod().getYearMonthList());
    }

    @POST
    @Path("d/export-csv")
    public ExportServiceResult generateCsv(ManHourDataSummaryQuery query) {
        return this.exportCsvService.start(query);
    }

    @POST
    @Path("e/register")
    public void registerManHourSummaryTableDuplicate(RegisterOrUpdateManHourSummaryTableDuplicateCommand command) {
        this.registerDuplicate.handle(command);
    }

//    @POST
//    @Path("e/update")
//    public void updateManHourSummaryTableDuplicate(RegisterOrUpdateManHourSummaryTableDuplicateCommand command) {
//        this.updateDuplicate.handle(command);
//    }
}
