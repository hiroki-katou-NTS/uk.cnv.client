package nts.uk.ctx.at.schedule.dom.schedule.alarm.bansamedayholiday;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.alarm.bansamedayholiday.ReferenceCalendar.Require;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.WorkdayDivision;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.daycalendar.CalendarWorkplace;

@RunWith(JMockit.class)
public class RefCalendarWorkplaceTest {
	@Injectable
	private Require require;
	
	@Test
	public void getters() {
		val calendarWorkplace = new ReferenceCalendarWorkplace("DUMMY");
		NtsAssert.invokeGetters(calendarWorkplace);
	}
	
	/**
	 * create Reference Calendar Workplace success
	 */
	@Test
	public void createRefCalendarWorkplace_success() {
		val calendarWorkplace = new ReferenceCalendarWorkplace("DUMMY");
		assertThat(calendarWorkplace.getWorkplaceID()).isEqualTo("DUMMY");
		
	}
	
	/**
	 *  check BusinessDaysCalendarType
	 *  excepted: WORKPLACE
	 */
	@Test
	public void check_BusinessDaysCalendarType_WORKPLACE() {
		val calendarWorkplace = new ReferenceCalendarWorkplace("DUMMY");
		assertThat(calendarWorkplace.getBusinessDaysCalendarType()).isEqualTo(BusinessDaysCalendarType.WORKPLACE);
	}
	
	/**
	 * get WorkdayDivision
	 * excepted： empty
	 */
	@Test
	public void getWorkdayDivisionIsEmpty() {
		val refCalWorkplace = new ReferenceCalendarWorkplace("DUMMY");
		
		new Expectations() {
			{
				require.getCalendarWorkplaceByDay((String) any, (GeneralDate)any);

			}
		};

		assertThat(refCalWorkplace.getWorkdayDivision(require, GeneralDate.today())).isEmpty();
	}
	
	/**
	 * get WorkdayDivision
	 * excepted： WORKINGDAYS
	 */
	@Test
	public void getWorkdayDivision_WORKINGDAYS() {
		val calWorkplace = CalendarWorkplace.createFromJavaType("DUMMY", GeneralDate.today(), WorkdayDivision.WORKINGDAYS.value);
		val refCalWorkplace = new ReferenceCalendarWorkplace("DUMMY");
		
		new Expectations() {
			{
				require.getCalendarWorkplaceByDay((String) any, (GeneralDate)any);
				result = Optional.of(calWorkplace);

			}
		};

		assertThat(refCalWorkplace.getWorkdayDivision(require, calWorkplace.getDate()).get()).isEqualTo(calWorkplace.getWorkDayDivision());
	}
	
	/**
	 * get WorkdayDivision 
	 * excepted： NON_WORKINGDAY_INLAW
	 */
	@Test
	public void getWorkdayDivision_NON_WORKINGDAY_INLAW() {
		val calWorkplace = CalendarWorkplace.createFromJavaType("DUMMY", GeneralDate.today(), WorkdayDivision.NON_WORKINGDAY_INLAW.value);
		val refCalWorkplace = new ReferenceCalendarWorkplace("DUMMY");
		new Expectations() {
			{
				require.getCalendarWorkplaceByDay((String) any, (GeneralDate)any);
				result = Optional.of(calWorkplace);

			}
		};

		assertThat(refCalWorkplace.getWorkdayDivision(require, calWorkplace.getDate()).get()).isEqualTo(calWorkplace.getDate());
	}
	
	
	/**
	 * get WorkdayDivision 
	 * excepted： NON_WORKINGDAY_EXTRALEGAL
	 */
	@Test
	public void getWorkdayDivision_NON_WORKINGDAY_EXTRALEGAL() {
		val calWorkplace = CalendarWorkplace.createFromJavaType("DUMMY", GeneralDate.today(), WorkdayDivision.NON_WORKINGDAY_EXTRALEGAL.value);
		val refCalWorkplace = new ReferenceCalendarWorkplace("DUMMY");
		new Expectations() {
			{
				require.getCalendarWorkplaceByDay((String) any, (GeneralDate)any);
				result = Optional.of(calWorkplace);

			}
		};

		assertThat(refCalWorkplace.getWorkdayDivision(require, calWorkplace.getDate()).get()).isEqualTo(calWorkplace.getDate());
	}

}
