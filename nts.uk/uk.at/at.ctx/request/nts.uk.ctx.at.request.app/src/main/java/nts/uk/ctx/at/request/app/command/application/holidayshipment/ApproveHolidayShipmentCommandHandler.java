package nts.uk.ctx.at.request.app.command.application.holidayshipment;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.InitMode;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterApproval;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.OutputMode;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.User;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ApproveProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class ApproveHolidayShipmentCommandHandler
		extends CommandHandlerWithResult<HolidayShipmentCommand, ApproveProcessResult> {

	@Inject
	private DetailBeforeUpdate detailBefUpdate;
	@Inject
	private DetailAfterApproval detailAfAppv;
	
//	@Inject
//	ApplicationSettingRepository applicationSettingRepository;
	
//	@Inject
//	private AppTypeDiscreteSettingRepository appTypeDiscreteSettingRepository;
	
	@Inject
	private InitMode initMode;
	
	@Inject
	private ApplicationRepository applicationRepository;

	@Override
	protected ApproveProcessResult handle(CommandHandlerContext<HolidayShipmentCommand> context) {
		HolidayShipmentCommand command = context.getCommand();
		String companyID = AppContexts.user().companyId();
		String employeeID = AppContexts.user().employeeId();
		int version = command.getAppVersion();
		String memo = context.getCommand().getMemo();
		
		OutputMode outputMode = initMode.getDetailScreenInitMode(EnumAdaptor.valueOf(command.getUser(), User.class), command.getReflectPerState());
		
		String appReason = Strings.EMPTY;
		boolean isUpdateReason = false;
		if(outputMode==OutputMode.EDITMODE){
//			AppTypeDiscreteSetting appTypeDiscreteSetting = appTypeDiscreteSettingRepository.getAppTypeDiscreteSettingByAppType(
//					companyID, 
//					ApplicationType.COMPLEMENT_LEAVE_APPLICATION.value).get();
//				
//			String typicalReason = Strings.EMPTY;
//			String displayReason = Strings.EMPTY;
//			if(appTypeDiscreteSetting.getTypicalReasonDisplayFlg().equals(AppDisplayAtr.DISPLAY)){
//				typicalReason += context.getCommand().getComboBoxReason();
//			}
//			if(appTypeDiscreteSetting.getDisplayReasonFlg().equals(AppDisplayAtr.DISPLAY)){
//				if(Strings.isNotBlank(typicalReason)){
//					displayReason += System.lineSeparator();
//				}
//				displayReason += context.getCommand().getTextAreaReason();
//			} else {
//				if(Strings.isBlank(typicalReason)){
//					boolean isApprovalRec = command.getRecAppID() != null;
//					boolean isApprovalAbs = command.getAbsAppID() != null;
//					if (isApprovalRec) {
//						displayReason = applicationRepository.findByID(companyID, command.getRecAppID()).get().getOpAppReason().get().v();
//					}
//					if (isApprovalAbs) {
//						displayReason = applicationRepository.findByID(companyID, command.getAbsAppID()).get().getOpAppReason().get().v();
//					}
//				}
//			}
//			Optional<ApplicationSetting> applicationSettingOp = applicationSettingRepository.getApplicationSettingByComID(companyID);
//			ApplicationSetting applicationSetting = applicationSettingOp.get();
//			if(appTypeDiscreteSetting.getTypicalReasonDisplayFlg().equals(AppDisplayAtr.DISPLAY)
//				||appTypeDiscreteSetting.getDisplayReasonFlg().equals(AppDisplayAtr.DISPLAY)){
//				if (applicationSetting.getRequireAppReasonFlg().equals(RequiredFlg.REQUIRED)
//						&& Strings.isBlank(typicalReason+displayReason)) {
//					throw new BusinessException("Msg_115");
//				}
//				appReason = typicalReason + displayReason;
//				isUpdateReason = true;
//			}
		}
		
		// ??????????????????????????????????????????????????????????????????
		ProcessResult processResult = approvalApplication(command, companyID, employeeID, version, memo, appReason, isUpdateReason);
		
		if(!isUpdateReason){
			appReason = applicationRepository.findByID(companyID, processResult.getAppIDLst().stream().findFirst().orElse(null)).get().getOpAppReason().get().v();
		}
		
		/*return new ApproveProcessResult(
				processResult.isProcessDone(), 
				processResult.isAutoSendMail(), 
				processResult.getAutoSuccessMail(), 
				processResult.getAutoFailMail(), 
				processResult.getAppID(), 
				processResult.getReflectAppId(), 
				appReason);*/
		return null;
	}

	private ProcessResult approvalApplication(HolidayShipmentCommand command, String companyID, String employeeeID,
			int version, String memo, String appReason, boolean isUpdateReason) {
		boolean isApprovalRec = command.getRecAppID() != null;
		boolean isApprovalAbs = command.getAbsAppID() != null;
		ProcessResult result = null;
		if (isApprovalRec) {
			// ???????????????????????????????????????????????????
			result = approvalProcessing(companyID, command.getRecAppID(), employeeeID, version, memo, appReason, isUpdateReason);
		}
		if (isApprovalAbs) {
			// ???????????????????????????????????????????????????
			result = approvalProcessing(companyID, command.getAbsAppID(), employeeeID, version, memo, appReason, isUpdateReason);
		}

		return result;
	}
	
    //KAF011 ????????????
	private ProcessResult approvalProcessing(String companyID, String appID, String employeeID, int version,
			String memo, String appReason, boolean isUpdateReason) {
        //EA???????????? No.3258
        //hoatt 2019.04.25
        // ????????????????????????????????????????????????????????? (th???c hi???n x??? l?? ???check version???)
        detailBefUpdate.exclusiveCheck(companyID, appID, version);
		// ??????????????????????????????????????????????????????????????????????????? kh??ng x??? l??

		// x??? l?? ?????ng th???i
		// ????????????????????????????????????????????????????????????
		// ?????????????????????????????????????????????????????????????????????
        // refactor 4 error
		//return detailAfAppv.doApproval(companyID, appID, employeeID, memo, appReason, isUpdateReason);
        return null;

	}

}
