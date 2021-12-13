package nts.uk.ctx.at.shared.dom.workingcondition;

import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceDayAttr;
import nts.uk.ctx.at.shared.dom.worktype.HolidayAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

/**
 * 
 * @author HieuLT
 *
 */
/** 個人勤務日区分別勤務 **/
@Getter
public class WorkByIndividualWorkDay extends DomainObject{

	/** 勤務時間帯 -- 個人勤務日区分別勤務時間  - update : chuyển PersonalDayOfWeek vào trong đây*/
	private PersonalWorkCategory workTime;
	/** 勤務種類--- 個人勤務日区分別勤務種類  -- tao moi**/
	private WorkTypeByIndividualWorkDay workType;

	public WorkByIndividualWorkDay(PersonalWorkCategory workTime, WorkTypeByIndividualWorkDay workType) {
		super();
		this.workTime = workTime;
		this.workType = workType;
	}

	/**
	 * [1] 該当する曜日の勤務情報を取得する
	 * 
	 * @param date
	 * @return
	 */
	public WorkInformation getWorkInformationDayOfTheWeek(GeneralDate date) {
		// val 曜日 ＝ 年月日。曜日；
		int dayOfWeek = date.dayOfWeek();
		// val 就業時間帯コード ＝ ＠勤務時間帯。曜日別。該当の単一日勤務予定を取得する（曜日）：
		// map $。就業時間帯コード WorkTimeCode
		Optional<SingleDaySchedule> data = this.workTime.getDayOfWeek().getSingleDaySchedule(date);
		if (data.isPresent() && data.get().getWorkTimeCode().isPresent()) {
			return new WorkInformation(workType.getWeekdayTimeWTypeCode(),
					data.get().getWorkTimeCode().get());

		} else {
			return new WorkInformation(workType.getHolidayTimeWTypeCode(), null);
		}
	}

	/**
	 * [2] 休日の勤務情報を取得する
	 * 
	 * @return
	 */
	public WorkInformation getHolidayWorkInformation() {

		//return new 勤務情報（＠勤務種類。休日時、Optinal。empty()）
		return new WorkInformation(workType.getHolidayTimeWTypeCode(), null);

	}

	/**
	 * [3] 出勤日の勤務情報を取得する
	 * 
	 * @return
	 */
	public WorkInformation getWorkInformationWorkDay() {

		// return new 勤務情報（＠勤務種類。出勤時、＠勤務時間帯。平日時。就業時間帯コード）
		return new WorkInformation(workType.getWeekdayTimeWTypeCode(), workTime.getWeekdayTime().getWorkTimeCode().get());

	}
	/**
	 * [4] 休出時の勤務情報を取得する
	 */
	public WorkInformation getWorkinfoOnVacation(WorkType workType) {
		 WorkTypeCode workTypeCode = null;
		 Optional<WorkTimeCode> workTimeCode = Optional.empty();

		// 休日設定を確認する
		Optional<HolidayAtr> holidayAtr = workType.getHolidayAtr();
		if (!holidayAtr.isPresent())
			return null;
		
		switch (holidayAtr.get()) {
		case STATUTORY_HOLIDAYS: /** 法定内休日 */
			// 法内休出時
			if (this.workType != null) {
				if (this.workType.getInLawBreakTimeWTypeCode().isPresent()) {
					workTypeCode = this.workType.getInLawBreakTimeWTypeCode().get();

					if (this.workTime != null) {
						workTimeCode = this.workTime.getHolidayWork() == null ? Optional.empty(): this.workTime.getHolidayWork().getWorkTimeCode();
					}
				} else {
					return new WorkInformation(workTypeCode, inOtherCase().orElse(null));
				}
			}
			break;
		case NON_STATUTORY_HOLIDAYS:/** 法定外休日 */
			// 法外休出時
			if (this.workType != null) {
				if (this.workType.getOutsideLawBreakTimeWTypeCode().isPresent()) {
					workTypeCode = this.workType.getOutsideLawBreakTimeWTypeCode().get();

					if (this.workTime != null) {
						workTimeCode = this.workTime.getHolidayWork() == null ? Optional.empty() : this.workTime.getHolidayWork().getWorkTimeCode();
					}
				} else {
					return new WorkInformation(workTypeCode, inOtherCase().orElse(null));
				}
			}
			break;
		case PUBLIC_HOLIDAY:/** 祝日 */
			// 祝日休出時
			if (this.workType != null) {
				if (this.workType.getHolidayAttendanceTimeWTypeCode().isPresent()) {
					workTypeCode = this.workType.getHolidayAttendanceTimeWTypeCode().get();

					if (this.workTime != null) {
						workTimeCode = this.workTime.getHolidayWork() == null ? Optional.empty() : this.workTime.getHolidayWork().getWorkTimeCode();
					}
				} else {
					return new WorkInformation(workTypeCode, inOtherCase().orElse(null));
				}
			}
			break;

		default:
			break;
		}

		return new WorkInformation(workTypeCode == null ? null : workTypeCode.v(),
                				   workTimeCode.isPresent() ? workTimeCode.get().v() : null);
	}
	
	private Optional<WorkTimeCode> inOtherCase() {
		return this.getWorkTime().getHolidayWork().getWorkTimeCode();
	}
	
	// [5] 就業時間帯コードを取得する
	public Optional<WorkInformation> getWorkInfoFromSetting(String companyId, String employeeId,
			GeneralDate date, WorkType workType) {
		
		// 1日半日出勤・1日休日系の判定（休出判定あり）
		AttendanceDayAttr attAtr = workType.chechAttendanceDay();
		if (attAtr == AttendanceDayAttr.HOLIDAY_WORK) {
			// 休出時の勤務情報を取得する
			return Optional.of(this.getWorkinfoOnVacation(workType));
		}

		WorkInformation workInfo = this.getWorkInformationDayOfTheWeek(date);
		if (workInfo.getWorkTimeCodeNotNull().isPresent()) {
			return Optional.of(new WorkInformation(workType.getWorkTypeCode(), workInfo.getWorkTimeCode()));
		}

		return Optional.of(new WorkInformation(workType.getWorkTypeCode(), this.getWorkInformationWorkDay().getWorkTimeCode()));

	}
}
