/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktype;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class DailyWork.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyWork extends DomainObject { // 1日の勤務

	/** The work type unit. */
	// 勤務区分
	private WorkTypeUnit workTypeUnit;

	/** The one day. */
	// 1日
	private WorkTypeClassification oneDay;

	/** The morning. */
	// 午前
	private WorkTypeClassification morning; 

	/** The afternoon. */
	private WorkTypeClassification afternoon; // 午後
	
	/**
	 * 出勤区分区分の取得
	 * @return 出勤休日区分
	 */
	public AttendanceHolidayAttr getAttendanceHolidayAttr() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			if (this.oneDay.isHoliday()) {
				return AttendanceHolidayAttr.HOLIDAY;
			} else {
				return AttendanceHolidayAttr.FULL_TIME;
			}
		} else {
			if (this.morning.isAttendance() && this.afternoon.isAttendance()) {
				return AttendanceHolidayAttr.FULL_TIME;
			} else if (this.morning.isAttendance() && this.afternoon.isHoliday()) {
				return AttendanceHolidayAttr.MORNING;
			} else if (this.morning.isHoliday() && this.afternoon.isAttendance()) {
				return AttendanceHolidayAttr.AFTERNOON;
			} else {
				return AttendanceHolidayAttr.HOLIDAY;
			}
		}
	}
	
	/**
	 * 平日出勤か判定
	 * @return　平日出勤である
	 */
	public boolean isWeekDayAttendance() {
		if(this.workTypeUnit == WorkTypeUnit.OneDay) {
			return this.oneDay.isWeekDayAttendance();
		}
		else if(this.workTypeUnit == WorkTypeUnit.MonringAndAfternoon) {
			return morning.isWeekDayAttendance() || afternoon.isWeekDayAttendance();
		}
		return false;
	}
	
	/**
	 * 休日出勤であるか判定
	 * @return　休日出勤である
	 */
	public boolean isHolidayWork() {
		return this.oneDay.isHolidayWork();
	}
}
