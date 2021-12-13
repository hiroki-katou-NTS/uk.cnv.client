package nts.uk.screen.com.ws.cmf.cmf001;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.screen.com.app.cmf.cmf001.b.get.GetLayoutParam;
import nts.uk.screen.com.app.cmf.cmf001.f.delete.Cmf001fDeleteCommand;
import nts.uk.screen.com.app.cmf.cmf001.f.delete.Cmf001fDeleteCommandHandler;
import nts.uk.screen.com.app.cmf.cmf001.f.get.CsvBaseLayoutDto;
import nts.uk.screen.com.app.cmf.cmf001.f.get.GetLayoutForCsvBase;
import nts.uk.screen.com.app.cmf.cmf001.f.save.Cmf001fSaveCommand;
import nts.uk.screen.com.app.cmf.cmf001.f.save.Cmf001fSaveCommandHandler;

@Path("screen/com/cmf/cmf001/f")
@Produces(MediaType.APPLICATION_JSON)
public class Cmf001fWebService {
	
	@Inject
	private GetLayoutForCsvBase layout;
	
	@Inject
	private Cmf001fSaveCommandHandler handler;
	
	@Inject
	private Cmf001fDeleteCommandHandler delHandler;
	
	@POST
	@Path("get/layout/detail")
	public List<CsvBaseLayoutDto> getLayoutDetail(GetLayoutParam query) {
		List<CsvBaseLayoutDto> result = layout.getCsvBaseDetail(query);
		return result;
	}

	@POST
	@Path("save")
	public void save(Cmf001fSaveCommand command) {
		handler.handle(command);
	}

	@POST
	@Path("delete")
	public void save(Cmf001fDeleteCommand command) {
		delHandler.handle(command);
	}
}
