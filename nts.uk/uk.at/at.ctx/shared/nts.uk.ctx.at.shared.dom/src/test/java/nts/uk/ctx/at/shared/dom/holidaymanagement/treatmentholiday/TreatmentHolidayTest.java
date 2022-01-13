package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.days.FourWeekDays;
import nts.uk.ctx.at.shared.dom.common.days.WeeklyDays;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.calendar.MonthDay;

@RunWith(JMockit.class)
public class TreatmentHolidayTest {

	@Test
	public void testGetter() {
		WeeklyHolidayAcqMana weeklyHolidayAcqMana = new WeeklyHolidayAcqMana(new WeeklyDays(1.0));
		TreatmentHoliday treatmentHoliday = new TreatmentHoliday("companyId", NotUseAtr.NOT_USE, weeklyHolidayAcqMana);
		NtsAssert.invokeGetters(treatmentHoliday);
	}

	/**
	 * if 休日取得管理  == 週の管理
	 * @author lan_lt
	 *
	 */
	public static class WeeklyHolidayManageTest {

		@Injectable private TreatmentHoliday.Require require;

		private WeeklyHolidayAcqMana weeklyHolidayAcqMana;

		private TreatmentHoliday treatmentHoliday;

		private WeekRuleManagement weekRuleManagemen;

		@Before
		public void initData() {

			this.weeklyHolidayAcqMana = new WeeklyHolidayAcqMana(new WeeklyDays(1.0));

			this.treatmentHoliday = new TreatmentHoliday("cid", NotUseAtr.NOT_USE, this.weeklyHolidayAcqMana);

			this.weekRuleManagemen = WeekRuleManagement.of("cid", DayOfWeek.MONDAY);

			new Expectations() {
				{
					require.find();
					result = weekRuleManagemen;
				}
			};
		}

		/**
		 * 休日取得数と管理期間を取得する
		 */
		@Test
		public void test_getNumberHoliday() {
			val result = treatmentHoliday.getNumberHoliday(require, GeneralDate.ymd(2020, 11, 25));

			assertThat(result.getAddNonstatutoryHolidays()).isEqualTo(treatmentHoliday.getAddNonstatutoryHolidays());
			assertThat(result.getPeriod())
					.isEqualTo(new DatePeriod(GeneralDate.ymd(2020, 11, 23), GeneralDate.ymd(2020, 11, 29)));
			assertThat(result.getHolidayDays().v()).isEqualTo(weeklyHolidayAcqMana.getWeeklyDays().v());
		}

	}

	/**
	 * if 休日取得管理  == 月日起算の休日取得管理
	 * @author lan_lt
	 *
	 */
	public static class HolidayManageByMDTest {

		@Injectable private TreatmentHoliday.Require require;

		private HolidayAcqManageByMD holidayAcqManageByMD;

		private TreatmentHoliday treatmentHoliday;

		@Before
		public void initData() {
			this.holidayAcqManageByMD = new HolidayAcqManageByMD(new MonthDay(1, 1), new FourWeekDays(4.0), new WeeklyDays(1.0));
			this.treatmentHoliday = new TreatmentHoliday("companyId", NotUseAtr.NOT_USE, this.holidayAcqManageByMD);
		}

		/**
		 * 休日取得数と管理期間を取得する
		 */
		@Test
		public void test_getNumberHoliday() {
			val result = this.treatmentHoliday.getNumberHoliday(require, GeneralDate.ymd(2021, 1, 28));

			assertThat( result.getAddNonstatutoryHolidays()).isEqualTo(this.treatmentHoliday.getAddNonstatutoryHolidays());
			assertThat( result.getPeriod()).isEqualTo(new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 28)));
			assertThat( result.getHolidayDays().v()).isEqualTo(4.0);
		}

	}

	/**
	 * if 休日取得管理  == 年月日起算の休日取得管理
	 * @author lan_lt
	 *
	 */
	public static class HolidayManageByYMDTest {

		@Injectable private TreatmentHoliday.Require require;

		private HolidayAcqManageByYMD holidayManageByYMD;

		private TreatmentHoliday treatmentHoliday;

		@Before
		public void initData() {
			this.holidayManageByYMD = new HolidayAcqManageByYMD(GeneralDate.ymd(2021, 1, 1), new FourWeekDays(4.0));
			this.treatmentHoliday = new TreatmentHoliday("companyId", NotUseAtr.USE, holidayManageByYMD);
		}

		/**
		 *  休日取得数と管理期間を取得する
		 */
		@Test
		public void test_getNumberHoliday() {
			val result = treatmentHoliday.getNumberHoliday(require, GeneralDate.ymd(2021, 1, 10));

			assertThat( result.getAddNonstatutoryHolidays()).isEqualTo(this.treatmentHoliday.getAddNonstatutoryHolidays());
			assertThat( result.getPeriod()).isEqualTo(new DatePeriod(GeneralDate.ymd(2021, 1, 1), GeneralDate.ymd(2021, 1, 28)));
			assertThat( result.getHolidayDays()).isEqualTo(this.holidayManageByYMD.getFourWeekHoliday());
		}

	}

}
