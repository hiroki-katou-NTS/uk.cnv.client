package nts.uk.file.at.app.export.dailyschedule.totalsum;

import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;

/**
 * 日数項目
 * Enum value is based on class WorkTypeClassification
 * @author HoangNDH
 *
 */
public enum DayType {
	// 出勤
	ATTENDANCE(0),
	// 休日
	HOLIDAY(1),
	// 休出
	OFF_WORK(2),
	// 年休
	ANNUAL_HOLIDAY(3),
	// 積立年休
	YEARLY_RESERVED(4),
	// 特休
	SPECIAL(5),
	// 欠勤
	ABSENCE(6);
	
	public int value;
	
	private DayType(int value) {
		this.value = value;
	}
	
	public boolean compareToWorkTypeCls(WorkTypeClassification workTypeCls) {
		switch (this) {
		case ATTENDANCE:
			return WorkTypeClassification.Attendance.equals(workTypeCls)
					|| WorkTypeClassification.Shooting.equals(workTypeCls);
		case HOLIDAY:
			return WorkTypeClassification.Holiday.equals(workTypeCls)
					|| WorkTypeClassification.Pause.equals(workTypeCls);
		case OFF_WORK:
			return WorkTypeClassification.HolidayWork.equals(workTypeCls);
		case ANNUAL_HOLIDAY:
			return WorkTypeClassification.AnnualHoliday.equals(workTypeCls);
		case YEARLY_RESERVED:
			return WorkTypeClassification.YearlyReserved.equals(workTypeCls);
		case SPECIAL:
			return WorkTypeClassification.SpecialHoliday.equals(workTypeCls);
		case ABSENCE:
			return WorkTypeClassification.Absence.equals(workTypeCls);
		default:
			return false;
		}
	}
}
