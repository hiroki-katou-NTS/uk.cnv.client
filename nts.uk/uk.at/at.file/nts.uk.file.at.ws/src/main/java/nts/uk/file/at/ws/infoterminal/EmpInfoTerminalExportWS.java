package nts.uk.file.at.ws.infoterminal;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.uk.file.at.app.export.employmentinfoterminal.infoterminal.EmpInfoTerminalExportDataSource;
import nts.uk.file.at.app.export.employmentinfoterminal.infoterminal.EmpInfoTerminalExportService;

/**
 * 
 * @author huylq
 *
 */
@Path("file/empInfoTerminal/report")
@Produces("application/json")
public class EmpInfoTerminalExportWS extends WebService {
	
	@Inject
	private EmpInfoTerminalExportService empInfoTerminalExportService;
	
	//就業情報端末の情報をマスタリストに出力する
	@POST
	@Path("export")
	public ExportServiceResult generate() {
		List<EmpInfoTerminalExportDataSource> dataSource = this.empInfoTerminalExportService.getData();
		return this.empInfoTerminalExportService.start(dataSource);
	}
}
