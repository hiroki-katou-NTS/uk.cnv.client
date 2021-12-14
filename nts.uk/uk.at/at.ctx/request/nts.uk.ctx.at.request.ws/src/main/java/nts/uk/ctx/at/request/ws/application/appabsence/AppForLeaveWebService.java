package nts.uk.ctx.at.request.ws.application.appabsence;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.request.app.command.application.appabsence.CreatAppAbsenceCommand;
import nts.uk.ctx.at.request.app.command.application.appabsence.CreatAppAbsenceCommandHandler;
import nts.uk.ctx.at.request.app.command.application.appabsence.RegisterAppAbsenceCommand;
import nts.uk.ctx.at.request.app.command.application.appabsence.RegisterHolDatesCommandHandler;
import nts.uk.ctx.at.request.app.command.application.appabsence.UpdateAppAbsenceCommand;
import nts.uk.ctx.at.request.app.command.application.appabsence.UpdateAppAbsenceCommandHandler;
import nts.uk.ctx.at.request.app.find.application.appabsence.AppAbsenceFinder;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.AbsenceCheckRegisterDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.AbsenceStartScreenBOutput;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.AppAbsenceStartInfoDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.AppForLeaveStartBCommand;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.ChangeHolidayDatesParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.ChangeRelationShipDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.ChangeWorkTimeParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.ChangeWorkTypeParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.CheckBeforeRegisterHolidayParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.CheckTyingManagementParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.DisplayAllScreenParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.ParamGetAllAppAbsence;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.RegisterHolidayDatesParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.SpecAbsenceParam;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoStartupDto;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.VacationCheckOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.shr.com.context.AppContexts;

@Path("at/request/application/appforleave")
@Produces("application/json")
public class AppForLeaveWebService extends WebService{
	@Inject
	private AppAbsenceFinder appForLeaveFinder;
	@Inject
	private CreatAppAbsenceCommandHandler creatAppAbsence;
	@Inject
	private UpdateAppAbsenceCommandHandler updateAppAbsence;
	@Inject
	private RegisterHolDatesCommandHandler registerHolDates;
	
	@POST
	@Path("getAppForLeaveStart")
	public AppAbsenceStartInfoDto getAppForLeaveStart(AppDispInfoStartupDto appDispInfoStartupDto) {
		return this.appForLeaveFinder.getAppForLeave(appDispInfoStartupDto);
	}
	@POST
	@Path("getAllAppForLeave")
	public AppAbsenceStartInfoDto getAppForLeaveAll(DisplayAllScreenParam param) {
		return this.appForLeaveFinder.getAllDisplay(param);
	}
	@POST
	@Path("findChangeAppdate")
	public AppAbsenceStartInfoDto findChangeAppdate(DisplayAllScreenParam param) {
		return this.appForLeaveFinder.getChangeAppDate(
				param.getCompanyID(),
				param.getStartInfo(),
				param.getAppDates(),
				param.getHolidayAppType(),
				param.getAppWithDate());
	}
//	@POST
//	@Path("getChangeAllDayHalfDay")
//	public AppAbsenceStartInfoDto getChangeAllDayHalfDay(ParamGetAllAppAbsence param) {
//		return this.appForLeaveFinder.getChangeByAllDayOrHalfDay(
//				param.getAppAbsenceStartInfoDto(),
//				param.isDisplayHalfDayValue(),
//				param.getAlldayHalfDay(),
//				param.getHolidayType());
//	}
//	@POST
//	@Path("getChangeAllDayHalfDayForDetail")
//	public AppAbsenceStartInfoDto getChangeByAllDayOrHalfDayForUIDetail(ParamGetAllAppAbsence param) {
//		return this.appForLeaveFinder.getChangeByAllDayOrHalfDayForUIDetail(param);
//	}
//	@POST
//	@Path("findChangeDisplayHalfDay")
//	public AppAbsenceStartInfoDto getChangeDisplayHalfDay(ParamGetAllAppAbsence param) {
//		return this.appForLeaveFinder.getChangeDisplayHalfDay(
//				param.getStartAppDate(),
//				param.isDisplayHalfDayValue(),
//				param.getEmployeeID(),
//				param.getWorkTypeCode(),
//				param.getHolidayType(),
//				param.getAlldayHalfDay(),
//				param.getAppAbsenceStartInfoDto());
//	}
	@POST
	@Path("findChangeWorkType")
	public AppAbsenceStartInfoDto getChangeWorkType(ChangeWorkTypeParam param) {
		return this.appForLeaveFinder.getChangeWorkType(param);
	}
	
