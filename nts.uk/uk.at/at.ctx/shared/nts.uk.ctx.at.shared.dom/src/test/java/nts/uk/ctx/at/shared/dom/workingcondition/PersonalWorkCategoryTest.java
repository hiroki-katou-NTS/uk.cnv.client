package nts.uk.ctx.at.shared.dom.workingcondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class PersonalWorkCategoryTest {

	@Test
	public void getters() {
		PersonalWorkCategory target = new PersonalWorkCategory(
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(8, 30), TimeWithDayAttr.hourMinute(17, 30))),
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(8, 30), TimeWithDayAttr.hourMinute(17, 30))),
				Helper.createMondayOnly(new TimeZone(1, TimeWithDayAttr.hourMinute(8, 30), TimeWithDayAttr.hourMinute(17, 30))));
		NtsAssert.invokeGetters(target);
	}
	
	@Test
	public void getWorkingHours_dayOfWeek() {
		PersonalWorkCategory target = new PersonalWorkCategory(
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(8, 00), TimeWithDayAttr.hourMinute(17, 00))), //平日
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(9, 00), TimeWithDayAttr.hourMinute(18, 00))), //休出
				Helper.createMondayOnly(new TimeZone(1, TimeWithDayAttr.hourMinute(10, 00), TimeWithDayAttr.hourMinute(19, 00)))); //曜日別（月曜のみ）
		List<TimeZone> result = target.getWorkingHoursOfDayOfWeek(GeneralDate.ymd(2022, 1, 3)); //月曜
		assertThat(result)
				.extracting(
						t -> t.getCnt(),
						t -> t.getStart().valueAsMinutes(),
						t -> t.getEnd().valueAsMinutes())
				.containsExactly(
						tuple(1, TimeWithDayAttr.hourMinute(10, 00).valueAsMinutes(), TimeWithDayAttr.hourMinute(19, 00).valueAsMinutes())); //曜日別
	}
	
	@Test
	public void getWorkingHours_weekdayTime() {
		PersonalWorkCategory target = new PersonalWorkCategory(
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(8, 00), TimeWithDayAttr.hourMinute(17, 00))), //平日
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(9, 00), TimeWithDayAttr.hourMinute(18, 00))), //休出
				Helper.createMondayOnly(new TimeZone(1, TimeWithDayAttr.hourMinute(10, 00), TimeWithDayAttr.hourMinute(19, 00)))); //曜日別（月曜のみ）
		List<TimeZone> result = target.getWorkingHoursOfDayOfWeek(GeneralDate.ymd(2022, 1, 1)); //土曜
		assertThat(result)
				.extracting(
						t -> t.getCnt(),
						t -> t.getStart().valueAsMinutes(),
						t -> t.getEnd().valueAsMinutes())
				.containsExactly(
						tuple(1, TimeWithDayAttr.hourMinute(8, 00).valueAsMinutes(), TimeWithDayAttr.hourMinute(17, 00).valueAsMinutes())); //平日
	}
	
	@Test
	public void getWorkingHours_dayOfWeekEmpty() {
		PersonalWorkCategory target = new PersonalWorkCategory(
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(8, 00), TimeWithDayAttr.hourMinute(17, 00))), //平日
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(9, 00), TimeWithDayAttr.hourMinute(18, 00))), //休出
				Helper.createAllEmpty()); //曜日別（全てEmpty）
		List<TimeZone> result = target.getWorkingHoursOfDayOfWeek(GeneralDate.ymd(2022, 1, 3)); //月曜
		assertThat(result)
				.extracting(
						t -> t.getCnt(),
						t -> t.getStart().valueAsMinutes(),
						t -> t.getEnd().valueAsMinutes())
				.containsExactly(
						tuple(1, TimeWithDayAttr.hourMinute(8, 00).valueAsMinutes(), TimeWithDayAttr.hourMinute(17, 00).valueAsMinutes())); //平日
	}
	
	@Test
	public void getWorkingHours_weekdayTimeAndDayOfWeekEmpty() {
		PersonalWorkCategory target = new PersonalWorkCategory(
				Helper.createSingleDayEmpty(), //平日（empty）
				Helper.createSingleDay(new TimeZone(1, TimeWithDayAttr.hourMinute(9, 00), TimeWithDayAttr.hourMinute(18, 00))), //休出
				Helper.createAllEmpty()); //曜日別（全てEmpty）
		List<TimeZone> result = target.getWorkingHoursOfDayOfWeek(GeneralDate.ymd(2022, 1, 3)); //月曜
		assertThat(result).isEmpty();
	}
	
	private static class Helper {
		private static SingleDaySchedule createSingleDay(TimeZone timeZone) {
			List<TimeZone> times = new ArrayList<>();
			times.add(timeZone);
			return new SingleDaySchedule(times, Optional.empty());
		}
		
		private static SingleDaySchedule createSingleDayEmpty() {
			return new SingleDaySchedule(new ArrayList<>(), Optional.empty());
		}
		
		private static PersonalDayOfWeek createMondayOnly(TimeZone timeZone) {
			List<TimeZone> times = new ArrayList<>();
			times.add(timeZone);
			return new PersonalDayOfWeek(Optional.of(Helper.createSingleDay(timeZone)),
					Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		}
		
		private static PersonalDayOfWeek createAllEmpty() {
			return new PersonalDayOfWeek(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		}
	}
}
