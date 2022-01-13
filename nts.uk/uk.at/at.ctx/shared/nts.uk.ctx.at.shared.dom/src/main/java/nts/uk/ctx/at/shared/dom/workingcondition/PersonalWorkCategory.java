/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.workingcondition;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.arc.time.GeneralDate;

/**
 * The Class PersonalWorkCategory.
 */
// 個人勤務日区分別勤務 --- 個人勤務日区分別勤務時間
@Getter
public class PersonalWorkCategory extends DomainObject {

	/** The weekday time. */
	// 平日時
	private SingleDaySchedule weekdayTime;

	/** The holiday work. */
	// 休日出勤時
	private SingleDaySchedule holidayWork;
	
	//曜日別: 個人曜日別勤務時間
	private PersonalDayOfWeek dayOfWeek;
	/** The in law break time. */
	

	/**
	 * Instantiates a new personal work category.
	 *
	 * @param memento
	 *            the memento
	 */
	public PersonalWorkCategory(PersonalWorkCategoryGetMemento memento) {
		this.holidayWork = memento.getHolidayWork();
		this.weekdayTime = memento.getWeekdayTime();
		this.dayOfWeek = memento.getDayOfWeek();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(PersonalWorkCategorySetMemento memento) {
		memento.setHolidayWork(this.holidayWork);
		memento.setWeekdayTime(this.weekdayTime);
		memento.setDayOfWeek(this.dayOfWeek);
	}

	public PersonalWorkCategory(SingleDaySchedule weekdayTime, SingleDaySchedule holidayWork, PersonalDayOfWeek dayOfWeek) {
		super();
		this.weekdayTime = weekdayTime;
		this.holidayWork = holidayWork;
		this.dayOfWeek = dayOfWeek;
	}

	public PersonalWorkCategory(SingleDaySchedule weekdayTime) {
		this.weekdayTime = weekdayTime;
	}
	
	/**
	 * [1] 曜日別の勤務時間帯を取得する
	 * @param date 年月日
	 * @return 勤務時間帯
	 */
	public List<TimeZone> getWorkingHoursOfDayOfWeek(GeneralDate date) {
		Optional<SingleDaySchedule> dayOfWeek = this.getDayOfWeek().getSingleDaySchedule(date);
		if(!dayOfWeek.isPresent()) {
			//平日時
			return this.getWeekdayTime().getWorkingHours();
		}
		//曜日別
		return dayOfWeek.get().getWorkingHours();
	}
}
