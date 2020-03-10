package nts.uk.ctx.at.request.ws.application.holidaywork;
/*import nts.uk.ctx.at.shared.dom.employmentrules.employmenttimezone.BreakTimeZoneSharedOutPut;*/
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.Value;
import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDateTime;
import nts.arc.web.session.HttpSubSession;
import nts.uk.ctx.at.request.app.command.application.holidaywork.CheckBeforeRegisterHolidayWork;
import nts.uk.ctx.at.request.app.command.application.holidaywork.CreateHolidayWorkCommand;
import nts.uk.ctx.at.request.app.command.application.holidaywork.CreateHolidayWorkCommandHandler;
import nts.uk.ctx.at.request.app.command.application.holidaywork.UpdateHolidayWorkCommand;
import nts.uk.ctx.at.request.app.command.application.holidaywork.UpdateHolidayWorkCommandHandler;
import nts.uk.ctx.at.request.app.find.application.holidaywork.AppHolidayWorkFinder;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.AppHolidayWorkDataNoDate;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.AppHolidayWorkDto;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamCalculationHolidayWork;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.ParamGetHolidayWork;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.RecordWorkParamHoliday;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.OvertimeCheckResultDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.ParamCalculateOvertime;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.ParamChangeAppDate;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.RecordWorkDto;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.CommonOvertimeHoliday;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.PreActualColorResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.ColorConfirmResult;
import nts.uk.ctx.at.request.dom.application.overtime.service.CaculationTime;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeRestAppCommonSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeRestAppCommonSetting;
import nts.uk.ctx.at.shared.app.find.worktime.common.dto.DeductionTimeDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.TimeWithDayAttr;


@Path("at/request/application/holidaywork")
@Produces("application/json")
public class HolidayWorkWebService extends WebService{
	@Inject
	private AppHolidayWorkFinder appHolidayWorkFinder;
	@Inject
	private CheckBeforeRegisterHolidayWork checkBeforeRegisterHolidayWork;
	@Inject
	private CreateHolidayWorkCommandHandler createHolidayWorkCommandHandler;
	@Inject
	private UpdateHolidayWorkCommandHandler updateHolidayWorkCommandHandle;
	
	@Inject
	private OvertimeRestAppCommonSetRepository overTimeSetRepo;
	
	@Inject
	private CommonOvertimeHoliday commonOvertimeHoliday;
	
	@Inject
	private HttpSubSession session;
	
	@POST
	@Path("getHolidayWorkByUI")
	public AppHolidayWorkDto getOvertimeByUIType(ParamGetHolidayWork param) {
		AppHolidayWorkDto appHolidayWorkDto = this.appHolidayWorkFinder.getAppHolidayWork(param.getAppDate(), param.getUiType(),param.getLstEmployee(),param.getPayoutType(),param.getEmployeeID(),new AppHolidayWorkDto());
		session.setAttribute("appHolidayWorkDataNoDate", appHolidayWorkDto.getAppHolidayWorkDataNoDate());
		return appHolidayWorkDto;
	}
	@POST
	@Path("findChangeAppDate")
	public AppHolidayWorkDto findChangeAppDate(ParamChangeAppDate param) {
		AppHolidayWorkDataNoDate appHolidayWorkDataNoDate = (AppHolidayWorkDataNoDate) session.getAttribute("appHolidayWorkDataNoDate");
		return this.appHolidayWorkFinder.findChangeAppDate(param.getAppDate(), param.getPrePostAtr(),param.getSiftCD(),param.getOvertimeHours(),param.getChangeEmployee(),
				param.getStartTime(), param.getEndTime(), appHolidayWorkDataNoDate);
	}
	@POST
	@Path("calculationresultConfirm")
	public ColorConfirmResult calculationresultConfirm(ParamCalculationHolidayWork param){
		return this.appHolidayWorkFinder.calculationresultConfirm(param.getBreakTimes(),
															param.getPrePostAtr(),
															param.getAppDate(),
															param.getSiftCD(),
															param.getWorkTypeCode(),
															param.getEmployeeID(),
															param.getInputDate() == null ? null :GeneralDateTime.fromString(param.getInputDate(), "yyyy/MM/dd HH:mm"),
															param.getStartTime(),
															param.getEndTime(),
															param.getStartTimeRests(),
															param.getEndTimeRests());
	}
	@POST
	@Path("getcalculationresult")
	public List<CaculationTime> getCalculationTime(ParamCalculationHolidayWork param){
		return this.appHolidayWorkFinder.getCaculationValue(param.getBreakTimes(),
															param.getPrePostAtr(),
															param.getAppDate(),
															param.getSiftCD(),
															param.getWorkTypeCode(),
															param.getEmployeeID(),
															param.getInputDate() == null ? null :GeneralDateTime.fromString(param.getInputDate(), "yyyy/MM/dd HH:mm"),
															param.getStartTime(),
															param.getEndTime(),
															param.getStartTimeRests(),
															param.getEndTimeRests(),
															param.getDailyAttendanceTimeCaculationImport());
	}
	
