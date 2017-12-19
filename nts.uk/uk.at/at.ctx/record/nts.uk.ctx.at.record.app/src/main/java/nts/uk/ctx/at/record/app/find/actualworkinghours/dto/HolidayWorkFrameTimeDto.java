package nts.uk.ctx.at.record.app.find.actualworkinghours.dto;

import lombok.Data;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemLayout;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemValue;
import nts.uk.ctx.at.shared.app.util.attendanceitem.type.ValueType;

/** 休出枠時間 */
@Data
public class HolidayWorkFrameTimeDto {

	/** 休出時間: 計算付き時間 */
	@AttendanceItemLayout(layout = "A")
	private CalcAttachTimeDto holidayWorkTime;

	/** 振替時間: 計算付き時間 */
	@AttendanceItemLayout(layout = "B")
	private CalcAttachTimeDto transferTime;

	/** 事前申請時間: 勤怠時間 */
	@AttendanceItemLayout(layout = "C")
	@AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private Integer beforeApplicationTime;

	/** 休出枠NO: 休出枠NO */
	@AttendanceItemLayout(layout = "D")
	@AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private Integer holidayFrameNo;
}
