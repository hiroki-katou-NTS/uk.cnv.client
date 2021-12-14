package nts.uk.file.com.ws.equipment.data;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.uk.file.com.app.equipment.data.EquipmentDataExportService;
import nts.uk.file.com.app.equipment.data.EquipmentDataQuery;

@Path("com/file/equipment/data/")
@Produces("application/json")
public class EquipmentDataReportWebService extends WebService {

	@Inject
	private EquipmentDataExportService exportService;
	
	@POST
	@Path("report")
	public ExportServiceResult generate(EquipmentDataQuery query) {
		return this.exportService.start(query);
	}
}
