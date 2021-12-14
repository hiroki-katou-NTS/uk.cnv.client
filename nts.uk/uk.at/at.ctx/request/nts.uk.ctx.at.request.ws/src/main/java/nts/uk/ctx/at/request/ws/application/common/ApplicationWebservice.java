package nts.uk.ctx.at.request.ws.application.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.file.export.ExportServiceResult;
import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.command.application.applicationlist.AppListApproveResult;
import nts.uk.ctx.at.request.app.command.application.common.AppDetailBehaviorCmd;
import nts.uk.ctx.at.request.app.command.application.common.AppNewBehaviorCmd;
import nts.uk.ctx.at.request.app.command.application.common.ApproveAppHandler;
import nts.uk.ctx.at.request.app.command.application.common.CancelAppHandler;
import nts.uk.ctx.at.request.app.command.application.common.DeleteAppHandler;
import nts.uk.ctx.at.request.app.command.application.common.DenyAppHandler;
import nts.uk.ctx.at.request.app.command.application.common.ReflectAplicationCommmandHandler;
import nts.uk.ctx.at.request.app.command.application.common.ReleaseAppHandler;
import nts.uk.ctx.at.request.app.command.application.common.RemandApplicationHandler;
import nts.uk.ctx.at.request.app.find.application.common.AppDataDateFinder;
import nts.uk.ctx.at.request.app.find.application.common.AppDateDataDto;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoStartupDto;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoWithDateDto;
import nts.uk.ctx.at.request.app.find.application.common.AppPrintQuery;
import nts.uk.ctx.at.request.app.find.application.common.ApplicationExportService;
import nts.uk.ctx.at.request.app.find.application.common.ApplicationFinder;
import nts.uk.ctx.at.request.app.find.application.common.GetDataCheckDetail;
import nts.uk.ctx.at.request.app.find.application.common.OutputDetailCheckDto;
import nts.uk.ctx.at.request.app.find.application.common.dto.AppDateParamCommon;
import nts.uk.ctx.at.request.app.find.application.common.dto.ApplicationMetaDto;
import nts.uk.ctx.at.request.app.find.application.common.dto.ApplicationSendDto;
import nts.uk.ctx.at.request.app.find.application.common.dto.ClosureParam;
import nts.uk.ctx.at.request.app.find.application.requestofearch.GetDataAppCfDetailFinder;
import nts.uk.ctx.at.request.app.find.setting.request.application.ApplicationDeadlineDto;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.common.service.application.SendMailDialogParam;
import nts.uk.ctx.at.request.dom.application.common.service.application.output.ApplicationForRemandOutput;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.InputGetDetailCheck;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.RemandCommand;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ApproveProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.ScreenIdentifier;
import nts.uk.shr.infra.web.util.StartPageLogService;

@Path("at/request/application")
@Produces("application/json")
public class ApplicationWebservice extends WebService {
	
	@Inject 
	private GetDataAppCfDetailFinder getDataAppCfDetailFinder;
	
	@Inject
	private GetDataCheckDetail getDataCheckDetail; 
	
	@Inject
	private RemandApplicationHandler remandApplicationHandler;

	@Inject
	private AppDataDateFinder appDataDateFinder;
//	@Inject
//	private UpdateApplicationDeadlineCommandHandler update;

	@Inject
	private ReflectAplicationCommmandHandler relect;
	@Inject
	private StartPageLogService writeLogSv;
	
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	// refactor 4
	
	@Inject 
	private ApplicationFinder finderApp;
	
	@Inject
	private DeleteAppHandler deleteApp;
	
	@Inject
	private ApproveAppHandler approveApp;
	
	@Inject
	private ReleaseAppHandler releaseApp;
	
	@Inject
	private DenyAppHandler denyApp;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private CancelAppHandler cancelApp;
	
	@Inject
	private ApplicationExportService applicationExportService;
	
	/**
	 * remand application
	 * @return
	 */
	@POST
	@Path("remandapp")
	public ProcessResult remandApp(RemandCommand command){
		return remandApplicationHandler.handle(command);
	}
	
	/**
	 * lấy message và deadline trên màn hình
	 * get message and deadline (getDataConfigDetail)
	 * @return
	 */
	/*@POST
	@Path("getmessagedeadline")
	public OutputMessageDeadline getDataConfigDetail(ApplicationMetaDto application) {
		return this.getDataAppCfDetailFinder.getDataConfigDetail(application);
	}*/
	//new InputMessageDeadline("000000000000-0005",null,1,null)
	
