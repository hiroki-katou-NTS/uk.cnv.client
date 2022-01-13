package nts.uk.screen.at.app.kdw013.e;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAttendanceTimeZoneBySupportWorkCommand {
	// 社員ID
	public String empId;
	
	// 年月日
	public GeneralDate date;
	
	// 応援時間帯
	public OuenWorkTimeSheetOfDailyAttendanceCommand ouenTimeSheet;
	
	// 応援時間
	public OuenWorkTimeOfDailyAttendanceCommand ouenTime;
}