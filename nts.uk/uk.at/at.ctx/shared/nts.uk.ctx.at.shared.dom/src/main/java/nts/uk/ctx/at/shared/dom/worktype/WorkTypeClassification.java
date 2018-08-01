/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktype;

import lombok.AllArgsConstructor;

/**
 * The Enum WorkTypeClassification
 * 勤務種類の分類
 */
@AllArgsConstructor
public enum WorkTypeClassification {

	/**
	 * 出勤
	 */
	Attendance(0, "Enum_WorkTypeClassification_Attendance"),
	/**
	 * 休日
	 */
	Holiday(1, "Enum_WorkTypeClassification_Holiday"),//1
	/**
	 * 年休
	 */
	AnnualHoliday(2, "Enum_WorkTypeClassification_AnnualHoliday"), //2
	/**
	 * 積立年休
	 */
	YearlyReserved(3, "Enum_WorkTypeClassification_YearlyReserved"), //3
	/**
	 * 特別休暇
	 */
	SpecialHoliday(4, "Enum_WorkTypeClassification_SpecialHoliday"), //4
	/**
	 * 欠勤
	 */
	Absence(5, "Enum_WorkTypeClassification_Absence"), //5
	/**
	 * 代休
	 */
	SubstituteHoliday(6, "Enum_WorkTypeClassification_SubstituteHoliday"), //6
	/**
	 * 振出
	 */
	Shooting(7, "Enum_WorkTypeClassification_Shooting"), 
	/**
	 * 振休
	 */
	Pause(8, "Enum_WorkTypeClassification_Pause"), //7
	/**
	 * 時間消化休暇
	 */
	TimeDigestVacation(9, "Enum_WorkTypeClassification_TimeDigestVacation"), //8
	/**
	 * 連続勤務
	 */
	ContinuousWork(10, "Enum_WorkTypeClassification_ContinuousWork"),
	/**
	 * 休日出勤       休出
	 */
	HolidayWork(11, "Enum_WorkTypeClassification_HolidayWork"),
	/**
	 * 休職
	 */
	LeaveOfAbsence(12, "Enum_WorkTypeClassification_LeaveOfAbsence"), 
	/**
	 * 休業
	 */
	Closure(13, "Enum_WorkTypeClassification_Closure");

	/** The value. */
	public final int value;
	public final String nameId;
	
	

	
	/**
	 * 年休であるか判定する
	 * @author ken_takasu
	 * @return
	 */
	public boolean isAnnualLeave() {
		return AnnualHoliday.equals(this);
	}
	
	/**
	 * 積立年休であるか判定する
	 * @author ken_takasu
	 * @return
	 */
	public boolean isYearlyReserved() {
		return YearlyReserved.equals(this);
	}
	
	/**
	 * 特別休暇であるか判定する
	 * @author ken_takasu
	 * @return
	 */
	public boolean isSpecialHoliday() {
		return SpecialHoliday.equals(this);
	}
	
	/**
	 * 代休であるか判定する
	 * @return　代休である
	 */
	public boolean isSubstituteHoliday() {
		return SubstituteHoliday.equals(this);
	}
		/**
	 * 休日であるか判定する
	 * @return　休日である
	 */
	public boolean isHoliday() {
		return Holiday.equals(this);
	}
	/**
	 * 振出であるか判定する
	 * @return　振出である
	 */
	public boolean isShooting() {
		return Shooting.equals(this);
	}
	
	public boolean isAttendance() {
		return !this.isHolidayType();
	}
	
	/**
	 * 出勤系か判定する
	 * @return　平日出勤系である
	 */
	public boolean isWeekDayAttendance() {
		switch (this) {
		case Attendance:
		case Shooting:
		
			return true;
		case HolidayWork:
		case Absence:
		case AnnualHoliday:
		case Closure:
		case ContinuousWork:
		case Holiday:
		case LeaveOfAbsence:
		case Pause:
		case SpecialHoliday:
		case SubstituteHoliday:
		case TimeDigestVacation:
		case YearlyReserved:
			
			return false;
		default:
			throw new RuntimeException("invalid value: " + this);
		}
	}
	
	/**
	 * 休日系か判定する
	 * @return　休日系である
	 */
	public boolean isHolidayType() {
		switch (this) {
		case Absence:
		case AnnualHoliday:
		case Closure:
		case Holiday:
		case LeaveOfAbsence:
		case Pause:
		case SpecialHoliday:
		case SubstituteHoliday:
		case TimeDigestVacation:
		case YearlyReserved:
		case ContinuousWork:
			return true;
			
		case HolidayWork:
		case Attendance:
		case Shooting:
			return false;
			
		default:
			throw new RuntimeException("invalid value: " + this);
		}
	}
	
	/**
	 * 休日出勤(休出系)であるか判定する
	 * @return　休日出勤である
	 */
	public boolean isHolidayWork() {
		return HolidayWork.equals(this);
	}
	/**
	 * 休暇系であるか判定する
	 * @return　休暇系である
	 */
	public boolean isVacation() {
		switch (this) {
		case AnnualHoliday:
		case SpecialHoliday:
		case Absence:
		case SubstituteHoliday:
		case TimeDigestVacation:
			return true;
		case Closure:
		case Holiday:
		case LeaveOfAbsence:
		case Pause:
		case HolidayWork:
		case Attendance:
		case Shooting:
		case YearlyReserved:
		case ContinuousWork:
			return false;
		default:
			throw new RuntimeException("invalid value: " + this);
		}
	}
	

	
}
