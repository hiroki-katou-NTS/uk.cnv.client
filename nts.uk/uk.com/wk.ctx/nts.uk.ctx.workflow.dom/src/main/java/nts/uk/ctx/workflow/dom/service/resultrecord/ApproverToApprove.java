package nts.uk.ctx.workflow.dom.service.resultrecord;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
/**
 * 承認すべき承認者
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ApproverToApprove {
	
	/**
	 * 対象日
	 */
	private GeneralDate date;
	
	/**
	 * 対象者
	 */
	private String employeeID;
	
	/**
	 * 承認者
	 */
	private List<ApproverEmployee> authorList;
	
}
