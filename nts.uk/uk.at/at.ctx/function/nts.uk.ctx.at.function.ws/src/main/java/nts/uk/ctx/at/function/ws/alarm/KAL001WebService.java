package nts.uk.ctx.at.function.ws.alarm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.app.command.JavaTypeResult;
import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.task.AsyncTaskInfo;
import nts.uk.ctx.at.function.app.command.alarm.alarmlist.ErrorAlarmListCommand;
import nts.uk.ctx.at.function.app.command.alarm.alarmlist.ErrorAlarmListExtractCommandHandler;
import nts.uk.ctx.at.function.app.command.alarm.extraprocessstatus.ActiveAlarmListExtraProcessCommand;
import nts.uk.ctx.at.function.app.command.alarm.extraprocessstatus.FinishAlarmListExtraProcessHandler;
import nts.uk.ctx.at.function.app.command.alarm.extraprocessstatus.StartAlarmListExtraProcessHandler;
import nts.uk.ctx.at.function.app.command.alarm.sendemail.ParamAlarmSendEmailCommand;
import nts.uk.ctx.at.function.app.command.alarm.sendemail.StartAlarmSendEmailProcessHandler;
import nts.uk.ctx.at.function.app.export.alarm.AlarmExportQuery;
import nts.uk.ctx.at.function.app.export.alarm.AlarmExportService;
import nts.uk.ctx.at.function.app.find.alarm.AlarmPatternSettingFinder;
import nts.uk.ctx.at.function.app.find.alarm.CheckConditionTimeFinder;
import nts.uk.ctx.at.function.app.find.alarm.CodeNameAlarmDto;
import nts.uk.ctx.at.function.app.find.alarm.alarmlist.*;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.extractresult.AlarmListExtractResult;
import nts.uk.ctx.at.function.dom.alarm.extractionrange.CheckConditionTimeDto;
import nts.uk.ctx.at.function.dom.alarm.extraprocessstatus.AlarmListExtraProcessStatusRepository;
import nts.uk.ctx.at.function.dom.alarm.extraprocessstatus.ExtractionState;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author dxthuong
 *
 * Web Service for function KAL001_アラームリスト
 */
@Path("at/function/alarm/kal/001")
@Produces("application/json")
public class KAL001WebService {

	@Inject
	private AlarmPatternSettingFinder alarmFinder;
	
	@Inject
	private CheckConditionTimeFinder checkConditionFinder;
	
	@Inject
	private ErrorAlarmListExtractCommandHandler extractAlarmHandler;
	
//	@Inject
//	private EmployeeInfoFunFinder employeeInfoFunFinder;
	
	@Inject
	private AlarmListExtraProcessStatusRepository alListExtraProcessStatusRepo;
	
	@Inject
	private StartAlarmListExtraProcessHandler startExtractHandler;
	
	@Inject
	private FinishAlarmListExtraProcessHandler finishExtractHandler;
	
	@Inject
	private StartAlarmSendEmailProcessHandler startSendEmailHandler;
	
	@Inject
	private AlarmExportService alarmExportService;
	
	@Inject
	private ErAlExtractResultFinder extractResultFinder;

	@Inject
	private ExtractAlarmListFinder extractAlarmListFinder;
	
	@POST
	@Path("pattern/setting")
	public List<CodeNameAlarmDto> getAlarmByUser(){
		return alarmFinder.getCodeNameAlarm();
	}
	
	@POST
	@Path("check/condition/time")
	public List<CheckConditionTimeDto> getCheckConditionTime(String alarmCode){
		return checkConditionFinder.getCheckConditionTime(alarmCode);
	}
	
	@POST
	@Path("processingym")
	public int getProcessingym(){
		return checkConditionFinder.getProcessingYm();
	}
	
	@POST
	@Path("extract/alarm")
	public AsyncTaskInfo extractAlarm(ErrorAlarmListCommand command) {
		return extractAlarmHandler.handle(command);
	}

	
	private static final int KAL001_LIMIT_LIVE_VIEW = 1000;
	
	@POST
	@Path("extract/result/{processId}")
	public ErAlExtractViewResult getResult(@PathParam("processId") String processId) {
		ErAlExtractViewResult rerult =  extractResultFinder.getResultLimitedBy(processId, KAL001_LIMIT_LIVE_VIEW);
		return rerult;
	}
	
	@POST
	@Path("isextracting")
	public JavaTypeResult<Boolean> isExtracting() {
		String companyID = AppContexts.user().companyId();
		String employeeId = AppContexts.user().employeeId();
		return new JavaTypeResult<Boolean>(alListExtraProcessStatusRepo.isAlListExtaProcessing(companyID, employeeId,ExtractionState.PROCESSING.value));
	}
	
	@POST
	@Path("extractstarting")
	public JavaTypeResult<String> extractStarting() {
		return new JavaTypeResult<String>(startExtractHandler.handle(new ActiveAlarmListExtraProcessCommand()));
	}
	
	@POST
	@Path("extractfinished")
	public void extractFinished(ActiveAlarmListExtraProcessCommand command) {
		finishExtractHandler.handle(command);
	}
	
	@POST
	@Path("get/employee/sendEmail/{processId}")
	public List<EmployeeSendEmail> extractAlarm(@PathParam("processId") String processId) {
		AlarmListExtractResult result = extractResultFinder.getResultEmpInfo(processId);
		return result.getEmpInfos().stream().map(c -> new EmployeeSendEmail(c.getWorkplaceId(), c.getWorkplaceName(), 
									c.getEmployeeId(), c.getEmployeeCode(), c.getEmployeeName()))
				.collect(Collectors.toList());
	}

	@POST
	@Path("send-email")
	public JavaTypeResult<String> sendEmailStarting(ParamAlarmSendEmailCommand command) {
		return new JavaTypeResult<String>(startSendEmailHandler.handle(command));
	}
	
	@POST
	@Path("export-alarm-data/{processId}/{currentAlarmCode}")
	public ExportServiceResult generate(@PathParam("processId") String processId, @PathParam("currentAlarmCode") String currentAlarmCode) {

		return this.alarmExportService.start(new AlarmExportQuery(extractResultFinder.getResultDto(processId), currentAlarmCode));
	}

	@POST
	@Path("alarmlist/webmenu")
	public List<ValueExtractAlarmDto> getAlarmListWebMenu(List<String> employeeIds) {
	    return extractAlarmListFinder.getWebMenu(employeeIds);
    }
}