	/**
	 * get getDetailedScreenPreBootMode (check)
	 * @return
	 */
	@POST
	@Path("getdetailcheck")
	public OutputDetailCheckDto getDetailCheck(InputGetDetailCheck inputGetDetailCheck){
		
		return this.getDataCheckDetail.getDataCheckDetail(inputGetDetailCheck);
	}
	
	
	
	@POST
	@Path("getAppInfoByAppID")
	public ApplicationMetaDto getAppInfo(String appID){
		return this.finderApp.getAppByID(appID);
	}
	
	@POST
	@Path("getAppInfoByListAppID")
	public List<ApplicationMetaDto> getListAppInfo(List<String> listAppID){
		return this.finderApp.getListAppInfo(listAppID);
	}
	
	
	@POST
	@Path("getAppInfoForRemandByAppId")
	public ApplicationForRemandOutput getAppInfoByAppIdForRemand(List<String> appID){
		return this.finderApp.getAppByIdForRemand(appID);
	}
	@POST
	@Path("getApplicationForSendByAppID")
	public ApplicationSendDto getApplicationForSendByAppID(SendMailDialogParam param){
		return finderApp.getAppByIdForSend(param);
	}
	
	@POST
	@Path("getAppDataByDate")
	public AppDateDataDto getAppDataByDate(AppDateParamCommon param){
		int overtimeAtr = 2;
		String overtimeAtrParam = param.getOvertimeAtrParam();
		if(overtimeAtrParam!=null){
			if(overtimeAtrParam.equals("0")){
				overtimeAtr = 0;
			}
			if(overtimeAtrParam.equals("1")){
				overtimeAtr = 1;
			}
		}
		return appDataDateFinder.getAppDataByDate(
				param.getAppTypeValue(), 
				param.getAppDate(), 
				param.getIsStartup(), 
				param.getAppID(),
				param.getEmployeeID(),
				overtimeAtr);
	}
	
	@POST
	@Path("getDetailRealData")
	public AchievementOutput getDetailRealData(String appID){
		return finderApp.getDetailRealData(appID);
	}
	
	/**
	 * @author yennth
	 * @param closureId
	 * @return
	 */
	@POST
    @Path("getalldatabyclosureId")
    public List<ApplicationDeadlineDto> getDeadlineByClosureId(ClosureParam closureId){
        return this.getDataAppCfDetailFinder.findByClosureId(closureId.getClosureId());
    }
	/**
	 * update application deadline
	 * @param command
	 * @author yennth
	 */
//	@POST
//	@Path("update")
//	public void update(List<ApplicationDeadlineCommand> command){
//		this.update.handle(command);
//	}
	
	@POST
	@Path("reflect-app")
	public void reflectApp(List<String> command){
		relect.handle(command);
	}
	@POST
	@Path("write-log")
	public void writeLog(ParamWriteLog paramLog){
		writeLogSv.writeLog(new ScreenIdentifier(paramLog.getProgramId(), paramLog.getScreenId(), paramLog.getQueryString()));
	}
	
	// refactor 4
	
