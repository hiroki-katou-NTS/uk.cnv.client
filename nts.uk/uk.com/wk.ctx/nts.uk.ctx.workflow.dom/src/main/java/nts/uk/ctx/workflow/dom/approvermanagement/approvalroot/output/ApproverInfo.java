package nts.uk.ctx.workflow.dom.approvermanagement.approvalroot.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.workflow.dom.adapter.bs.dto.ConcurrentEmployeeImport;
import nts.uk.ctx.workflow.dom.approvermanagement.workroot.Approver;

/**
 * 承認者IDリスト
 * 
 * @author vunv
 *
 */
@Getter
@AllArgsConstructor
public class ApproverInfo {
	/**承認者Gコード*/
	private String jobGCD;
	/**社員ID*/
	private String sid;
	/**承認者順序*/
	private int approverOrder;
	/**確定者*/
	private Boolean isConfirmPerson;
	
	private String name;
	
	public static ApproverInfo create(Approver x, String employeeName) {
		return new ApproverInfo(x.getJobGCD(),
				x.getEmployeeId(), 
				x.getApproverOrder(),
				true, 
				employeeName);
	}
	
	public static ApproverInfo create(ConcurrentEmployeeImport emp) {
		return new ApproverInfo(
				emp.getJobId(),
				emp.getEmployeeId(),
				1,
				null,
				emp.getPersonName());
	}
}
