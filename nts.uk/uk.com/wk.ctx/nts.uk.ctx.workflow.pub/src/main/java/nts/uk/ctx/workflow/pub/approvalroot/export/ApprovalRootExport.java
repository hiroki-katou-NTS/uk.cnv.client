package nts.uk.ctx.workflow.pub.approvalroot.export;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@Getter
@NoArgsConstructor
public class ApprovalRootExport {
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

	private List<ApprovalPhaseExport> beforeApprovers;

	private List<ApprovalPhaseExport> afterApprovers;

	/** エラーフラグ */
	private ErrorFlag errorFlag;

	public ApprovalRootExport(String companyId, String workplaceId, String approvalId, String employeeId,
			String historyId, GeneralDate startDate, GeneralDate endDate 
			// String branchId,
			// String anyItemApplicationId
			) {
		super();
		this.companyId = companyId;
		this.workplaceId = workplaceId;
		this.approvalId = approvalId;
		this.employeeId = employeeId;
		this.historyId = historyId;
		this.startDate = startDate;
		this.endDate = endDate;
		// this.branchId = branchId;
		// this.anyItemApplicationId = anyItemApplicationId;
	}

	public void addDataType(Integer applicationType, Integer confirmationRootType, int employmentRootAtr) {
		this.applicationType = applicationType;
		this.confirmationRootType = confirmationRootType;
		this.employmentRootAtr = employmentRootAtr;
	}

	public void addBeforeApprovers(List<ApprovalPhaseExport> beforeApprovers) {
		this.beforeApprovers = beforeApprovers;
	}

	public void addAfterApprovers(List<ApprovalPhaseExport> afterApprovers) {
		this.afterApprovers = afterApprovers;
	}
}
