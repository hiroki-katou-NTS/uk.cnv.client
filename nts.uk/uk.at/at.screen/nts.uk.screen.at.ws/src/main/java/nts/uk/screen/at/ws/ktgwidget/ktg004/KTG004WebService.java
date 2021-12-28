package nts.uk.screen.at.ws.ktgwidget.ktg004;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.screen.at.app.command.ktg.ktg004.WorkStatusSettingCommandHandler;
import nts.uk.screen.at.app.ktgwidget.ktg004.AcquisitionResultOfWorkStatusOutput;
import nts.uk.screen.at.app.ktgwidget.ktg004.AttendanceInforDto;
import nts.uk.screen.at.app.ktgwidget.ktg004.GetAttendanceInforParam;
import nts.uk.screen.at.app.ktgwidget.ktg004.KTG004DisplayYearMonthParam;
import nts.uk.screen.at.app.ktgwidget.ktg004.KTG004Finder;
import nts.uk.screen.at.app.ktgwidget.ktg004.WorkStatusSettingDto;
import nts.uk.shr.com.context.AppContexts;

@Path("screen/at/ktg004")
@Produces("application/json")
public class KTG004WebService {

	@Inject
	private KTG004Finder ktg004Finder;
	
	@Inject 
	private WorkStatusSettingCommandHandler commandHandler;
	
	@POST
	@Path("getSetting")
	public WorkStatusSettingDto checkDisplay() {
		return ktg004Finder.getApprovedDataWidgetStart();
	}
	
	@POST
	@Path("save")
	public void updateSetting(WorkStatusSettingDto param) {
		this.commandHandler.updateSetting(param);
	}
	
	@POST
	@Path("getData")
	public AcquisitionResultOfWorkStatusOutput getData(KTG004DisplayYearMonthParam param) {
		return ktg004Finder.startWorkStatus(
				AppContexts.user().companyId(), 
				AppContexts.user().employeeId(),
				param);
	}
	
	@POST
	@Path("getAttendanceInfor")
	public AttendanceInforDto getAttendanceInfor(GetAttendanceInforParam param) {
		return this.ktg004Finder.getWorkStatusData(AppContexts.user().companyId(), 
				AppContexts.user().employeeId(), param.getItemsSetting(), param.getCurrentClosingPeriod());
	}
}