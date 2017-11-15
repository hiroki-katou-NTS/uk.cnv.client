package nts.uk.ctx.pereg.ws.reginfo.copysetting.setting;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import find.person.setting.init.category.SettingCtgDto;
import nts.uk.ctx.pereg.app.find.reginfo.copysetting.setting.EmpCopySettingFinder;

/**
 * @author sonnlb
 *
 */
@Path("reginfo/copysetting/setting")
@Produces("application/json")
public class EmpCopySettingWebService {

	@Inject
	private EmpCopySettingFinder finder;

	@POST
	@Path("getCopySetting")
	public List<SettingCtgDto> getEmpCopySetting() {
		return this.finder.getEmpCopySetting();
	}
}
