package nts.uk.ctx.at.record.app.find.actualworkinghours.dto;

import lombok.Data;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemLayout;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemValue;
import nts.uk.ctx.at.shared.app.util.attendanceitem.type.ValueType;

/** 計算付き時間 and 計算付き時間(マイナス有り) */
@Data
public class CalcAttachTimeDto {

	/** 時間 */
	@AttendanceItemLayout(layout = "A")
	@AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private Integer calcTime;

	/** 計算時間 */
	@AttendanceItemLayout(layout = "B")
	@AttendanceItemValue(itemId = -1, type = ValueType.INTEGER)
	private Integer time;
}
