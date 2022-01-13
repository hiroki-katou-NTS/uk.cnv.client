package nts.uk.screen.at.app.kdw006.j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyAttendanceItemKDW006Dto {
	
	/* 勤怠項目ID */
	private int attendanceItemId;

	/* 勤怠項目名称 */
	private String attendanceName;

	/* 表示番号 */
	private int displayNumber;

	public DailyAttendanceItemKDW006Dto(AttItemName param) {
		super();
		this.attendanceItemId = param.getAttendanceItemId();
		this.attendanceName = param.getAttendanceItemName();
		this.displayNumber = param.getAttendanceItemDisplayNumber();
	}

}
