/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktype;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class DailyWork.
 * 1日の勤務
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.就業規則.勤務種類.1日の勤務
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyWork extends DomainObject implements Cloneable, Serializable{ // 1日の勤務

	/** Serializable */
	private static final long serialVersionUID = 1L;

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
	// 午後
	private WorkTypeClassification afternoon;

	public boolean isAnnualOrSpecialHoliday() {
		if(this.workTypeUnit == WorkTypeUnit.OneDay) {
			return isAnnualOrSpecialHoliday(oneDay);
		}
		return isAnnualOrSpecialHoliday(afternoon) || isAnnualOrSpecialHoliday(morning);
	}

	private boolean isAnnualOrSpecialHoliday(WorkTypeClassification halfDay) {
		return halfDay.isAnnualLeave() || halfDay.isSpecialHoliday();
	}
	/**
	 * check leave for a a morning
	 *
	 * @return true leave for morning else false
	 */
	public boolean IsLeaveForMorning() {
		return this.checkLeave(this.morning);
	}

	/**
	 * check leave for a afternoon
	 *
	 * @return true leave for a afternoon else false
	 */
	public boolean IsLeaveForAfternoon() {
		return this.checkLeave(this.afternoon);
	}

	/**
	 * check leave for a day
	 *
	 * @return true leave for a day else false
	 */
	public boolean IsLeaveForADay() {
		return this.checkLeave(this.oneDay);
	}

	/**
	 * check leave by
	 *
	 * @param attribute
	 *            勤務種類の分類
	 * @return
	 */
	private boolean checkLeave(WorkTypeClassification attribute) {
		return WorkTypeClassification.Holiday == attribute || WorkTypeClassification.Pause == attribute
				|| WorkTypeClassification.AnnualHoliday == attribute
				|| WorkTypeClassification.YearlyReserved == attribute
				|| WorkTypeClassification.SpecialHoliday == attribute
				|| WorkTypeClassification.TimeDigestVacation == attribute
				|| WorkTypeClassification.SubstituteHoliday == attribute || WorkTypeClassification.Absence == attribute
				|| WorkTypeClassification.ContinuousWork == attribute || WorkTypeClassification.Closure == attribute
				|| WorkTypeClassification.LeaveOfAbsence == attribute;
	}

	/**
	 * 出勤区分区分の取得
	 *
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

	public boolean isHolidayType() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			if (this.oneDay.isHolidayType()) {
				return true;
			} else {
				return false;
			}
		} else {
			if (this.morning.isHolidayType() && this.afternoon.isHolidayType()) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	/**
	 * 所定時間の取得先を判定する
	 * @return　出勤休出区分
	 */
	public AttendanceHolidayAttr decisionNeedPredTime() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
//			if (this.oneDay.isHoliday()) {
			if (this.oneDay.isHolidayType()) {
				return AttendanceHolidayAttr.HOLIDAY;
			} else {
				return AttendanceHolidayAttr.FULL_TIME;
			}
		} else {
			if (this.morning.isWeekDayAttendance() && this.afternoon.isWeekDayAttendance()) {
				return AttendanceHolidayAttr.FULL_TIME;
//			} else if (this.morning.isWeekDayAttendance() && this.afternoon.isHoliday()) {
			} else if (this.morning.isWeekDayAttendance() && !this.afternoon.isWeekDayAttendance()) {
				return AttendanceHolidayAttr.MORNING;
//			} else if (this.morning.isHoliday() && this.afternoon.isWeekDayAttendance()) {
			} else if (!this.morning.isWeekDayAttendance() && this.afternoon.isWeekDayAttendance()) {
				return AttendanceHolidayAttr.AFTERNOON;
			} else {
				return AttendanceHolidayAttr.HOLIDAY;
			}
		}
	}


	/**
	 * 平日出勤か判定
	 *
	 * @return 平日出勤である
	 */
	public boolean isWeekDayAttendance() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			return this.oneDay.isWeekDayAttendance();
		} else if (this.workTypeUnit == WorkTypeUnit.MonringAndAfternoon) {
			return morning.isWeekDayAttendance() || afternoon.isWeekDayAttendance();
		}
		return false;
	}

	/**
	 * 休日出勤か判定
	 *
	 * @return true：休日出勤である false：休日出勤ではない
	 */
	public boolean isHolidayWork() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			return this.oneDay.isHolidayWork();
		} else if (this.workTypeUnit == WorkTypeUnit.MonringAndAfternoon) {
			return morning.isHolidayWork() || afternoon.isHolidayWork();
		}
		return false;
	}

	/**
	 * 連続勤務か判定
	 *
	 * @return true：連続勤務である false：連続勤務ではない
	 */
	public boolean isContinueWork() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			return this.oneDay.isContinuousWork();
		} else if (this.workTypeUnit == WorkTypeUnit.MonringAndAfternoon) {
			return morning.isContinuousWork() || afternoon.isContinuousWork();
		}
		return false;
	}

	/**
	 * 欠勤であるか判定する
	 * @return true：欠勤である false：欠勤ではない
	 */
	public boolean isAbsence() {
		if (this.workTypeUnit.isOneDay()) {
			return this.oneDay.isAbsence();
		}
		if (this.workTypeUnit.isMonringAndAfternoon()) {
			return morning.isAbsence() || afternoon.isAbsence();
		}
		return false;
	}

	/**
	 * 平日出勤or休出であるか判定する
	 * @return 平日出勤or休出である
	 */
	public boolean isWeekDayOrHolidayWork() {
		return isWeekDayAttendance() || isHolidayWork();
	}

	/**
	 * 1日振休か判定する
	 * @return　1日振休である
	 */
	public boolean isPause() {
		if(this.getWorkTypeUnit().isOneDay()) {
			return this.getOneDay().isPause();
		}
		else if(this.getWorkTypeUnit().isMonringAndAfternoon()) {
			return this.getMorning().isPause() && this.getAfternoon().isPause();
		}
		return false;
	}

	/**
	 * 受け取った勤務種類の分類に一致しているか判定する
	 *
	 * @param workTypeClassification
	 *            勤務種類の分類
	 * @return 何時一致しているか
	 */
	public AttendanceHolidayAttr decisionMatchWorkType(WorkTypeClassification workTypeClassification) {
		if (oneDay.equals(workTypeClassification)) {
			return AttendanceHolidayAttr.FULL_TIME;
		} else if (morning.equals(workTypeClassification)) {
			return AttendanceHolidayAttr.MORNING;
		} else if (afternoon.equals(workTypeClassification)) {
			return AttendanceHolidayAttr.AFTERNOON;
		} else {
			return AttendanceHolidayAttr.HOLIDAY;
		}
	}

	/**
	 * 1日-休業
	 * @return true is closure else false
	 */
	public boolean isClosure() {
		return WorkTypeClassification.Closure.equals(this.oneDay);
	}

	public boolean isOneDay() {
		return this.workTypeUnit == WorkTypeUnit.OneDay;
	}

	public boolean isOneDayHoliday() {
		return this.oneDay == WorkTypeClassification.Holiday;
	}


	/**
	 * 1日年休の場合であるか
	 * @return 1日年休である
	 */
	public boolean isOneOrHalfAnnualHoliday() {
		if(this.workTypeUnit.isOneDay()) {
			return this.getOneDay().isAnnualLeave();
		}
		else {
			return this.getMorning().isAnnualLeave() && this.getAfternoon().isAnnualLeave();
		}
	}


	/**
	 * 1日特別休暇の場合であるか
	 * @return 特別休暇である
	 */
	public boolean isOneOrHalfDaySpecHoliday() {
		if(this.workTypeUnit.isOneDay()) {
			return this.getOneDay().isSpecialHoliday();
		}
		else {
			return this.getMorning().isSpecialHoliday() && this.getAfternoon().isSpecialHoliday();
		}
	}
	
	/**
	 * 特別休暇であるか
	 * @return
	 */
	public boolean isSpecHoliday() {
		if(this.workTypeUnit.isOneDay()) {
			return this.getOneDay().isSpecialHoliday();
		}
		if (this.workTypeUnit.isMonringAndAfternoon()) {
			return this.getMorning().isSpecialHoliday() || this.getAfternoon().isSpecialHoliday();
		}
		return false;
	}

	/**
	 * 積立年休の場合であるか
	 * @return 積立年休である
	 */
	public boolean isOneOrHalfDayYearlyReserved() {
		if(this.workTypeUnit.isOneDay()) {
			return this.getOneDay().isYearlyReserved();
		}
		else {
			return this.getMorning().isYearlyReserved() && this.getAfternoon().isYearlyReserved();
		}
	}

