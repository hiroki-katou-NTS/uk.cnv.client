package nts.uk.ctx.at.function.pub.dailyattendanceitemname;

import lombok.Value;

@Value
public class DailyAttendanceItemPubDto {

	private int attendanceItemId;

	private String attendanceItemName;

	private int attendanceItemDisplayNumber;

	private Integer typeOfAttendanceItem;
	
	private Integer frameCategory;

	private String displayName;
}
