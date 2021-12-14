package nts.uk.ctx.workflow.pub.approvalroot.export;

import lombok.Getter;

/**
 * 承認者IDリスト
 * 
 * @author vunv
 *
 */
@Getter
public class ApproverInfoExport {
	
	/**職位ID*/
	private String jobGCD;
	/**社員ID*/
	private String sid;
	/** 承認者順序 */
	private int approverOrder;
	/** 確定者 */
	private Boolean isConfirmPerson;
	
	private String name;

	public ApproverInfoExport(String jobGCD, String sid, int approverOrder, boolean isConfirmPerson) {
		super();
		this.jobGCD = jobGCD;
		this.sid = sid;
		this.isConfirmPerson = isConfirmPerson;
		this.approverOrder = approverOrder;
	}

	public void addEmployeeName(String name) {
		this.name = name;
	}
}
