package nts.uk.ctx.at.record.pub.dailyprocess.attendancetime;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 
 * @author keisuke_hoshina
 *
 */
public interface DailyAttendanceTimePub {
	//1日分の勤怠時間を仮計算
	// RequestList No.23
	DailyAttendanceTimePubExport calcDailyAttendance(DailyAttendanceTimePubImport imp);

	// RequestList No.23 different return
	IntegrationOfDaily calcOneDayAttendance(DailyAttendanceTimePubImport imp);
	
	//RequestList No.13
	public DailyAttendanceTimePubLateLeaveExport calcDailyLateLeave(DailyAttendanceTimePubImport imp);
	
}
