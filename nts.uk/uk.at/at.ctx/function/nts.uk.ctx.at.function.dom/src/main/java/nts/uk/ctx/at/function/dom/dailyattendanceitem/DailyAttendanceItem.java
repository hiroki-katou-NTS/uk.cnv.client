package nts.uk.ctx.at.function.dom.dailyattendanceitem;

import lombok.Data;

@Data
public class DailyAttendanceItem {

	private int attendanceItemId;

	private String attendanceItemName;

	private int attendanceItemDisplayNumber;

	private Integer typeOfAttendanceItem;

	private Integer frameCategory;
	
	private String displayName;

	public DailyAttendanceItem() {
		super();
	}

}
