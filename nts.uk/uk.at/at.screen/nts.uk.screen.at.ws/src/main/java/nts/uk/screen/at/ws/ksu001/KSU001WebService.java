package nts.uk.screen.at.ws.ksu001;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.task.AsyncTaskInfo;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.monthly.root.common.DatePeriodDto;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExecutionInfor;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.ChangeConfirmedStateCommand;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.ChangeConfirmedStateCommandHandler;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.RegisterWorkSchedule;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.ResultRegisWorkSchedule;
import nts.uk.ctx.at.schedule.app.command.schedule.workschedule.WorkScheduleCommand;
import nts.uk.ctx.at.shared.dom.WorkInfoAndTimeZone;
import nts.uk.screen.at.app.ksu001.changepage.ChangePageParam;
import nts.uk.screen.at.app.ksu001.changepage.GetDataWhenChangePage;
import nts.uk.screen.at.app.ksu001.changepage.GetShiftPalChangePageResult;
import nts.uk.screen.at.app.ksu001.changeworkplace.ChangeWorkPlaceFinder;
import nts.uk.screen.at.app.ksu001.changeworkplace.ChangeWorkPlaceParam;
import nts.uk.screen.at.app.ksu001.eventinformationandpersonal.DateInformationDto;
import nts.uk.screen.at.app.ksu001.get28dateperiod.ModePeriodParam;
import nts.uk.screen.at.app.ksu001.get28dateperiod.ScreenQuery28DayPeriod;
import nts.uk.screen.at.app.ksu001.getaggregatedInfo.AggregatedInfoParam;
import nts.uk.screen.at.app.ksu001.getaggregatedInfo.AggregatedInformationRs;
import nts.uk.screen.at.app.ksu001.getaggregatedInfo.GetAggregatedInfoFinder;
import nts.uk.screen.at.app.ksu001.getevent.EventFinder;
import nts.uk.screen.at.app.ksu001.getevent.EventFinderParam;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangeMonthDto;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangeMonthFinder;
import nts.uk.screen.at.app.ksu001.getsendingperiod.ChangePeriodModeFinder;
import nts.uk.screen.at.app.ksu001.getshiftpalette.GetShiftPalette;
import nts.uk.screen.at.app.ksu001.getshiftpalette.GetShiftPaletteParam;
import nts.uk.screen.at.app.ksu001.getshiftpalette.GetShiftPaletteResult;
import nts.uk.screen.at.app.ksu001.orderemployee.DataAfterSortEmpDto;
import nts.uk.screen.at.app.ksu001.orderemployee.GetDataAfterSortEmp;
import nts.uk.screen.at.app.ksu001.processcommon.CorrectWorkTimeHalfDay;
import nts.uk.screen.at.app.ksu001.processcommon.CorrectWorkTimeHalfDayParam;
import nts.uk.screen.at.app.ksu001.processcommon.CorrectWorkTimeHalfDayRs;
import nts.uk.screen.at.app.ksu001.start.ChangeMonthParam;
import nts.uk.screen.at.app.ksu001.start.ChangePeriodModeParam;
import nts.uk.screen.at.app.ksu001.start.OrderEmployeeParam;
import nts.uk.screen.at.app.ksu001.start.StartKSU001Dto;
import nts.uk.screen.at.app.ksu001.start.StartKSU001Param;
import nts.uk.screen.at.app.ksu001.start.StartKSU001Ver5;
import nts.uk.screen.at.app.ksu001.validwhenedittime.ValidDataWhenEditTime;
import nts.uk.screen.at.app.ksu001.validwhenedittime.ValidDataWhenEditTimeParam;
import nts.uk.screen.at.app.ksu001.validwhenpaste.ValidDataWhenPaste;
import nts.uk.screen.at.app.ksu001.validwhenpaste.ValidDataWhenPasteParam;

