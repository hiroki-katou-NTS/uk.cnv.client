package nts.uk.ctx.workflow.dom.approvermanagement.approvalroot.output;

import java.util.List;

import lombok.Data;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.ApprovalPhase;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.CompanyApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.PersonApprovalRoot;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.WorkplaceApprovalRoot;
import nts.uk.ctx.workflow.dom.service.output.ErrorFlag;

@Data
public class ApprovalRootOutput {
	/** 会社ID */
	private String companyId;
	/** 職場ID */
	private String workplaceId;
	/** 承認ID */
	public String approvalId;
	/** 社員ID */
	private String employeeId;
	/** 履歴ID */
	private String historyId;
	/** 申請種類 */
	private Integer applicationType;
	/** 開始日 */
	private GeneralDate startDate;
	/** 完了日 */
	private GeneralDate endDate;
//	/** 分岐ID */
//	private String branchId;
//	/** 任意項目申請ID */
//	private String anyItemApplicationId;
	/** 確認ルート種類 */
	private Integer confirmationRootType;
	/** 就業ルート区分 */
	private int employmentRootAtr;

	private List<ApprovalPhase> beforePhases;

	private AdjustedApprovalPhases afterPhases;

	/** エラーフラグ */
	private ErrorFlag errorFlag;

	public ApprovalRootOutput(String companyId, String workplaceId, String approvalId, String employeeId,
			String historyId, Integer applicationType, GeneralDate startDate, GeneralDate endDate, 
			// String branchId,
			// String anyItemApplicationId,
			Integer confirmationRootType, int employmentRootAtr,
			List<ApprovalPhase> beforeApprovers, List<ApprovalPhaseOutput> afterApprovers) {
		super();
		this.companyId = companyId;
		this.workplaceId = workplaceId;
		this.approvalId = approvalId;
		this.employeeId = employeeId;
		this.historyId = historyId;
		this.applicationType = applicationType;
		this.startDate = startDate;
		this.endDate = endDate;
		// this.branchId = branchId;
		// this.anyItemApplicationId = anyItemApplicationId;
		this.confirmationRootType = confirmationRootType;
		this.employmentRootAtr = employmentRootAtr;
		this.beforePhases = beforeApprovers;
		this.afterPhases = new AdjustedApprovalPhases(afterApprovers);
	}

	public static ApprovalRootOutput convertFromPersonData(PersonApprovalRoot x) {
		return new ApprovalRootOutput(x.getCompanyId(), null, x.getApprovalId(), x.getEmployeeId(), x.getApprRoot().getHistoryItems().get(0).getHistoryId(),
				x.getApprRoot().getApplicationType() == null ? null : x.getApprRoot().getApplicationType().value, x.getApprRoot().getHistoryItems().get(0).start(),
				x.getApprRoot().getHistoryItems().get(0).end(), 
				// x.getApprRoot().getBranchId(), 
				// x.getApprRoot().getAnyItemApplicationId(),
				x.getApprRoot().getConfirmationRootType() == null ? null : x.getApprRoot().getConfirmationRootType().value,
				x.getApprRoot().getEmploymentRootAtr().value, null, null);
	}

	public static ApprovalRootOutput convertFromWkpData(WorkplaceApprovalRoot x) {
		return new ApprovalRootOutput(x.getCompanyId(), x.getWorkplaceId(), x.getApprovalId(), null, x.getApprRoot().getHistoryItems().get(0).getHistoryId(),
				x.getApprRoot().getApplicationType() == null ? null : x.getApprRoot().getApplicationType().value, x.getApprRoot().getHistoryItems().get(0).start(),
				x.getApprRoot().getHistoryItems().get(0).end(), 
				// x.getApprRoot().getBranchId(), 
				// x.getApprRoot().getAnyItemApplicationId(),
				x.getApprRoot().getConfirmationRootType() == null ? null : x.getApprRoot().getConfirmationRootType().value,
				x.getApprRoot().getEmploymentRootAtr().value, null, null);
	}

	public static ApprovalRootOutput convertFromCompanyData(CompanyApprovalRoot x) {
		return new ApprovalRootOutput(x.getCompanyId(), null, x.getApprovalId(), null, x.getApprRoot().getHistoryItems().get(0).getHistoryId(),
				x.getApprRoot().getApplicationType() == null ? null : x.getApprRoot().getApplicationType().value, x.getApprRoot().getHistoryItems().get(0).start(),
				x.getApprRoot().getHistoryItems().get(0).end(), 
				// x.getApprRoot().getBranchId(), 
				// x.getApprRoot().getAnyItemApplicationId(),
				x.getApprRoot().getConfirmationRootType() == null ? null : x.getApprRoot().getConfirmationRootType().value,
				x.getApprRoot().getEmploymentRootAtr().value, null, null);
	}

	public void setAdjustedPhases(List<ApprovalPhaseOutput> phases) {
		this.afterPhases = new AdjustedApprovalPhases(phases);
	}
}