	@POST
	@Path("getCalculateValue")
	public PreActualColorResult getCalculateValue(ParamCalculateOvertime param) {
		return appHolidayWorkFinder.getCalculateValue(
				param.employeeID, 
				param.appDate, 
				param.prePostAtr, 
				param.workTypeCD, 
				param.workTimeCD, 
				param.overtimeInputLst, 
				param.startTime, 
				param.endTime, 
				param.getStartTimeRests(), 
				param.getEndTimeRests());
	}
	
	@POST
	@Path("create")
	public ProcessResult createHolidayWork(CreateHolidayWorkCommand command){
		return createHolidayWorkCommandHandler.handle(command);
	}
	@POST
	@Path("beforeRegisterColorConfirm")
	public ColorConfirmResult beforeRegisterColorConfirm(CreateHolidayWorkCommand command){
		return checkBeforeRegisterHolidayWork.checkBeforeRregisterColor(command);
	}
	@POST
	@Path("checkBeforeRegister")
	public OvertimeCheckResultDto checkBeforeRegister(CreateHolidayWorkCommand command){
		return checkBeforeRegisterHolidayWork.CheckBeforeRegister(command);
	}
	@POST
	@Path("findByAppID")
	public AppHolidayWorkDto findByChangeAppID(String appID) {
		return this.appHolidayWorkFinder.getAppHolidayWorkByAppID(appID);
	}
	@POST
	@Path("beforeUpdateColorConfirm")
	public ColorConfirmResult beforeUpdateColorConfirm(CreateHolidayWorkCommand command){
		return checkBeforeRegisterHolidayWork.checkBeforeUpdateColor(command);
	}
	@POST
	@Path("checkBeforeUpdate")
	public OvertimeCheckResultDto checkBeforeUpdate(CreateHolidayWorkCommand command){
		return checkBeforeRegisterHolidayWork.checkBeforeUpdate(command);
	}
	@POST
	@Path("update")
	public ProcessResult updateHolidayWork(UpdateHolidayWorkCommand command){
		return updateHolidayWorkCommandHandle.handle(command);
	}
	@POST
	@Path("getRecordWork")
	public RecordWorkDto getRecordWork(RecordWorkParamHoliday param) {
		return this.appHolidayWorkFinder.getRecordWork(param.employeeID, param.appDate, param.siftCD,param.prePostAtr,param.getBreakTimeHours(), param.getWorkTypeCD());
	}
	
	@POST
	@Path("getBreakTimes")
	public List<DeductionTimeDto> getBreakTimes(GetBreakTimeParam param) {
		Optional<TimeWithDayAttr> opStartTime = param.getStartTime()==null ? Optional.empty() : Optional.of(new TimeWithDayAttr(param.getStartTime())); 
		Optional<TimeWithDayAttr> opEndTime = param.getEndTime()==null ? Optional.empty() : Optional.of(new TimeWithDayAttr(param.getEndTime()));
		return this.appHolidayWorkFinder.getBreakTimes(param.getWorkTypeCD(), param.getWorkTimeCD(), opStartTime, opEndTime);
	}
	
	@POST
	@Path("confirmInconsistency")
	public List<String> confirmInconsistency(CreateHolidayWorkCommand command) {
		String companyID = AppContexts.user().companyId();
		Optional<OvertimeRestAppCommonSetting>  overTimeSettingOpt = overTimeSetRepo.getOvertimeRestAppCommonSetting(companyID, ApplicationType.BREAK_TIME_APPLICATION.value);
		return commonOvertimeHoliday.inconsistencyCheck(
				companyID, 
				command.getApplicantSID(), 
				command.getApplicationDate(),
				ApplicationType.BREAK_TIME_APPLICATION,
				overTimeSettingOpt.get().getAppDateContradictionAtr());
	}
	
}

@Value
class GetBreakTimeParam {
	String workTypeCD;
	String workTimeCD;
	Integer startTime;
	Integer endTime;
}
