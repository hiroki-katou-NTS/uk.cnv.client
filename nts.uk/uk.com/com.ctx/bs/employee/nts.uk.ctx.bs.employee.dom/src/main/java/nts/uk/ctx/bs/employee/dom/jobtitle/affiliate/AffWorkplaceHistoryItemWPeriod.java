package nts.uk.ctx.bs.employee.dom.jobtitle.affiliate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

/**
 * 期間付き職場履歴項目
 * @author nws-dungdv
 *
 */
@AllArgsConstructor
@Getter
public class AffWorkplaceHistoryItemWPeriod {
	
	private GeneralDate startDate;
	
	private GeneralDate endDate;
	
	private String historyId;
	
	private String employeeId;
	
	private String  workplaceId;
	
//	private String  normalWorkplaceId;
}
