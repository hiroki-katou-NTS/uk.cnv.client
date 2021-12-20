package nts.uk.ctx.bs.employee.dom.jobtitle.affiliate;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;

/*
 *期間付き職位履歴項目 
 */
@Data
@AllArgsConstructor
public class AffJobTitleHistoryItemWithPeriod {
	
	private GeneralDate startDate;
	
	private GeneralDate endDate;
	
	private String historyId;

	private String employeeId;

	private String jobTitleId;

}
