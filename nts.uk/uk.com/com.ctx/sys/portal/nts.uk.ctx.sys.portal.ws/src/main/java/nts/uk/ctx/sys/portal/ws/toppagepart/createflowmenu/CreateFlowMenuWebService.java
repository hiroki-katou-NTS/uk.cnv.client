package nts.uk.ctx.sys.portal.ws.toppagepart.createflowmenu;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.CopyFileCommand;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.CopyFileCommandHandler;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.CopyFileResultDto;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.CopyFlowMenuCommand;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.CopyFlowMenuCommandHandler;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.DeleteCreateFlowMenuCommandHandler;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.DeleteFlowMenuCommand;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.FileExportCommand;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.FileExportService;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.RegisterFlowMenuCommand;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.RegisterFlowMenuCommandHandler;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.UpdateCreateFlowMenuCommandHandler;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.UpdateFlowMenuCommand;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.UpdateFlowMenuLayoutCommand;
import nts.uk.ctx.sys.portal.app.command.toppagepart.createflowmenu.UpdateFlowMenuLayoutCommandHandler;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.CopyFileResponseDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.CreateFlowMenuDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.ExtractionResponseDto;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.GetFlowMenuListScreenQuery;
import nts.uk.ctx.sys.portal.app.screenquery.topppagepart.createflowmenu.GetFlowMenuScreenQuery;
import nts.uk.ctx.sys.portal.dom.toppagepart.createflowmenu.CreateFlowMenuFileService;

@Path("sys/portal/createflowmenu")
@Produces("application/json")
public class CreateFlowMenuWebService extends WebService {

	@Inject
	private GetFlowMenuScreenQuery getFlowMenuScreenQuery;
	
	@Inject
	private GetFlowMenuListScreenQuery getFlowMenuListScreenQuery;
	
	@Inject
	private RegisterFlowMenuCommandHandler registerFlowMenuCommandHandler;
	
	@Inject
	private DeleteCreateFlowMenuCommandHandler deleteFlowMenuCommandHandler;
	
	@Inject
	private UpdateCreateFlowMenuCommandHandler updateFlowMenuCommandHandler;
	
	@Inject
	private UpdateFlowMenuLayoutCommandHandler updateFlowMenuLayoutCommandHandler;
	
	@Inject
	private CopyFlowMenuCommandHandler copyFlowMenuCommandHandler;
	
	@Inject
	private CopyFileCommandHandler copyFileCommandHandler;
	
	@Inject
	private FileExportService exportService;
	
	@Inject
	private CreateFlowMenuFileService createFlowMenuFileService;
	
	@POST
	@Path("/getFlowMenu/{flowMenuCode}")
	public CreateFlowMenuDto getFlowMenu(@PathParam(value = "flowMenuCode") String flowMenuCode) {
		return this.getFlowMenuScreenQuery.getFlowMenu(flowMenuCode);
	}
	
	@POST
	@Path("/getFlowMenu")
	public Map<String, String> getFlowMenu() {
		return this.getFlowMenuListScreenQuery.getList();
	}
	
	@POST
	@Path("/register")
	public void register(RegisterFlowMenuCommand command) {
		this.registerFlowMenuCommandHandler.handle(command);
	}
	
	@POST
	@Path("/update")
	public void update(UpdateFlowMenuCommand command) {
		this.updateFlowMenuCommandHandler.handle(command);
	}
	
	@POST
	@Path("/updateLayout")
	public void updateLayout(UpdateFlowMenuLayoutCommand command) {
		this.updateFlowMenuLayoutCommandHandler.handle(command);
	}
	
	@POST
	@Path("/delete")
	public void delete(DeleteFlowMenuCommand command) {
		this.deleteFlowMenuCommandHandler.handle(command);
	}
	
	@POST
	@Path("/copy")
	public void copy(CopyFlowMenuCommand command) {
		this.copyFlowMenuCommandHandler.handle(command);
	}
	
	@POST
	@Path("/generateHtml")
	public ExportServiceResult generateHtml(FileExportCommand command) {
		return this.exportService.start(command);
	}
	
	@POST
	@Path("/extract/{fileId}")
	public ExtractionResponseDto extractData(@PathParam("fileId") String fileId) throws IOException {
		return this.exportService.extract(fileId).orElse(null);
	}
	
	@POST
	@Path("/extractFlow/{fileId}")
	public ExtractionResponseDto extractDataFlow(@PathParam("fileId") String fileId) throws IOException {
		return this.exportService.extractFlowMenu(fileId);
	}
	
	@POST
	@Path("/extractListFileId")
	public List<ExtractionResponseDto> extractListData(ParamListFileId param) throws IOException {
		return this.exportService.extractByListFileId(param.getLstFileId());
	}
	
	@POST
	@Path("/copyFile")
	public CopyFileResultDto copyFile(CopyFileCommand command) {
		return this.copyFileCommandHandler.handle(command);
	}
	
	@POST
	@Path("/copyFile/{fileId}")
	public CopyFileResponseDto copyFile(@PathParam("fileId") String fileId) throws IOException {
		return new CopyFileResponseDto(this.createFlowMenuFileService.copyFile(fileId));
	}
}

