package nts.uk.ctx.at.record.app.find.dailyperform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemLayout;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemValue;
import nts.uk.ctx.at.shared.app.util.attendanceitem.type.ValueType;

/** 残業枠時間 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverTimeFrameTimeDto {

	/** 振替時間: 計算付き時間 */
	@AttendanceItemLayout(layout = "A", jpPropertyName = "振替時間")
	private CalcAttachTimeDto transferTime;

	/** 残業時間: 計算付き時間 */
	@AttendanceItemLayout(layout = "B", jpPropertyName = "残業時間")
	private CalcAttachTimeDto overtime;

	/** 事前申請時間: 勤怠時間 */
	@AttendanceItemLayout(layout = "C", jpPropertyName = "事前申請時間")
	@AttendanceItemValue(itemId = { 220, 225, 230, 235, 240, 245, 250, 255, 260, 265 }, type = ValueType.INTEGER)
	private Integer beforeApplicationTime;

	/** 指示時間: 勤怠時間 */
//	@AttendanceItemLayout(layout = "D")
//	@AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private Integer orderTime;

	/** 残業枠NO: 残業枠NO */
	// @AttendanceItemLayout(layout = "E")
	// @AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private Integer overtimeFrameNo;
}
