package nts.uk.ctx.at.request.dom.application.holidayworktime.service;

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
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.RegisterAtApproveReflectionInfoService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.after.NewAfterRegister;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.AppHdWorkDispInfoOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;

/**
 * Refactor5
 * @author huylq
 *
 */
@Stateless
public class HolidayWorkRegisterServiceImpl implements HolidayWorkRegisterService {
	
	@Inject
	private ApplicationApprovalService applicationApprovalService;
	
	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;

	@Inject
	private RegisterAtApproveReflectionInfoService registerAtApproveReflectionInfoService;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject 
	private NewAfterRegister newAfterRegister;
	
	@Inject
	private DetailAfterUpdate detailAfterUpdate;
	
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;
	
	@Override
	public ProcessResult register(String companyId, AppHolidayWork appHolidayWork, AppTypeSetting appTypeSetting, 
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput) {
		Application application = appHolidayWork.getApplication();
		
		//	2-2.????????????????????????????????????????????????
		applicationApprovalService.insertApp(application, 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpListApprovalPhaseState().orElse(Collections.emptyList()));
		String reflectAppId = registerAtApproveReflectionInfoService.newScreenRegisterAtApproveInfoReflect(appHolidayWork.getApplication().getEmployeeID(), application);
		appHolidayWorkRepository.add(appHolidayWork);
		
		//	???????????????????????? (pending)
		interimRemainDataMngRegisterDateChange.registerDateChange(
				companyId, 
				application.getEmployeeID(), 
				Arrays.asList(application.getAppDate().getApplicationDate()));
		
		//	2-3.??????????????????????????????
		ProcessResult processResult = newAfterRegister.processAfterRegister(
				Arrays.asList(application.getAppID()), 
				appTypeSetting,
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().isMailServerSet(),
				false);
		if(Strings.isNotBlank(reflectAppId)) {
			processResult.setReflectAppIdLst(Arrays.asList(reflectAppId));
		}
		return processResult;
	}
	
	@Override
	public ProcessResult registerMulti(String companyId, List<String> empList, AppTypeSetting appTypeSetting,
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput, AppHolidayWork appHolidayWork,
			Map<String, ApprovalRootContentImport_New> approvalRootContentMap) {
		List<String> applicationIdList = new ArrayList<String>();
		List<String> reflectAppIdLst = new ArrayList<>();
		//	INPUT???????????????????????????????????????
		empList.forEach(empId -> {
			//	?????????????????????????????????????????????INPUT?????????????????????
			AppHolidayWork empAppHolidayWork = appHolidayWork;
			empAppHolidayWork.setEmployeeID(empId);
			String appId = IdentifierUtil.randomUniqueId();
			empAppHolidayWork.setAppID(appId);
			List<ApprovalPhaseStateImport_New> listApprovalPhaseState = approvalRootContentMap.get(empId).getApprovalRootState().getListApprovalPhaseState();
			//	List?????????ID??????Add(?????????GUID)
			applicationIdList.add(appId);
			
			//	2-2.????????????????????????????????????????????????
			applicationApprovalService.insertApp(empAppHolidayWork.getApplication(), listApprovalPhaseState);
			String reflectAppId = registerAtApproveReflectionInfoService.newScreenRegisterAtApproveInfoReflect(appHolidayWork.getApplication().getEmployeeID(), empAppHolidayWork.getApplication());
			if(Strings.isNotBlank(reflectAppId)) {
				reflectAppIdLst.add(reflectAppId);
			}
			appHolidayWorkRepository.add(empAppHolidayWork);
			
			//	???????????????????????? (pending)
			interimRemainDataMngRegisterDateChange.registerDateChange(
					companyId, 
					empAppHolidayWork.getEmployeeID(), 
					Arrays.asList(empAppHolidayWork.getAppDate().getApplicationDate()));
		});
		
		//	List?????????ID?????????????????????
		//2-3.??????????????????????????????
		ProcessResult processResult = newAfterRegister.processAfterRegister(
				applicationIdList, 
				appTypeSetting, 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().isMailServerSet(),
				true);
		processResult.setReflectAppIdLst(reflectAppIdLst);
		return processResult;
	}
	
	@Override
	public ProcessResult update(String companyId, AppHolidayWork appHolidayWork, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		Application application = (Application) appHolidayWork;
		//	????????????????????????????????????????????????
		applicationRepository.update(application);
		//	????????????????????????????????????????????????????????????
		appHolidayWorkRepository.update(appHolidayWork);
		
		//	????????????????????????
		interimRemainDataMngRegisterDateChange.registerDateChange(
				companyId, 
				application.getEmployeeID(), 
				Arrays.asList(application.getAppDate().getApplicationDate()));
		
		//	4-2.??????????????????????????????
		return detailAfterUpdate.processAfterDetailScreenRegistration(
				companyId,
				application.getAppID(),
				appDispInfoStartupOutput);
	}

	@Override
	public ProcessResult registerMobile(Boolean mode, String companyId, AppHdWorkDispInfoOutput appHdWorkDispInfo,
			AppHolidayWork appHolidayWork, AppTypeSetting appTypeSetting) {
		if(mode) {
			return this.register(companyId, appHolidayWork, appTypeSetting, appHdWorkDispInfo);
		} else {
			return this.update(companyId, appHolidayWork, appHdWorkDispInfo.getAppDispInfoStartupOutput());
		}
	}
}
