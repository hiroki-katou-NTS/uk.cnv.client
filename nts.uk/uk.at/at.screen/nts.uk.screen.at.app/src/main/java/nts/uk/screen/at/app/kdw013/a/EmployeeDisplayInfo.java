package nts.uk.screen.at.app.kdw013.a;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class EmployeeDisplayInfo {
	
	//List<作業グループ>
	private List<WorkGroup> workGroups;
	
	//年月日
	private GeneralDate date;
	
	//List＜確認者>
	private List<ConfirmerDto> lstComfirmerDto;
	
	//List<作業実績詳細>
	private List<WorkRecordDetail> workRecordDetails;
	
}