	@POST
	@Path("getStartPC")
	public AppDispInfoStartupDto getStartPC(StartupParam param) {
		String companyID = AppContexts.user().companyId();
		List<GeneralDate> dateLst = param.getDateLst().stream().map(x -> {
										if(Strings.isBlank(x)) {
											return null;
										} else {
											return GeneralDate.fromString(x, "yyyy/MM/dd");
										}
									}).collect(Collectors.toList());
		AppDispInfoStartupOutput appDispInfoStartupOutput = commonAlgorithm.getAppDispInfoStart(
				companyID, 
				EnumAdaptor.valueOf(param.appType, ApplicationType.class), 
				param.empLst, 
				dateLst, 
				true,
				param.getOpHolidayAppType() == null ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(param.getOpHolidayAppType(), HolidayAppType.class)),
				param.getOpOvertimeAppAtr() == null ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(param.getOpOvertimeAppAtr(), OvertimeAppAtr.class)));
		return AppDispInfoStartupDto.fromDomain(appDispInfoStartupOutput);
	}
	
	@POST
	@Path("getDetailPC/{appID}")
	public AppDispInfoStartupDto getDetailPC(@PathParam("appID") String appID) {
		return finderApp.getDetailPC(appID);
	}
	
	@POST
	@Path("changeAppDate")
	public AppDispInfoWithDateDto changeAppDate(ChangeDateParam param) {
		String companyID = AppContexts.user().companyId();

		List<GeneralDate> dateLst = new ArrayList<>();
		if(Strings.isNotBlank(param.getStartDate()) && Strings.isNotBlank(param.getEndDate())) {
			GeneralDate startDate = GeneralDate.fromString(param.getStartDate(), "yyyy/MM/dd");
			GeneralDate endDate = GeneralDate.fromString(param.getEndDate(), "yyyy/MM/dd");
			dateLst = new DatePeriod(startDate, endDate).datesBetween();
		} else {
			dateLst.add(GeneralDate.fromString(param.getAppDate(), "yyyy/MM/dd"));
		}
		
		// TODO: 申請設定 domain has changed!
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithm.changeAppDateProcess(
				companyID, 
				dateLst, 
				param.getAppDispInfoStartupOutput().toDomain().getAppDispInfoNoDateOutput().getApplicationSetting().getAppTypeSettings().get(0).getAppType(),
				param.getAppDispInfoStartupOutput().toDomain().getAppDispInfoNoDateOutput(), 
				param.getAppDispInfoStartupOutput().toDomain().getAppDispInfoWithDateOutput(),
				param.getOpOvertimeAppAtr() == null ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(param.getOpOvertimeAppAtr(), OvertimeAppAtr.class)));
		return AppDispInfoWithDateDto.fromDomain(appDispInfoWithDateOutput);
	}
	
	@POST
	@Path("deleteapp")
	public ProcessResult deleteApp(AppDetailBehaviorCmd command){
		return deleteApp.handle(command);
	}
	
	@POST
	@Path("approveapp")
	public ApproveProcessResult approveApp(AppDetailBehaviorCmd command){
		return approveApp.handle(command);
	}
	
	@POST
	@Path("releaseapp")
	public ProcessResult releaseApp(AppDispInfoStartupDto command){
		return releaseApp.handle(command);
	}
	
	@POST
	@Path("denyapp")
	public ProcessResult denyApp(AppDetailBehaviorCmd command){
		return denyApp.handle(command);
	}
	
	@POST
	@Path("checkVersion")
	public void checkVersion(VersionCheckParam param) {
		String companyID = AppContexts.user().companyId();
		detailBeforeUpdate.exclusiveCheck(companyID, param.appID, param.version);
	}
	
	@POST
	@Path("cancelapp")
	public Boolean cancelApp(AppDispInfoStartupDto command){
		return cancelApp.handle(command);
	}
	
	@POST
    @Path("print")
    public ExportServiceResult generate(AppPrintQuery query) {
		return applicationExportService.start(query);
    }
	
	@POST
	@Path("afterRegister")
	public AppListApproveResult afterRegister(AppNewBehaviorCmd appNewBehaviorCmd) {
		
		// return newAfterRegister.processAfterRegister(appID, appTypeSetting, mailServerSet);
		return null;
	}
	
	@POST
	@Path("afterUpdate")
	public AppListApproveResult afterUpdate(AppNewBehaviorCmd appNewBehaviorCmd) {
		
		// return newAfterRegister.processAfterRegister(appID, appTypeSetting, mailServerSet);
		return null;
	}
	
	@POST
	@Path("initApp")
	public boolean registerSample() {
		return true;
	}
	
	@POST
	@Path("checkBeforeSample")
	public List<ConfirmMsgOutput> checkBeforeRegisterSample(List<String> msgIDLst) {
		if(msgIDLst.contains("Msg_324") || msgIDLst.contains("Msg_197") || msgIDLst.contains("Msg_26")) {
			throw new BusinessException(msgIDLst.get(0));
		}
		if(CollectionUtil.isEmpty(msgIDLst)) {
			return Collections.emptyList();
		}
		return msgIDLst.stream().map(x -> new ConfirmMsgOutput(x, Collections.emptyList())).collect(Collectors.toList());
	}
	
	@POST
	@Path("changeDataSample")
	public ProcessResult registerSample(List<String> msgIDLst) {
		ProcessResult processResult = new ProcessResult();
		processResult.setProcessDone(true);
		return processResult;
	}
}

