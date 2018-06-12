package nts.uk.ctx.at.request.dom.application.stamp;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.ApplicationApprovalService_New;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.RegisterAtApproveReflectionInfoService_New;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.after.NewAfterRegister_New;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.BeforePrelaunchAppCommonSet;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister_New;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.stamp.output.AppStampNewPreOutput;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class AppStampNewDefaultImpl implements AppStampNewDomainService {
	
	@Inject
	private BeforePrelaunchAppCommonSet beforePrelaunchAppCommonSet;
	
	@Inject
	private NewBeforeRegister_New newBeforeRegister; 
	
	@Inject
	private RegisterAtApproveReflectionInfoService_New registerAtApproveReflectionInfoService;
	
	@Inject
	private AppStampRepository appStampRepository;
	
	@Inject
	private NewAfterRegister_New newAfterRegister;
	
	@Inject
	private AppStampCommonDomainService appStampCommonDomainService;
	
	@Inject
	private ApplicationApprovalService_New applicationApprovalService;

	@Override
	public AppStampNewPreOutput appStampPreProcess(String companyID, String employeeID, GeneralDate appDate) {
		AppStampNewPreOutput appStampNewPreOutput = new AppStampNewPreOutput();
		appStampNewPreOutput.appCommonSettingOutput = beforePrelaunchAppCommonSet.prelaunchAppCommonSetService(
															companyID, 
															employeeID, 
															EmploymentRootAtr.APPLICATION.value,
															ApplicationType.STAMP_APPLICATION, 
															appDate);
		appStampNewPreOutput.appStampSetOutput = appStampCommonDomainService.appStampSet(companyID);
		appStampNewPreOutput.employeeName = appStampCommonDomainService.getEmployeeName(employeeID);
		return appStampNewPreOutput;
	}

	@Override
	public ProcessResult appStampRegister(String applicationReason, AppStamp appStamp) {
		appStampCommonDomainService.appReasonCheck(applicationReason, appStamp);
		appStampCommonDomainService.validateReason(appStamp);
		return appStampRegistration(appStamp);
	}
	
	// 打刻申請の新規登録
	private ProcessResult appStampRegistration(AppStamp appStamp) {
		newBeforeRegister.processBeforeRegister(appStamp.getApplication_New(),0);
		appStamp.customValidate();
		appStampRepository.addStamp(appStamp);
		applicationApprovalService.insert(appStamp.getApplication_New());
		registerAtApproveReflectionInfoService.newScreenRegisterAtApproveInfoReflect(
				appStamp.getApplication_New().getEmployeeID(), 
				appStamp.getApplication_New());
		return newAfterRegister.processAfterRegister(appStamp.getApplication_New());
	}
}
