package nts.uk.ctx.workflow.dom.approvermanagement.workroot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.AggregateRoot;
/**
 * 会社別就業承認ルート
 * @author hoatt
 *
 */
@Getter
@AllArgsConstructor
public class CompanyApprovalRoot extends AggregateRoot {
	/**会社ID*/
	private String companyId;
	/**承認ID*/
	public String approvalId;
	/**履歴ID*/
	private String historyId;
	/**申請種類*/
	private ApplicationType applicationType;
	/**期間*/
	private ApprovalPeriod period;
	/**分岐ID*/
	private String branchId;
	/**任意項目申請ID*/
	private String anyItemApplicationId;
	/**確認ルート種類*/
	private ConfirmationRootType confirmationRootType;
	/**就業ルート区分*/
	private EmploymentRootAtr employmentRootAtr;
	
	public static CompanyApprovalRoot createSimpleFromJavaType(String companyId,
			String approvalId,
			String historyId,
			int applicationType,
			String startDate,
			String endDate,
			String branchId,
			String anyItemApplicationId,
			int confirmationRootType,
			int employmentRootAtr){
		return new CompanyApprovalRoot(companyId,
			approvalId,
			historyId,
			EnumAdaptor.valueOf(applicationType, ApplicationType.class), 
			ApprovalPeriod.createSimpleFromJavaType(startDate, endDate),
			branchId,
			anyItemApplicationId,
			EnumAdaptor.valueOf(confirmationRootType, ConfirmationRootType.class),
			EnumAdaptor.valueOf(employmentRootAtr, EmploymentRootAtr.class));
	}
}