/**
 * 
 * @author laitv
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Path("screen/at/schedule")
@Produces("application/json")
public class KSU001WebService extends WebService{

	@Inject
	private StartKSU001Ver5 startKSU001Ver5;
	@Inject
	private ChangeMonthFinder changeMonthFinder;
	@Inject
	private ChangePeriodModeFinder changePeriodModeFinder;
	@Inject
	private GetShiftPalette getShiftPalette;
	@Inject
	private GetDataWhenChangePage getDataWhenChangePage;
	@Inject
	private GetDataAfterSortEmp sortEmployees;
	@Inject
	private ValidDataWhenPaste valid;
	@Inject
	private ChangeWorkPlaceFinder changeWorkPlaceFinder;
	@Inject
	private ValidDataWhenEditTime validTime;
	@Inject
	private EventFinder eventFinder;
	@Inject
	private RegisterWorkSchedule regWorkSchedule;
	@Inject
	private CorrectWorkTimeHalfDay correctWorkTimeHalfDay;
	@Inject
	private ChangeConfirmedStateCommandHandler changeConfirmedStateHandler;
	@Inject
	private GetAggregatedInfoFinder getdataA11A12;
	@Inject
	private ScreenQuery28DayPeriod mode28DayPeriod;
	
	@POST
	@Path("start")
	public StartKSU001Dto getDataStartScreenVer5(StartKSU001Param param){
		StartKSU001Dto data = startKSU001Ver5.getData(param);
		return data;
	}
	
	@POST
	@Path("shift")
	public StartKSU001Dto getDataShiftMode(StartKSU001Param param){
		StartKSU001Dto data = startKSU001Ver5.getData(param);
		return data;
	}
	
	@POST
	@Path("shortname")
	public StartKSU001Dto getDataShortNameMode(StartKSU001Param param){
		StartKSU001Dto data = startKSU001Ver5.getData(param);
		return data;
	}
	
	@POST
	@Path("time")
	public StartKSU001Dto getDataTimeMode(StartKSU001Param param){
		StartKSU001Dto data = startKSU001Ver5.getData(param);
		return data;
	}
	
	@POST
	@Path("change-month")
	public ChangeMonthDto getDataNextMonth(ChangeMonthParam param){
		ChangeMonthDto data = changeMonthFinder.getData(param);
		return data;
	}
	
	@POST
	@Path("change-mode-period")
	public ChangeMonthDto getDataWhenChangeModePeriod(ChangePeriodModeParam param){
		ChangeMonthDto data = changePeriodModeFinder.getData(param);
		return data;
	}

	@POST
	@Path("getShiftPallets") // get cho cả 2 trường hợp company và workPlace 
	public GetShiftPaletteResult getShiftPallets(GetShiftPaletteParam param) {
		return getShiftPalette.getDataShiftPallet(param);
	}
	
	@POST
	@Path("change-page") // get cho cả 2 trường hợp company và workPlace 
	public GetShiftPalChangePageResult getShiftPallet(ChangePageParam param) {
		return getDataWhenChangePage.gatData(param);
	}

	@POST
	@Path("order-employee")
	public DataAfterSortEmpDto orderEmployee(OrderEmployeeParam param) {
		return sortEmployees.getData(param);
	}
	
	@POST
	@Path("valid-when-paste")
	public boolean validWhenPaste(List<ValidDataWhenPasteParam> shiftmasters) {
		return valid.valid(shiftmasters);
	}
	
	@POST
	@Path("valid-when-edit-time")
	public boolean validWhenEditTime(ValidDataWhenEditTimeParam param) {
		return validTime.valid(param);
	}
	
	@POST
	@Path("change-workplace") 
	public StartKSU001Dto getDataWhenChangeWkp(ChangeWorkPlaceParam param) {
		StartKSU001Dto data = changeWorkPlaceFinder.getData(param);
		return data;
	}
	
	@POST
	@Path("get-event") 
	public List<DateInformationDto> getEvent(EventFinderParam param) {
		List<DateInformationDto> data = eventFinder.getEvent(param);
		return data;
	}
	
	@POST
	@Path("reg-workschedule")
	public ExecutionInfor regWorkSchedule(List<WorkScheduleCommand> param) {
		ExecutionInfor executionInfor = regWorkSchedule.handle(param);
		return executionInfor;
	}
	
	@POST
	@Path("correct-worktime-halfday") 
	public CorrectWorkTimeHalfDayRs correctWorkTimeofHalfday(CorrectWorkTimeHalfDayParam param) {
		WorkInfoAndTimeZone rs = correctWorkTimeHalfDay.handle(param);
		if(rs == null || rs.getTimeZones().isEmpty()) {
			return new CorrectWorkTimeHalfDayRs(null, null);
		}
		
		Integer strTime = rs.getTimeZones().get(0).getStart() == null ? null : rs.getTimeZones().get(0).getStart().v();
		Integer endTime = rs.getTimeZones().get(0).getEnd()   == null ? null : rs.getTimeZones().get(0).getEnd().v();
		return new CorrectWorkTimeHalfDayRs(strTime, endTime);
	}
	
	@POST
	@Path("change-confirmed-state")
	public void changeConfirmedStateHandler(List<ChangeConfirmedStateCommand> param) {
		changeConfirmedStateHandler.handle(param);
	}
	
	@POST
	@Path("get-aggregated-info") // get lai data A11, A12 
	public AggregatedInformationRs getAggregatedInfo(AggregatedInfoParam param){
		AggregatedInformationRs data = getdataA11A12.getData(param);
		return data;
	}
	
	@POST
	@Path("get-28day-period")
	public DatePeriodDto get28DaysPeriod(ModePeriodParam param){
		GeneralDate endDate = GeneralDate.fromString(param.endDate, "yyyy/MM/dd");
		DatePeriodDto data = mode28DayPeriod.get(endDate, param.isNextMonth);
		return data;
	}
	
	@POST
	@Path("change-mode")
	public void changeMode(){}
}
