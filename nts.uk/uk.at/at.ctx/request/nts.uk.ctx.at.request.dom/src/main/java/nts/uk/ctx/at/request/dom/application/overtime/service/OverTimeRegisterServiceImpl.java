package nts.uk.ctx.at.request.dom.application.overtime.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationApprovalService;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.RegisterAtApproveReflectionInfoService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.after.NewAfterRegister;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class OverTimeRegisterServiceImpl implements OverTimeRegisterService {
	
	@Inject
	private DetailAfterUpdate detailAfterUpdate;
	
	@Inject
	private ApplicationRepository appUpdateRepository;
	
	@Inject
	ApplicationApprovalService appRepository;
	
	@Inject
	AppOverTimeRepository appOverTimeRepository;
	
	@Inject
	private RegisterAtApproveReflectionInfoService registerService;
	
	@Inject 
	NewAfterRegister newAfterRegister;
	
	@Inject
    private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	@Override
	public ProcessResult register(
			String companyId,
			AppOverTime appOverTime,
			// change listApproval -> common setting
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			Boolean mailServerSet,
			AppTypeSetting appTypeSetting) {
		Application application = appOverTime.getApplication();
		// ?????????????????????
		appRepository.insertApp(application,
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpListApprovalPhaseState().orElse(Collections.emptyList()));
		String reflectAppId = registerService.newScreenRegisterAtApproveInfoReflect(application.getEmployeeID(), application);
		appOverTimeRepository.add(appOverTime);
		
		// ????????????????????????(pendding)
		this.interimRemainDataMngRegisterDateChange.registerDateChange(
                AppContexts.user().companyId(),
                application.getEmployeeID(),
                Arrays.asList(application.getAppDate().getApplicationDate())
        );
		
		// 2-3.??????????????????????????????????????? #112628
		ProcessResult processResult = newAfterRegister.processAfterRegister(
				Arrays.asList(application.getAppID()), 
				appTypeSetting,
				mailServerSet,
				false);
		if(Strings.isNotBlank(reflectAppId)) {
			processResult.setReflectAppIdLst(Arrays.asList(reflectAppId));
		}
		return processResult;
	}

	@Override
	public ProcessResult update(
			String companyId,
			AppOverTime appOverTime,
			AppDispInfoStartupOutput appDispInfoStartupOutput
			) {
		Application application = appOverTime.getApplication();
		// ??????????????????????????????????????????????????????
		appUpdateRepository.update(application);
		appOverTimeRepository.update(appOverTime);
		
		// ????????????????????????
		this.interimRemainDataMngRegisterDateChange.registerDateChange(
                AppContexts.user().companyId(),
                application.getEmployeeID(),
                Arrays.asList(application.getAppDate().getApplicationDate())
        );
		
		// ?????????????????????4-2.????????????????????????????????????????????????
		return detailAfterUpdate.processAfterDetailScreenRegistration(
				companyId,
				application.getAppID(),
				appDispInfoStartupOutput); //#112628
	}

	@Override
	public ProcessResult insertMobile(
			String companyId,
			Boolean mode,
			AppOverTime appOverTime,
			Boolean isMailServer,
			AppDispInfoStartupOutput appDispInfoStartupOutput) {
		// INPUT?????????????????????????????????????????????
		
		if (mode) {
			return this.register(
					companyId,
					appOverTime,
					appDispInfoStartupOutput,
					isMailServer,
					appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getApplicationSetting().getAppTypeSettings().get(0));
		} else {
			return this.update(
					companyId,
					appOverTime,
					appDispInfoStartupOutput);
		}
	}

	@Override
	public ProcessResult registerMultiple(
			String companyId,
			AppOverTime appOverTime,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			Boolean mailServerSet,
			AppTypeSetting appTypeSetting,
			Map<String, ApprovalRootContentImport_New> approvalRootContentMap
			) {
		List<String> guidS = new ArrayList<>();
		List<String> reflectAppIdLst = new ArrayList<>();
		for (EmployeeInfoImport el : appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst()) {
			String sid = el.getSid();
			// INPUT?????????????????????????????????????????????
			String UID = IdentifierUtil.randomUniqueId();
			// List?????????ID????????????????????????ID?????????
			guidS.add(UID);
			// 04_??????????????????
			Application application = appOverTime.getApplication();
			application.setAppID(UID);
			application.setEmployeeID(sid);
			appOverTime.setApplication(application);
			// ?????????????????????
			appRepository.insertApp(
					application,
					approvalRootContentMap.get(sid)
										  .getApprovalRootState()
										  .getListApprovalPhaseState());
			String reflectAppId = registerService.newScreenRegisterAtApproveInfoReflect(application.getEmployeeID(), application);
			if(Strings.isNotBlank(reflectAppId)) {
				reflectAppIdLst.add(reflectAppId);
			}
			appOverTimeRepository.add(appOverTime);
			
			// ????????????????????????(pendding)
			this.interimRemainDataMngRegisterDateChange.registerDateChange(
	                AppContexts.user().companyId(),
	                application.getEmployeeID(),
	                Arrays.asList(application.getAppDate().getApplicationDate())
	        );
		}
		// List?????????ID?????????????????????
		// 2-3.??????????????????????????????????????? #112628
		ProcessResult processResult = newAfterRegister.processAfterRegister(
			guidS, 
			appTypeSetting,
			mailServerSet,
			true);
		processResult.setReflectAppIdLst(reflectAppIdLst);
		return processResult;
	}
	

}
