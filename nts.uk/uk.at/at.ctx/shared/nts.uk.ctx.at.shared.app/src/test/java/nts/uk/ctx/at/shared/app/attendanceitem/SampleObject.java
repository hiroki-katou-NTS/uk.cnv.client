package nts.uk.ctx.at.shared.app.attendanceitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemLayout;
import nts.uk.ctx.at.shared.app.util.attendanceitem.annotation.AttendanceItemValue;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SampleObject {

	@AttendanceItemValue(itemId = {1, 1, 1})
	@AttendanceItemLayout(layout = "A", jpPropertyName = "A")
	private String attendanceItem;

	@AttendanceItemValue(itemId = {3, 3, 3})
	@AttendanceItemLayout(layout = "B", jpPropertyName = "AffiliationInfor")
	private String attendanceItem2;
}
