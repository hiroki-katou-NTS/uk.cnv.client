package nts.uk.screen.com.ws.cmf.cmf001;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportLayoutDto;
import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportSettingDto;
import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportSettingListItemDto;
import nts.uk.screen.com.app.cmf.cmf001.b.get.GetExternalImportSetting;
import nts.uk.screen.com.app.cmf.cmf001.b.get.GetLayout;
import nts.uk.screen.com.app.cmf.cmf001.b.get.GetLayoutQuery;
import nts.uk.screen.com.app.cmf.cmf001.b.save.Cmf001bSaveCommand;
import nts.uk.screen.com.app.cmf.cmf001.b.save.Cmf001bSaveCommandHandler;

@Path("screen/com/cmf/cmf001")
@Produces(MediaType.APPLICATION_JSON)
public class Cmf001bWebService {
	
	@Inject
	private GetLayout layout;
	
	@Inject
	private GetExternalImportSetting setting;
	
	@Inject
	private Cmf001bSaveCommandHandler saveCmd;
	
	@POST
	@Path("get/setting/all")
	public List<ExternalImportSettingListItemDto> getAll() {
		List<ExternalImportSettingListItemDto> result = setting.getAll();
		return result;
	}
	
	@POST
	@Path("get/setting/{settingCode}")
	public ExternalImportSettingDto get(@PathParam("settingCode") String settingCode) {
		ExternalImportSettingDto result = setting.get(settingCode);
		return result;
	}
	
	@POST
	@Path("get/layout")
	public List<Integer> getLayout(GetLayoutQuery query) {
		List<Integer> result = layout.get(query);
		return result;
	}
	
	@POST
	@Path("get/layout/detail")
	public List<ExternalImportLayoutDto> getLayoutDetail(GetLayoutQuery query) {
		List<ExternalImportLayoutDto> result = layout.getDetail(query);
		return result;
	}
	
	@POST
	@Path("save")
	public void save(Cmf001bSaveCommand command) {
		saveCmd.handle(command);
	}
}
