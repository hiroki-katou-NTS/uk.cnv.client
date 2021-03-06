package nts.uk.ctx.workflow.dom.service.resultrecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalForm;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ConfirmPerson;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalBehaviorAtr;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalFrame;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalPhaseState;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApproverInfor;
import nts.uk.ctx.workflow.dom.resultrecord.AppFrameConfirm;
import nts.uk.ctx.workflow.dom.resultrecord.AppFrameInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppPhaseConfirm;
import nts.uk.ctx.workflow.dom.resultrecord.AppPhaseInstance;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootConfirm;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootConfirmRepository;
import nts.uk.ctx.workflow.dom.resultrecord.AppRootInstance;
import nts.uk.ctx.workflow.dom.service.ApproveService;
import nts.uk.ctx.workflow.dom.service.CollectApprovalAgentInforService;
import nts.uk.ctx.workflow.dom.service.output.ApprovalRepresenterOutput;
import nts.uk.shr.com.context.AppContexts;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class AppRootConfirmServiceImpl implements AppRootConfirmService {
	
	@Inject
	private CollectApprovalAgentInforService collectApprovalAgentInforService;
	
	@Inject
	private ApproveService approveService;
	
	@Inject
	private AppRootConfirmRepository appRootConfirmRepository;

	@Override
	public void approve(String approverID, String employeeID, GeneralDate date, AppRootInstance appRootInstance, AppRootConfirm appRootConfirm) {
		String companyID = AppContexts.user().companyId();
		// INPUT??????????????????????????????????????????????????????????????????????????????????????????1???5????????????????????????
		List<AppPhaseInstance> appPhaseInstanceLst = appRootInstance.getListAppPhase().stream()
				.sorted(Comparator.comparing(AppPhaseInstance::getPhaseOrder)).collect(Collectors.toList());
		for(AppPhaseInstance appPhaseInstance : appPhaseInstanceLst){
			AppPhaseConfirm appPhaseConfirm = new AppPhaseConfirm(appPhaseInstance.getPhaseOrder(), ApprovalBehaviorAtr.UNAPPROVED, new ArrayList<>());
			// ?????????????????????????????????????????????????????????????????????????????????
			Optional<AppPhaseConfirm> opAppPhaseConfirm = appRootConfirm.getListAppPhase().stream()
					.filter(phaseConfirm -> phaseConfirm.getPhaseOrder()==appPhaseInstance.getPhaseOrder()).findAny();
			if(opAppPhaseConfirm.isPresent()){
				appPhaseConfirm = opAppPhaseConfirm.get();
				if(appPhaseConfirm.getAppPhaseAtr()==ApprovalBehaviorAtr.APPROVED){
					continue;
				}
			}
			// ?????????????????????=false(?????????)
			ApprovalBehaviorAtr approvalFlg = ApprovalBehaviorAtr.UNAPPROVED;
			// ???????????????????????????????????????????????????1???5????????????????????????
			for(AppFrameInstance frameInstance : appPhaseInstance.getListAppFrame()){
				// ??????????????????????????????????????????????????????????????????????????????
				Optional<AppFrameConfirm> opAppFrameConfirm = appPhaseConfirm.getListAppFrame().stream()
						.filter(frameConfirm -> frameConfirm.getFrameOrder()==frameInstance.getFrameOrder()).findAny();
				if(opAppFrameConfirm.isPresent()){
					continue;
				}
				// ????????????????????????????????????????????????????????????????????????
				String approverIDParam = null, representerIDParam = null;
				if(!frameInstance.getListApprover().contains(approverID)){
					ApprovalRepresenterOutput approvalRepresenterOutput = collectApprovalAgentInforService.getApprovalAgentInfor(companyID, frameInstance.getListApprover());
					if(!approvalRepresenterOutput.getListAgent().contains(approverID)){
						continue;
					} else {
						representerIDParam = approverID;
					}
				} else {
					approverIDParam = approverID;
				}
				// ????????????????????????????????????????????????????????????
				AppFrameConfirm appFrameConfirm = new AppFrameConfirm(
						frameInstance.getFrameOrder(), 
						Optional.ofNullable(approverIDParam), 
						Optional.ofNullable(representerIDParam), 
						GeneralDate.today());
				appPhaseConfirm.getListAppFrame().add(appFrameConfirm);
				// ?????????????????????=true
				approvalFlg = ApprovalBehaviorAtr.APPROVED;
			}
			// ??????????????????????????????????????????
			if(approvalFlg==ApprovalBehaviorAtr.APPROVED){
				// ????????????????????????????????????????????????????????????????????????????????????
				if(!opAppPhaseConfirm.isPresent()){
					// ???????????????????????????????????????????????????????????????
					appRootConfirm.getListAppPhase().add(appPhaseConfirm);
				} else {
					AppPhaseConfirm oldAppPhaseConfirm = opAppPhaseConfirm.get();
					appRootConfirm.getListAppPhase().remove(oldAppPhaseConfirm);
					appRootConfirm.getListAppPhase().add(appPhaseConfirm);
				}
			}
			// ????????????????????????????????????????????????????????????????????????
			ApprovalPhaseState approvalPhaseState = this.convertPhaseInsToPhaseState(appPhaseInstance, appPhaseConfirm);
			// ?????????????????????????????????????????????????????????
			boolean phaseComplete = approveService.isApproveApprovalPhaseStateComplete(companyID, approvalPhaseState);
			// ????????????????????????????????????????????????????????????????????????????????????
			if(phaseComplete){
				appPhaseConfirm.setAppPhaseAtr(ApprovalBehaviorAtr.APPROVED);
			} else {
				break;
			}
		}
		appRootConfirmRepository.update(appRootConfirm);
	}

	@Override
	public boolean cleanStatus(String approverID, String employeeID, GeneralDate date, AppRootInstance appRootInstance, AppRootConfirm appRootConfirm) {
		// ?????????????????????????????????=false(?????????)
		boolean cleanComplete = false;
		// ????????????????????????=false(?????????)
		boolean loopCompleteFlg = false;
		// INPUT????????????????????????????????????????????????????????????????????????????????????????????????????????????5???1????????????????????????
		List<AppPhaseInstance> appPhaseInstanceLst = appRootInstance.getListAppPhase().stream()
				.sorted(Comparator.comparing(AppPhaseInstance::getPhaseOrder).reversed()).collect(Collectors.toList());
		for(AppPhaseInstance appPhaseInstance : appPhaseInstanceLst){
			// (??????????????????)???????????????????????????????????????????????????????????????
			List<String> approverLst = this.getApproverFromPhase(appPhaseInstance);
			if(approverLst.isEmpty()){
				// ?????????????????????????????????????????????
				if(loopCompleteFlg){
					break;
				}
				continue;
			}
			// ????????????????????????????????????????????????????????????
			Optional<AppPhaseConfirm> opAppPhaseConfirm = appRootConfirm.getListAppPhase().stream()
				.filter(phaseConfirm -> phaseConfirm.getPhaseOrder()==appPhaseInstance.getPhaseOrder()).findAny();
			if(!opAppPhaseConfirm.isPresent()){
				// ?????????????????????????????????????????????
				if(loopCompleteFlg){
					break;
				}
				continue;
			}
			// ????????????????????????????????????????????????????????????????????????
			AppPhaseConfirm appPhaseConfirm = opAppPhaseConfirm.get();
			ApprovalPhaseState approvalPhaseState = this.convertPhaseInsToPhaseState(appPhaseInstance, appPhaseConfirm);
			// ????????????????????????????????????
			if(!this.canCancelCheck(approvalPhaseState, approverID)) {
				break;
			}
			List<ApprovalFrame> confirmFrameLst = approvalPhaseState.getListApprovalFrame().stream().filter(x -> x.getConfirmAtr()==ConfirmPerson.CONFIRM).collect(Collectors.toList());
			// ????????????????????????????????????????????????
			if((approvalPhaseState.getApprovalForm()==ApprovalForm.SINGLE_APPROVED)&&confirmFrameLst.isEmpty()){
				appRootConfirm.getListAppPhase().remove(appPhaseConfirm);
				// ?????????????????????????????????=true
				cleanComplete = true;
				// ?????????????????????????????????????????????
				if(loopCompleteFlg){
					break;
				} 
				continue;
			}
			// ????????????????????????=false(?????????)
			loopCompleteFlg = false;
			for(AppFrameInstance appFrameInstance : appPhaseInstance.getListAppFrame()){
				Optional<AppFrameConfirm> opAppFrameConfirm = appPhaseConfirm.getListAppFrame().stream()
						.filter(frameConfirm -> frameConfirm.getFrameOrder()==appFrameInstance.getFrameOrder()).findAny();
				if(!opAppFrameConfirm.isPresent()){
					continue;
				}
				AppFrameConfirm appFrameConfirm = opAppFrameConfirm.get();
				// ????????????????????????????????????????????????????????????????????????
				if(appFrameInstance.getListApprover().contains(approverID) || appFrameConfirm.getRepresenterID().orElse("").equals(approverID)){
					// ????????????????????????????????????????????????????????????????????????????????????
					appPhaseConfirm.getListAppFrame().remove(appFrameConfirm);
					cleanComplete = true;
				}
			} 
			// ???????????????????????????????????????????????????????????????????????????
			if(CollectionUtil.isEmpty(appPhaseConfirm.getListAppFrame())){
				// ???????????????????????????????????????????????????????????????	
				appRootConfirm.getListAppPhase().remove(appPhaseConfirm);
			} else {
				// ???????????????????????????????????????????????????????????????=?????????
				appPhaseConfirm.setAppPhaseAtr(ApprovalBehaviorAtr.UNAPPROVED);
				// ????????????????????????=true
				loopCompleteFlg = true;
			}
			// ?????????????????????????????????????????????
			if(loopCompleteFlg){
				break;
			}
		}
		appRootConfirmRepository.update(appRootConfirm);
		return cleanComplete;
	}

	@Override
	public ApprovalPhaseState convertPhaseInsToPhaseState(AppPhaseInstance appPhaseInstance, AppPhaseConfirm appPhaseConfirm) {
		// output??????????????????????????????????????????????????????
		ApprovalPhaseState approvalPhaseState = new ApprovalPhaseState();
		approvalPhaseState.setApprovalAtr(appPhaseConfirm.getAppPhaseAtr());
		approvalPhaseState.setPhaseOrder(appPhaseInstance.getPhaseOrder());
		approvalPhaseState.setApprovalForm(appPhaseInstance.getApprovalForm());
		approvalPhaseState.setListApprovalFrame(new ArrayList<>());
		appPhaseInstance.getListAppFrame().forEach(frameInstance -> {
			ApprovalFrame approvalFrame = new ApprovalFrame();
			approvalFrame.setFrameOrder(frameInstance.getFrameOrder());
			approvalFrame.setConfirmAtr(frameInstance.isConfirmAtr() ? ConfirmPerson.CONFIRM : ConfirmPerson.NOT_CONFIRM);
			approvalFrame.setLstApproverInfo(new ArrayList<>());
			frameInstance.getListApprover().forEach(approver -> {
				ApproverInfor approverState = new ApproverInfor();
				approverState.setApprovalAtr(ApprovalBehaviorAtr.UNAPPROVED);
				approverState.setApproverID(approver);
				approvalFrame.getLstApproverInfo().add(approverState);
			});
			approvalPhaseState.getListApprovalFrame().add(approvalFrame);
		});
		approvalPhaseState.getListApprovalFrame().forEach(frame -> {
			Optional<AppFrameConfirm> opAppFrameConfirm = appPhaseConfirm.getListAppFrame().stream()
					.filter(frameConfirm -> frameConfirm.getFrameOrder()==frame.getFrameOrder()).findAny();
			if(opAppFrameConfirm.isPresent()){
				AppFrameConfirm appFrameConfirm = opAppFrameConfirm.get();
				if(!CollectionUtil.isEmpty(frame.getLstApproverInfo())) {
					frame.getLstApproverInfo().get(0).setApprovalAtr(ApprovalBehaviorAtr.APPROVED);
					frame.getLstApproverInfo().get(0).setApproverID(appFrameConfirm.getApproverID().orElse(""));
					frame.getLstApproverInfo().get(0).setAgentID(appFrameConfirm.getRepresenterID().orElse(""));
				}
				frame.setAppDate(appFrameConfirm.getApprovalDate());
			}
		});
		return approvalPhaseState;
	}

	@Override
	public List<String> getApproverFromPhase(AppPhaseInstance appPhaseInstance) {
		// ???????????????ID???????????????????????????????????????
		List<String> result = new ArrayList<>();
		// ?????????????????????????????????(loop approve frame ?????????)
		appPhaseInstance.getListAppFrame().forEach(appFrame -> {
			// ?????????????????????????????????????????????????????????????????????ID?????????????????????
			result.addAll(appFrame.getListApprover());
		});
		// ??????ID???????????????????????????
		return result.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public boolean canCancelCheck(ApprovalPhaseState approvalPhaseState, String employeeID) {
		// ???????????????????????? = false(?????????)
		boolean canCancel = false;
		// ??????????????????????????????????????????????????????????????????????????????
		if(approvalPhaseState.getApprovalForm()==ApprovalForm.EVERYONE_APPROVED){
			// ?????????????????????????????????????????????????????????????????????
			Optional<ApproverInfor> opApproverInfor = Optional.empty();
			List<ApprovalFrame> approvalFrameApprovedLst = approvalPhaseState.getListApprovalFrame().stream()
					.filter(frame -> frame.isApproved(approvalPhaseState.getApprovalForm())).collect(Collectors.toList());
			for(ApprovalFrame approvalFrame : approvalFrameApprovedLst) {
				Optional<ApproverInfor> opApproverInforApproved = approvalFrame.getLstApproverInfo().stream()
						.filter(approverInfor -> approverInfor.isApproved() && 
								((Strings.isNotBlank(approverInfor.getApproverID())&&approverInfor.getApproverID().equals(employeeID)) || 
								(Strings.isNotBlank(approverInfor.getAgentID())&&approverInfor.getAgentID().equals(employeeID))))
						.findAny();
				if(opApproverInforApproved.isPresent()) {
					opApproverInfor = opApproverInforApproved;
				}
			}
			if(opApproverInfor.isPresent()){
				// ???????????????????????? = true
				canCancel = true;
			}
		} else {
			// ?????????????????????????????????????????????(ki???m tra c?? c??i ?????t ????????? hay kh??ng)
			Optional<ApprovalFrame> opFrameConfirm = approvalPhaseState.getListApprovalFrame().stream()
					.filter(frame -> frame.getConfirmAtr()==ConfirmPerson.CONFIRM).findAny();
			if(opFrameConfirm.isPresent()){
				// ??????????????????????????????????????????????????????????????????????????????
				ApprovalFrame approvalFrame = opFrameConfirm.get();
				Optional<ApproverInfor> opApproverInforApproved = approvalFrame.getLstApproverInfo().stream()
						.filter(approverInfor -> approverInfor.isApproved() && 
								((Strings.isNotBlank(approverInfor.getApproverID())&&approverInfor.getApproverID().equals(employeeID)) || 
								(Strings.isNotBlank(approverInfor.getAgentID())&&approverInfor.getAgentID().equals(employeeID))))
						.findAny();
				if(opApproverInforApproved.isPresent()) {
					// ???????????????????????? = true
					canCancel = true;
				}
			} else {
				// ?????????????????????????????????????????????????????????????????????
				Optional<ApproverInfor> opApproverInfor = Optional.empty();
				List<ApprovalFrame> approvalFrameApprovedLst = approvalPhaseState.getListApprovalFrame().stream()
						.filter(frame -> frame.isApproved(approvalPhaseState.getApprovalForm())).collect(Collectors.toList());
				for(ApprovalFrame approvalFrame : approvalFrameApprovedLst) {
					Optional<ApproverInfor> opApproverInforApproved = approvalFrame.getLstApproverInfo().stream()
							.filter(approverInfor -> approverInfor.isApproved() && 
									((Strings.isNotBlank(approverInfor.getApproverID())&&approverInfor.getApproverID().equals(employeeID)) || 
									(Strings.isNotBlank(approverInfor.getAgentID())&&approverInfor.getAgentID().equals(employeeID))))
							.findAny();
					if(opApproverInforApproved.isPresent()) {
						opApproverInfor = opApproverInforApproved;
					}
				}
				if(opApproverInfor.isPresent()){
					// ???????????????????????? = true
					canCancel = true;
				}
			}
		}
		return canCancel;
	}

}