//	public WorkTypeRangeForPred decisionWorkTypeRange() {
//		if(oneDay.isWeekDayAttendance()) {
//			return WorkTypeRangeForPred.ONEDAY;
//		}
//		else if(morning.isWeekDayAttendance() &&(afternoon.isHoliday() || afternoon.isShooting())) {
//			return WorkTypeRangeForPred.MORNING;
//		}
//		else if((morning.isHoliday() || morning.isShooting()) && afternoon.isWeekDayAttendance()) {
//			return WorkTypeRangeForPred.AFTERNOON;
//		}
//		else if(oneDay.isVacation()) {
//			return WorkTypeRangeForPred.ONEDAY;
//		}
//		else if(morning.isVacation() && afternoon.isVacation()) {
//			return WorkTypeRangeForPred.ONEDAY;
//		}
//		else if(morning.isVacation() &&(afternoon.isHoliday()||afternoon.isShooting())) {
//			return WorkTypeRangeForPred.MORNING;
//		}
//		else if((morning.isHoliday()||morning.isShooting()) && morning.isVacation()) {
//			return WorkTypeRangeForPred.AFTERNOON;
//		}
//		return WorkTypeRangeForPred.NOTHING;
//	}

	public WorkTypeRangeForPred decisionWorkTypeRange() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			//1日かつ出勤系or休暇系の場合
			if(oneDay.isWeekDayAttendance()||oneDay.isVacation()) {
				return WorkTypeRangeForPred.ONEDAY;
			}
			else {
				return WorkTypeRangeForPred.NOTHING;
			}
		}
		else {
			//午前（出勤系or休暇系）+午後（出勤系or休暇系）
			if((morning.isWeekDayAttendance()||morning.isVacation()) && (afternoon.isWeekDayAttendance()||afternoon.isVacation())){
				return WorkTypeRangeForPred.ONEDAY;
			}
			//午前（出勤系or休暇系）+午後（休日系）
			else if((morning.isWeekDayAttendance()||morning.isVacation()) && afternoon.judgeHolidayType()) {
				return WorkTypeRangeForPred.MORNING;
			}
			//午前（休日系）+午後（出勤系or休暇系）
			else if(morning.judgeHolidayType() && (afternoon.isWeekDayAttendance()||afternoon.isVacation())) {
				return WorkTypeRangeForPred.AFTERNOON;
			}
			return WorkTypeRangeForPred.NOTHING;
		}
	}


	/**
	 * 1日休日系か判定する
	 * @return
	 */
	public boolean getDecidionAttendanceHolidayAttr() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			if (this.oneDay.isHolidayType()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 振休or休日であるか判定する
	 * @return振休or休日である
	 */
	public boolean isHolidayOrPause() {
		if(this.getWorkTypeUnit().isOneDay() && (this.getOneDay().isHoliday() || this.getOneDay().isPause())) {
			return true;
		}
		if(this.getWorkTypeUnit().isMonringAndAfternoon()
		&&(this.getMorning().isHoliday() || this.getMorning().isPause())
		&&(this.getAfternoon().isPause() || this.getAfternoon().isPause())) {
			return true;
		}
		return false;
	}

	@Override
	public DailyWork clone() {
		DailyWork cloned = new DailyWork();
		try {
			cloned.workTypeUnit = WorkTypeUnit.valueOf(this.workTypeUnit.value);
			cloned.oneDay = WorkTypeClassification.valueOf(this.oneDay.value);
			cloned.morning = WorkTypeClassification.valueOf(this.morning.value);
			cloned.afternoon = WorkTypeClassification.valueOf(this.afternoon.value);
		}
		catch (Exception e){
			throw new RuntimeException("DailyWork clone error.");
		}
		return cloned;
	}

	/**
	 * 1日半日出勤・1日休日系の判定（休出判定あり）
	 * @return 出勤日区分
	 */
	public AttendanceDayAttr chechAttendanceDay() {

		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			// 勤務の単位→1日
			if (this.oneDay.isHolidayType()) {
				// 勤務の分類(1日)＝休日系
				// 出勤日区分→1日休日系
				return AttendanceDayAttr.HOLIDAY;
			} else if (this.oneDay.isHolidayWork()) {
				// 勤務の分類(1日)＝休日出勤
				// 出勤日区分→休出
				return AttendanceDayAttr.HOLIDAY_WORK;
			} else {
				// 勤務の分類(1日)＝休日・休日出勤以外
				// 出勤日区分→1日出勤系
				return AttendanceDayAttr.FULL_TIME;
			}
		} else {
			// 勤務の単位→半日(午前と午後)
			if (this.morning.isHolidayType() && this.afternoon.isHolidayType()) {
				// 勤務の分類(午前)＝休日系 && 勤務の分類(午後)＝休日系
				// 出勤日区分→1日休日系
				return AttendanceDayAttr.HOLIDAY;
			} else if (!this.morning.isHolidayType() && !this.afternoon.isHolidayType()) {
				// 勤務の分類(午前)≠休日系 && 勤務の分類(午後)≠休日系
				// 出勤日区分→1日出勤系
				return AttendanceDayAttr.FULL_TIME;
			} else if (!this.morning.isHolidayType()) {
				// 勤務の分類(午前)≠休日系 && 勤務の分類(午後)＝休日系
				// 出勤日区分→半日出勤系(午前)
				return AttendanceDayAttr.HALF_TIME_AM;
			} else {
				// 勤務の分類(午前)＝休日系 && 勤務の分類(午後)≠休日系
				// 出勤日区分→半日出勤系(午後)
				return AttendanceDayAttr.HALF_TIME_PM;
			}
		}
	}
	
	public WorkTypeClassification getClassification() {
		if (this.workTypeUnit == WorkTypeUnit.OneDay) {
			return this.oneDay;
		}

		if (this.workTypeUnit == WorkTypeUnit.MonringAndAfternoon && !this.morning.isHolidayType()) {
			return this.morning;
		}

		return this.afternoon;
	}
	
	/**
	 * 半日ごとの勤務種類の分類を取得する
	 * @return
	 */
	public HalfDayWorkTypeClassification getHalfDayWorkTypeClassification() {
		
		if ( this.workTypeUnit.isOneDay() ) {
			return HalfDayWorkTypeClassification.createByWholeDay( this.oneDay );
		}
		
		return HalfDayWorkTypeClassification.createByAmAndPm(this.morning, this.afternoon);
	}
	
	/**
	 * [2] 勤務種類からどんな休暇種類を含むか判断する
	 * @return
	 */
	public Holiday determineHolidayByWorkType() {
	    // $休暇種類　＝　休暇種類#初期作成する()   
	    Holiday holiday = new Holiday();
	    
	    // if @勤務区分.1日であるか判定する()　//1日  
	    if (this.workTypeUnit.isOneDay()) {
	        // return $休暇種類.変更する(@1日.どんな休暇種類か判断する()）   
	        holiday.changeValue(this.oneDay.determineHolidayType());
	        return holiday;
	    }
	    
	    // $休暇種類.変更する(@午前.どんな休暇種類か判断する()）
	    holiday.changeValue(this.morning.determineHolidayType());
	    
	    // $休暇種類.変更する(@午後.どんな休暇種類か判断する()）  
	    holiday.changeValue(this.afternoon.determineHolidayType());
	    
	    // return $休暇種類    
	    return holiday;
	}
	
	public WorkTypeClassification getWorkTypeClassification(WorkAtr atr) {
		switch (atr) {
		case Monring:
			return this.morning;
		case Afternoon:
			return this.afternoon;
		default:
			return this.oneDay;
		}
	}
}
