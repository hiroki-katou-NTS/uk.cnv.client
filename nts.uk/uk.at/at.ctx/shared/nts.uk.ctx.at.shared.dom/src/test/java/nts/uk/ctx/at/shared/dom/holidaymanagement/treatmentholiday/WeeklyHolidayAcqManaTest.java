package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.days.WeeklyDays;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;

@RunWith(JMockit.class)
public class WeeklyHolidayAcqManaTest {
	
	@Injectable
	private HolidayAcquisitionManagement.Require require;

	@Test
	public void testGetter() {
		WeeklyHolidayAcqMana weeklyHolidayAcqMana = new WeeklyHolidayAcqMana( new WeeklyDays(4.0));
		NtsAssert.invokeGetters(weeklyHolidayAcqMana);
	}
	
	@Test
	public void test_getUnitManagementPeriod() {
		WeeklyHolidayAcqMana weeklyHolidayAcqMana = new WeeklyHolidayAcqMana( new WeeklyDays(4.0));
		HolidayCheckUnit result = weeklyHolidayAcqMana.getUnitManagementPeriod();
		assertThat(result).isEqualTo(HolidayCheckUnit.ONE_WEEK);
	}
	
	@Test
	public void test_getManagementPeriod() {
		WeeklyHolidayAcqMana weeklyHolidayAcqMana = new WeeklyHolidayAcqMana( new WeeklyDays(4.0));
		WeekRuleManagement weekRuleManagement = WeekRuleManagement.of("companyId", DayOfWeek.MONDAY);
		new Expectations() {{
			require.find();
			result = weekRuleManagement;
	}};
		HolidayAcqManaPeriod result = weeklyHolidayAcqMana.getManagementPeriod(require, GeneralDate.ymd(2020, 11, 11));
		assertThat(result.getHolidayDays().v()).isEqualTo(weeklyHolidayAcqMana.getWeeklyDays().v());
		assertThat(result.getPeriod()).isEqualTo(new DatePeriod(GeneralDate.ymd(2020, 11, 9), GeneralDate.ymd(2020, 11, 15)));
	}
	
}
