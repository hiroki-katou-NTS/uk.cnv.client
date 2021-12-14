package nts.uk.ctx.workflow.dom.approvermanagement.approvalroot.output;

import java.util.List;

import lombok.Getter;
import lombok.val;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhase;
import nts.uk.ctx.workflow.dom.service.output.ErrorFlag;

public class AdjustedApprovalPhases {
	@Getter
	public final List<ApprovalPhaseOutput> phases;

	public AdjustedApprovalPhases(List<ApprovalPhaseOutput> phases) {
		this.phases = phases;
	}

	public ErrorFlag checkError(List<ApprovalPhase> masterPhases) {

		ErrorFlag errorFlag;

		for (val phase : this.phases) {

			val masterPhase = masterPhases.stream()
					.filter(mp -> mp.getPhaseOrder() == phase.getPhaseOrder()).findFirst().get();

			// マスタに承認者が設定されているか
			if (masterPhase.getApprovers().isEmpty()) {
				continue;
			}

			errorFlag = phase.checkError();
			if (errorFlag.isError()) {
				return errorFlag;
			}

			// ドメインモデル「承認フェーズ」．承認形態が誰か一人(domain 「承認フェーズ」．承認形態 = 誰か一人)
			if (!phase.isSingleApproved()) {
				continue;
			}

			// ドメインモデル「承認者」．確定者がtrue(domain「承認者」．確定者 = true)
			if (!masterPhase.containsConfirmer()) {
				continue;
			}

			// 確定者あるドメインモデル「承認者」から変換した実際の承認者がいない場合
			if (!phase.containsConfirmer()) {
				continue;
			}
		}

		return ErrorFlag.NO_ERROR;
	}
}