	@POST
	@Path("findChangeUsingWorkTime")
	public AppAbsenceStartInfoDto getChangeWorkTime(ChangeWorkTimeParam param) {
	    return this.appForLeaveFinder.getChangeWorkTime(param);
	}
	
	
	@POST
	@Path("getListWorkTime")
	public List<String> getListWorkTime(ParamGetAllAppAbsence param) {
//		return this.appForLeaveFinder.getListWorkTimeCodes(param.getStartAppDate(),param.getEmployeeID());
	    return null;
	}
	@POST
	@Path("findChangeWorkTime")
	public AppAbsenceStartInfoDto getWorkingHours(ParamGetAllAppAbsence param) {
		return this.appForLeaveFinder.getWorkingHours(param.getDate(), param.getWorkTimeCode(),param.getWorkTypeCode(), param.getAppAbsenceStartInfoDto());
	}
	@POST
	@Path("insert")
	public ProcessResult insert(RegisterAppAbsenceCommand param) {
		return creatAppAbsence.handle(param);
	}
//	@POST
//	@Path("getByAppID")
//	public AppAbsenceDetailDto getByAppID(String appID) {
//		return this.appForLeaveFinder.getByAppID(appID);
//	}
	
	@POST
	@Path("update")
	public ProcessResult update(UpdateAppAbsenceCommand command) {
		return this.updateAppAbsence.handle(command);
	}
	
	@POST
	@Path("changeRela")
	public ChangeRelationShipDto changeRelationShip(SpecAbsenceParam specAbsenceParam){
		return appForLeaveFinder.changeRelationShip(specAbsenceParam);
	}
	
	@POST
	@Path("checkBeforeRegister")
	public AbsenceCheckRegisterDto checkBeforeRegister(CreatAppAbsenceCommand param){
		return appForLeaveFinder.checkBeforeRegister(param);
	}
	
	@POST
	@Path("checkBeforeUpdate")
	public AbsenceCheckRegisterDto checkBeforeUpdate(CreatAppAbsenceCommand param){
		return appForLeaveFinder.checkBeforeUpdate(param);
	}
	
	@POST
	@Path("checkVacationTyingManage")
	public VacationCheckOutput checkVacationTyingManage(CheckTyingManagementParam checkTyingManageparam) {
	    return appForLeaveFinder.checkVacationTyingManage(checkTyingManageparam.getWtBefore(), checkTyingManageparam.getWtAfter(), checkTyingManageparam.getLeaveComDayOffMana(), checkTyingManageparam.getPayoutSubofHDManagements());
	}
	
	// ScreenB
	@POST
	@Path("getAppForLeaveStartB")
	public AbsenceStartScreenBOutput getAppForLeaveStartB(AppForLeaveStartBCommand param) {
	    String companyID = AppContexts.user().companyId();
	    
	    return appForLeaveFinder.getAppForLeaveStartB(companyID, param.getAppID(), param.getAppDispInfoStartupOutput());
	}
	
	@POST
	@Path("changeHolidayDates")
	public AppAbsenceStartInfoDto getChangeHolidayDates(ChangeHolidayDatesParam param) {
	    String companyID = AppContexts.user().companyId();
	    
	    return appForLeaveFinder.getChangeHolidayDates(companyID, param.getHolidayDates(), param.getAppAbsenceStartInfoDto());
	}
	
	@POST
	@Path("checkBeforeRegisterHolidayDates")
	public AbsenceCheckRegisterDto checkBeforeRegisterHolidayDates(CheckBeforeRegisterHolidayParam param) {
	    String companyID = AppContexts.user().companyId();
	    
	    return appForLeaveFinder.checkBeforeRegisterHolidayDates(companyID, param.getOldApplication(), param.getNewApplication(), param.getAppAbsenceStartInfoDto(), param.getOriginApplyForLeave(), param.getNewApplyForLeave());
	}
	
	@POST
	@Path("registerHolidayDates")
	public ProcessResult registerHolidayDates(RegisterHolidayDatesParam param) {
	    return registerHolDates.handle(param);
	}
}

