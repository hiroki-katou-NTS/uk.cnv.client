package nts.uk.ctx.exio.ws.input.setting.assembly.revise;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.ctx.exio.app.input.setting.assembly.revise.ReviseItemDto;
import nts.uk.ctx.exio.app.input.setting.assembly.revise.ReviseItemFinder;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;

@Path("exio/input/setting/assembly/revise/reviseitem")
@Produces(MediaType.APPLICATION_JSON)
public class ReviseItemWebService {

	@Inject
	private ReviseItemFinder finder;
	
	@POST
	@Path("get/{settingCode}/{itemNo}")
	public ReviseItemDto get(
			@PathParam("settingCode") String settingCode,
			@PathParam("itemNo") int itemNo) {
		
		return finder.find(new ExternalImportCode(settingCode), itemNo)
				.orElse(new ReviseItemDto(settingCode, itemNo, null));
	}
}
