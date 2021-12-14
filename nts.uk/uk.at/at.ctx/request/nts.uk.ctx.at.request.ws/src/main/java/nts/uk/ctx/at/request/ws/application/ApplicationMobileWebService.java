package nts.uk.ctx.at.request.ws.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.command.application.common.AppDetailBehaviorCmd;
import nts.uk.ctx.at.request.app.command.application.common.ApproveAppMobileHandler;
import nts.uk.ctx.at.request.app.command.application.common.DenyAppMobileHandler;
import nts.uk.ctx.at.request.app.command.application.common.ReleaseAppMobileHandler;
import nts.uk.ctx.at.request.app.command.application.common.RemandAppMobileHandler;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoStartupDto;
import nts.uk.ctx.at.request.app.find.application.common.service.smartphone.output.RequestMsgInfoDto;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.RemandCommand;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.init.DetailAppCommonSetService;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ApproveProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.smartphone.CommonAlgorithmMobile;
import nts.uk.ctx.at.request.dom.application.common.service.smartphone.output.RequestMsgInfoOutput;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.ws.application.common.StartupParam;
import nts.uk.shr.com.context.AppContexts;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@Path("at/request/app/smartphone")
@Produces("application/json")
public class ApplicationMobileWebService {
	
	@Inject
	private CommonAlgorithmMobile commonAlgorithmMobile;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private DetailAppCommonSetService detailAppCommonSetService;
	
	@Inject
	private ApproveAppMobileHandler approveAppMobileHandler;
	
	@Inject
	private ReleaseAppMobileHandler releaseAppMobileHandler;
	
	@Inject
	private DenyAppMobileHandler denyAppMobileHandler;

	@Inject
	private RemandAppMobileHandler remandAppMobileHandler;
	
	@POST
	@Path("requestmsg")
	public RequestMsgInfoDto getRequestMsgInfoOutputMobile(RequestMsgInfoMobileParam requestMsgInfoMobileParam) {
		RequestMsgInfoOutput requestMsgInfoOutput = commonAlgorithmMobile.getRequestMsgInfoOutputMobile(
				requestMsgInfoMobileParam.getCompanyID(), 
				requestMsgInfoMobileParam.getEmployeeID(), 
				requestMsgInfoMobileParam.getEmploymentCD(), 
				requestMsgInfoMobileParam.getApplicationUseSetting().toDomain(), 
				requestMsgInfoMobileParam.getReceptionRestrictionSetting().toDomain(), 
				requestMsgInfoMobileParam.getOpOvertimeAppAtr() == null 
					? Optional.empty() 
					: Optional.of(EnumAdaptor.valueOf(requestMsgInfoMobileParam.getOpOvertimeAppAtr(), OvertimeAppAtr.class)));
		return RequestMsgInfoDto.fromDomain(requestMsgInfoOutput);
	} 
	
	@POST
	@Path("getStartNewMob")
	public AppDispInfoStartupDto getStartNewMob(StartNewMobParam startMobileParam) {
		
		List<String> sids = new ArrayList<String>();
		String sid = startMobileParam.getEmployeeID();
		if (!StringUtils.isBlank(sid)) {
			sids.add(sid);
		}
		StartupParam param = new StartupParam(sids, startMobileParam.getDateLst(), startMobileParam.getAppType(), startMobileParam.getHolidayAppType(), startMobileParam.getOvertimeAppAtr());
		
		String companyID = AppContexts.user().companyId();
		List<GeneralDate> dateLst = Collections.emptyList();
		if (!CollectionUtil.isEmpty(param.getDateLst())) {
			dateLst = param.getDateLst().stream().map(x -> {
				if(Strings.isBlank(x)) {
					return null;
				} else {
					return GeneralDate.fromString(x, "yyyy/MM/dd");
				}
			}).collect(Collectors.toList());			
		}
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
	@Path("getDetailMob")
	public AppDispInfoStartupDto getDetailMob(String appID) {
		String companyID = AppContexts.user().companyId();
		AppDispInfoStartupOutput appDispInfoStartupOutput = detailAppCommonSetService.getCommonSetBeforeDetail(companyID, appID);
		return AppDispInfoStartupDto.fromDomain(appDispInfoStartupOutput);
	}
	
	@POST
	@Path("approveapp")
	public ApproveProcessResult approveApp(AppDetailBehaviorCmd command){
		return approveAppMobileHandler.handle(command);
	}
	
	@POST
	@Path("releaseapp")
	public ProcessResult releaseApp(AppDispInfoStartupDto command){
		return releaseAppMobileHandler.handle(command);
	}
	
	@POST
	@Path("denyapp")
	public ProcessResult denyApp(AppDetailBehaviorCmd command){
		return denyAppMobileHandler.handle(command);
	}
	
	@POST
	@Path("remandapp")
	public ProcessResult remandApp(RemandCommand command){
		return remandAppMobileHandler.handle(command);
	}
	
}
