package nts.uk.screen.com.ws.cmf.cmf001;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportSettingDto;
import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportSettingListItemDto;
import nts.uk.screen.com.app.cmf.cmf001.b.get.GetExternalImportSetting;
import nts.uk.screen.com.app.cmf.cmf001.e.save.Cmf001eSaveCommand;
import nts.uk.screen.com.app.cmf.cmf001.e.save.Cmf001eSaveCommandHandler;


@Path("screen/com/cmf/cmf001/e")
@Produces(MediaType.APPLICATION_JSON)
public class Cmf001eWebService {
	
	@Inject
	private GetExternalImportSetting setting;

	@Inject
	private Cmf001eSaveCommandHandler saveCmd;
	
	@POST
	@Path("get/settings/csvbase")
	public List<ExternalImportSettingListItemDto> getAll() {
		List<ExternalImportSettingListItemDto> result = setting.getCsvBase();
		return result;
	}
	
	@POST
	@Path("get/setting/{settingCode}")
	public ExternalImportSettingDto get(@PathParam("settingCode") String settingCode) {
		ExternalImportSettingDto result = setting.get(settingCode);
		return result;
	}
	@POST
	@Path("save")
	public void save(Cmf001eSaveCommand command) {
		saveCmd.handle(command);
	}
}
