package nts.uk.screen.at.ws.kdw.kdw013;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.monthly.root.common.DatePeriodDto;

/**
 * 
 * @author tutt
 *
 */
@Getter
@Setter
public class SelectTargetEmployeeParam {
	
	// 社員ID
	private String employeeId;
	
	// 基準日
	private GeneralDate refDate;
	
	//表示期間
	private DatePeriodDto displayPeriod;

}
