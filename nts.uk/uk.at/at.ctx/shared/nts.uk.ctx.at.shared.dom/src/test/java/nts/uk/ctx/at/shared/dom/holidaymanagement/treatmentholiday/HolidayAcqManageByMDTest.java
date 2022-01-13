package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import lombok.AllArgsConstructor;
import lombok.val;
import mockit.Injectable;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.days.FourWeekDays;
import nts.uk.ctx.at.shared.dom.common.days.WeeklyDays;
import nts.uk.shr.com.time.calendar.MonthDay;
@RunWith(Enclosed.class)
public class HolidayAcqManageByMDTest {
	
	@Injectable
	private HolidayAcquisitionManagement.Require require;
	

	@Test
	public void testGetter() {
		HolidayAcqManageByMD holidayAcqManageByMD = new HolidayAcqManageByMD(new MonthDay(12, 31), new FourWeekDays(4.0), new WeeklyDays(1.0));
		NtsAssert.invokeGetters(holidayAcqManageByMD);
	}
	
	@Test
	public void test_getUnitManagementPeriod() {
		HolidayAcqManageByMD holidayAcqManageByMD = new HolidayAcqManageByMD(new MonthDay(12, 31), new FourWeekDays(4.0), new WeeklyDays(1.0));
		HolidayCheckUnit holidayCheckUnit = holidayAcqManageByMD.getUnitManagementPeriod();
		assertThat(holidayCheckUnit).isEqualTo(HolidayCheckUnit.FOUR_WEEK);
	}
	
	@RunWith(Theories.class)
	public static class TestGetMangementPeriod {
		
		@Injectable
		private HolidayAcquisitionManagement.Require require;
		
		@DataPoints
		public static Fixture[] cases = {
			//基準日 < 起算日
			new Fixture(GeneralDate.ymd(2019, 4, 1), GeneralDate.ymd(2019, 4, 1), GeneralDate.ymd(2019, 4, 28), 4.0),
			new Fixture(GeneralDate.ymd(2019, 4, 28), GeneralDate.ymd(2019, 4, 1), GeneralDate.ymd(2019, 4, 28), 4.0),
				
			new Fixture(GeneralDate.ymd(2020, 2, 3), GeneralDate.ymd(2020, 2, 3), GeneralDate.ymd(2020, 3, 1), 4.0),
			new Fixture(GeneralDate.ymd(2020, 3, 1), GeneralDate.ymd(2020, 2, 3), GeneralDate.ymd(2020, 3, 1), 4.0),
			
			new Fixture(GeneralDate.ymd(2020, 3, 2), GeneralDate.ymd(2020, 3, 2), GeneralDate.ymd(2020, 3, 31), 5.0),
			new Fixture(GeneralDate.ymd(2020, 3, 31), GeneralDate.ymd(2020, 3, 2), GeneralDate.ymd(2020, 3, 31), 5.0),
			
			//基準日 >=  起算日
			new Fixture(GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 28), 4.0),
			new Fixture(GeneralDate.ymd(2021, 4, 28), GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 28), 4.0),
			new Fixture(GeneralDate.ymd(2021, 4, 29), GeneralDate.ymd(2021, 4, 29), GeneralDate.ymd(2021, 5, 26), 4.0),
			
			new Fixture(GeneralDate.ymd(2022, 2, 3), GeneralDate.ymd(2022, 2, 3), GeneralDate.ymd(2022, 3, 2), 4.0),
			new Fixture(GeneralDate.ymd(2022, 3, 2), GeneralDate.ymd(2022, 2, 3), GeneralDate.ymd(2022, 3, 2), 4.0),
			new Fixture(GeneralDate.ymd(2022, 3, 3), GeneralDate.ymd(2022, 3, 3), GeneralDate.ymd(2022, 3, 31), 5.0),
			new Fixture(GeneralDate.ymd(2022, 3, 31), GeneralDate.ymd(2022, 3, 3), GeneralDate.ymd(2022, 3, 31), 5.0)
		};

		@Theory
		public void test(Fixture caseTest) {
			
			val holidayAcqManageByMD = new HolidayAcqManageByMD(	new MonthDay(4, 1)//起算月日
					,	new FourWeekDays(4.0)//4週間の休日日数
					,	new WeeklyDays(1.0)//最終週の休日日数
					);
			
			//Act
			HolidayAcqManaPeriod result = holidayAcqManageByMD.getManagementPeriod( require, caseTest.param );
			
			//Assert
			assertThat( result.getPeriod().start()).isEqualTo( caseTest.expectStartDate );
			assertThat( result.getPeriod().end()).isEqualTo( caseTest.expectEndDate );
			assertThat( result.getHolidayDays().v()).isEqualTo( caseTest.expectHolidayDays );
		}
		
	}
	
	@AllArgsConstructor
	static class Fixture {
		GeneralDate param;
		GeneralDate expectStartDate;
		GeneralDate expectEndDate;
		Double expectHolidayDays;
	}
	
}
