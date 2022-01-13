package nts.uk.ctx.at.request.ws.application.overtime;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.command.application.overtime.RegisterCommand;
import nts.uk.ctx.at.request.app.command.application.overtime.RegisterCommandHandler;
import nts.uk.ctx.at.request.app.command.application.overtime.RegisterCommandHandlerMultiple;
import nts.uk.ctx.at.request.app.command.application.overtime.UpdateCommand;
import nts.uk.ctx.at.request.app.command.application.overtime.UpdateCommandHandler;
import nts.uk.ctx.at.request.app.find.application.holidaywork.dto.CheckBeforeOutputMultiDto;
import nts.uk.ctx.at.request.app.find.application.overtime.*;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.CheckBeforeOutputDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.DetailOutputDto;
import nts.uk.ctx.at.request.app.find.application.overtime.dto.MultipleOvertimeContentDto;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;

import java.util.List;
import java.util.Map;

/**
 * Refactor5 
 * @author hoangnd
 *
 */
@Path("at/request/application/overtime")
@Produces("application/json")
public class OvertimeWebService extends WebService {
	
	@Inject
	private AppOvertimeFinder appOvertimeFinder;
	
	@Inject
	private RegisterCommandHandler registerCommandHandler;
	
	@Inject
	private RegisterCommandHandlerMultiple registerCommandHandlerMultiple;
	
	@Inject
	private UpdateCommandHandler updateCommandHandler;
	
	@POST
	@Path("start")
	public DisplayInfoOverTimeDto start(ParamOverTimeStart param) {
		return appOvertimeFinder.start(param);
	}

	@POST
    @Path("latestMultiApp")
    public MultiOvertimWithWorkDayOffDto getLatestMultipleOvertimeApp(ParamOverTimeChangeDate param) {
	    return appOvertimeFinder.getLatestMultipleOvertimeApp(param.employeeId, GeneralDate.fromString(param.dateOp, "yyyy/MM/dd"), param.prePost);
    }
	
	@POST
	@Path("breakTimes")
	public BreakTimeZoneSettingDto getBreakTime(ParamBreakTime param) {
		return appOvertimeFinder.getBreakTime(param);
	}
	
	@POST
	@Path("changeDate")
	public DisplayInfoOverTimeDto changeDate(ParamOverTimeChangeDate param) {
		return appOvertimeFinder.changeDate(param);
	}
	
	@POST
	@Path("selectWorkInfo")
	public DisplayInfoOverTimeDto selectWorkInfo(ParamSelectWork param) {
		return appOvertimeFinder.selectWorkInfo(param);
	}
	
	@POST
	@Path("calculate")
	public DisplayInfoOverTimeDto calculate(ParamCalculation param) {
		return appOvertimeFinder.calculate(param);
	}
	
	@POST
	@Path("checkBeforeRegister")
	public CheckBeforeOutputDto checkBeforeRegister(ParamCheckBeforeRegister param) {
		return appOvertimeFinder.checkBeforeRegister(param);
	}
	@POST
	@Path("checkBeforeRegisterMultiple")
	public CheckBeforeOutputMultiDto checkErrorRegisterMultiple(ParamCheckBeforeRegister param) {
		return appOvertimeFinder.checkErrorRegisterMultiple(param);
	}
	
	@POST
	@Path("register")
	public ProcessResult register(RegisterCommand command) {
		return registerCommandHandler.handle(command);
	}
	
	@POST
	@Path("registerMultiple")
	public ProcessResult registerMultiple(RegisterCommand command) {
		return registerCommandHandlerMultiple.handle(command);
	}
	
	@POST
	@Path("getDetail")
	public DetailOutputDto getDetail(ParamDetail param) {
		return appOvertimeFinder.getDetail(param);
	}
	
	@POST
	@Path("checkBeforeUpdate")
	public CheckBeforeOutputDto checkBeforeUpdate(ParamCheckBeforeUpdate param) {
		return appOvertimeFinder.checkBeforeUpdate(param);
	}
	
	@POST
	@Path("update")
	public ProcessResult update(UpdateCommand command) {
		return updateCommandHandler.handle(command);
	}
	
}
