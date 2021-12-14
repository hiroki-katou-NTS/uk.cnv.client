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
	
	/**
	 * input: 週の管理の週開始　= 日曜日
	 */
	@Test
	public void test_get28Days() {
		WeeklyHolidayAcqMana weeklyHolidayAcqMana = new WeeklyHolidayAcqMana( new WeeklyDays(4.0));
		WeekRuleManagement weekRuleManagement = WeekRuleManagement.of("companyId", DayOfWeek.SUNDAY);
		
		new Expectations() {
			{
				require.find();
				result = weekRuleManagement;
			}
		};
		
		DatePeriod result = weeklyHolidayAcqMana.get28Days(require, GeneralDate.ymd(2021, 01, 05));
		assertThat(result.start()).isEqualTo(GeneralDate.ymd(2021, 01, 03));
		assertThat(result.end()).isEqualTo(GeneralDate.ymd(2021, 01, 30));
		
		result = weeklyHolidayAcqMana.get28Days(require, GeneralDate.ymd(2021, 01, 10));
		assertThat(result.start()).isEqualTo(GeneralDate.ymd(2021, 01, 10));
		assertThat(result.end()).isEqualTo(GeneralDate.ymd(2021, 02, 06));
	}

}
