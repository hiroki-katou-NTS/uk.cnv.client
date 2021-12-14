package nts.uk.ctx.workflow.dom.approvermanagement.approvalroot.output;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalForm;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhase;
import nts.uk.ctx.workflow.dom.service.output.ApproverInfo;
import nts.uk.ctx.workflow.dom.service.output.ErrorFlag;

/**
 * 承認フェーズDto
 * 
 * @author vunv
 *
 */
@Getter
@AllArgsConstructor
public class ApprovalPhaseOutput {
	/** 承認ID */
	private String approvalId;
	/** 承認フェーズ順序 */
	private int phaseOrder;
	/** 承認形態 */
	private int approvalForm;
	/** 閲覧フェーズ */
	private int browsingPhase;
	/**承認者指定区分*/
	private int approvalAtr;
	/** 承認者*/
	private List<ApproverInfo> approvers;
	
	public static ApprovalPhaseOutput convertToOutputData(ApprovalPhase phase) {
		return new ApprovalPhaseOutput(
				phase.getApprovalId(), 
				phase.getPhaseOrder(), 
				phase.getApprovalForm().value, 
				phase.getBrowsingPhase(),
				phase.getApprovalAtr().value,
				null);
	}
	
	public void addApproverList(List<ApproverInfo> approvers) {
		this.approvers = approvers;
	}
	
	public ErrorFlag checkError() {
		if (this.approvers.size() > 10) {
			return ErrorFlag.APPROVER_UP_10;
		}
		
		if (this.approvers.isEmpty()) {
			return ErrorFlag.NO_APPROVER;
		}
		
		return ErrorFlag.NO_ERROR;
	}
	
	public boolean isSingleApproved() {
		return this.approvalForm == ApprovalForm.SINGLE_APPROVED.value;
	}
	
	public boolean containsConfirmer() {
		return this.approvers.stream().anyMatch(a -> a.getIsConfirmPerson());
	}
}
