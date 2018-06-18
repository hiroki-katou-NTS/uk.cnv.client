package nts.uk.ctx.workflow.dom.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalBehaviorAtr;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalFrame;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalPhaseState;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalRootState;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalRootStateRepository;
import nts.uk.ctx.workflow.dom.service.output.ApprovalRepresenterOutput;

/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
public class RemandImpl implements RemandService {
	
	@Inject
	private ApprovalRootStateRepository approvalRootStateRepository;
	
	@Inject
	private JudgmentApprovalStatusService judgmentApprovalStatusService;
	
	@Inject
	private ReleaseAllAtOnceService releaseAllAtOnceService;
	
	@Inject
	private CollectApprovalAgentInforService collectApprovalAgentInforService;

	@Override
	public List<String> doRemandForApprover(String companyID, String rootStateID, Integer order, Integer rootType) {
		Optional<ApprovalRootState> opApprovalRootState = approvalRootStateRepository.findByID(rootStateID, rootType);
		if(!opApprovalRootState.isPresent()){
			throw new RuntimeException("状態：承認ルート取得失敗"+System.getProperty("line.separator")+"error: ApprovalRootState, ID: "+rootStateID);
		}
		ApprovalRootState approvalRootState = opApprovalRootState.get();
		List<ApprovalPhaseState> listApprovalPhase = approvalRootState.getListApprovalPhaseState();
		listApprovalPhase.sort(Comparator.comparing(ApprovalPhaseState::getPhaseOrder).reversed());
		List<ApprovalPhaseState> listUpperPhase = listApprovalPhase.stream().filter(x -> x.getPhaseOrder() >= order).collect(Collectors.toList());
		listUpperPhase.forEach(approvalPhaseState -> {
			Boolean phaseNotApprovalFlag = approvalPhaseState.getApprovalAtr().equals(ApprovalBehaviorAtr.UNAPPROVED);
			for(ApprovalFrame approvalFrame : approvalPhaseState.getListApprovalFrame()){
				phaseNotApprovalFlag = Boolean.logicalAnd(phaseNotApprovalFlag, approvalFrame.getApprovalAtr().equals(ApprovalBehaviorAtr.UNAPPROVED));
			}
			if(phaseNotApprovalFlag.equals(Boolean.TRUE)){
				return;
			}
			approvalPhaseState.getListApprovalFrame().forEach(approvalFrame -> {
				approvalFrame.setApprovalAtr(ApprovalBehaviorAtr.UNAPPROVED);
				approvalFrame.setApproverID("");
				approvalFrame.setRepresenterID("");
				approvalFrame.setApprovalDate(GeneralDate.today());
				approvalFrame.setApprovalReason("");
			});
			approvalPhaseState.setApprovalAtr(ApprovalBehaviorAtr.UNAPPROVED);
		});
		ApprovalPhaseState currentPhase = listApprovalPhase.stream().filter(x -> x.getPhaseOrder()==order).findFirst().get();
		currentPhase.setApprovalAtr(ApprovalBehaviorAtr.REMAND);
		approvalRootStateRepository.update(approvalRootState, rootType);
		// 送信者ID＝送信先リスト
		List<String> approvers = judgmentApprovalStatusService.getApproverFromPhase(currentPhase);
		ApprovalRepresenterOutput approvalRepresenterOutput = collectApprovalAgentInforService.getApprovalAgentInfor(companyID, approvers);
		approvers.addAll(approvalRepresenterOutput.getListAgent());
		return approvers.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public void doRemandForApplicant(String companyID, String rootStateID, Integer rootType) {
		releaseAllAtOnceService.doReleaseAllAtOnce(companyID, rootStateID, rootType);
	}

	@Override
	public Integer getCurrentApprovePhase(String rootStateID, Integer rootType) {
		Optional<ApprovalRootState> opApprovalRootState = approvalRootStateRepository.findByID(rootStateID, rootType);
		if(!opApprovalRootState.isPresent()){
			throw new RuntimeException("状態：承認ルート取得失敗"+System.getProperty("line.separator")+"error: ApprovalRootState, ID: "+rootStateID);
		}
		ApprovalRootState approvalRootState = opApprovalRootState.get();
		List<ApprovalPhaseState> listApprovalPhase = approvalRootState.getListApprovalPhaseState();
		listApprovalPhase.sort(Comparator.comparing(ApprovalPhaseState::getPhaseOrder).reversed());
		return listApprovalPhase.stream().filter(x -> x.getApprovalAtr()==ApprovalBehaviorAtr.APPROVED).findFirst().get().getPhaseOrder()+1;
	}

}
