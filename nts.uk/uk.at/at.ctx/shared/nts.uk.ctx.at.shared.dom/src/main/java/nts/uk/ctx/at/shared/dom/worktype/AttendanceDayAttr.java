package nts.uk.ctx.at.shared.dom.worktype;

import java.util.Optional;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;

/**
 * 出勤日区分
 * @author shuichi_ishida
 */
@AllArgsConstructor
public enum AttendanceDayAttr {

	/** １日休日系 */
	HOLIDAY(0),

	/** 半日出勤系(午前) */
	HALF_TIME_AM(1),

	/** 半日出勤系(午後) */
	HALF_TIME_PM(2),

	/** １日出勤系 */
	FULL_TIME(3),

	/** 休出 */
	HOLIDAY_WORK(4);

	public final int value;


	/**
	 * 休出か
	 *
	 * @return
	 */
	public boolean isHolidayWork() {

		switch( this ) {
			case HOLIDAY_WORK:	// 休出
				return true;
			default:			// 休出 以外
				return false;
		}

	}

	/**
	 * 休日か
	 *
	 * @return
	 */
	public boolean isHoliday() {

		switch( this ) {
			case HOLIDAY:	// 休日系
				return true;
			default:		// 休日系 以外
				return false;
		}
	}

	/**
	 * 午前午後区分に変換
	 *
	 * @return
	 */
	public Optional<AmPmAtr> toAmPmAtr() {

		switch( this  ) {
			case FULL_TIME:		// 1日出勤系
			case HOLIDAY_WORK:	// 休出
				// 午前午後区分→1日
				return Optional.of( AmPmAtr.ONE_DAY );
			case HALF_TIME_AM:	// 午前
				// 午前午後区分→午前
				return Optional.of( AmPmAtr.AM );
			case HALF_TIME_PM:	// 午後
				// 午前午後区分→午後
				return Optional.of( AmPmAtr.PM );
			default:
				return Optional.empty();
		}

	}
	
	public AttendanceHolidayAttr toAttendanceHolidayAttr() {
		switch (this) {
		case FULL_TIME:
		case HOLIDAY_WORK:
			return AttendanceHolidayAttr.FULL_TIME;
		case HOLIDAY:
			return AttendanceHolidayAttr.HOLIDAY;
		case HALF_TIME_AM:
			return AttendanceHolidayAttr.MORNING;
		case HALF_TIME_PM:
			return AttendanceHolidayAttr.AFTERNOON;
		default:
			throw new BusinessException("toAttendanceHolidayAttr error");
		}
	}
}
