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

@RunWith(Enclosed.class)
public class HolidayAcqManageByYMDTest {
	
	@Injectable
	private HolidayAcquisitionManagement.Require require;
	
	@Test
	public void testGetter() {
		HolidayAcqManageByYMD holidayAcqManageByYMD = new HolidayAcqManageByYMD(GeneralDate.today(), new FourWeekDays(4.0));
		NtsAssert.invokeGetters(holidayAcqManageByYMD);
	}
	
	@Test
	public void test_getUnitManagementPeriod() {
		HolidayAcqManageByYMD holidayAcqManageByYMD = new HolidayAcqManageByYMD(GeneralDate.today(), new FourWeekDays(4.0));
		HolidayCheckUnit holidayCheckUnit = holidayAcqManageByYMD.getUnitManagementPeriod();
		assertThat(holidayCheckUnit).isEqualTo(HolidayCheckUnit.FOUR_WEEK);
	}
	
	@Test
	public void test_getStartDateType() {
		HolidayAcqManageByYMD holidayAcqManageByYMD = new HolidayAcqManageByYMD(GeneralDate.today(), new FourWeekDays(4.0));
		StartDateClassification result = holidayAcqManageByYMD.getStartDateType();
		
		assertThat(result).isEqualTo(StartDateClassification.SPECIFY_YMD);
	}
	
	@RunWith(Theories.class)
	public static class TestGetMangementPeriod {
		
		@Injectable
		private HolidayAcquisitionManagement.Require require;
		
		@DataPoints
		public static Fixture[] cases = {
			//基準日 < 起算日
			new Fixture(GeneralDate.ymd(2020, 3, 31), GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 28), 4.0),
			
			//基準日 >=  起算日
			new Fixture(GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 28), 4.0),
			new Fixture(GeneralDate.ymd(2021, 4, 28), GeneralDate.ymd(2021, 4, 1), GeneralDate.ymd(2021, 4, 28), 4.0),
			new Fixture(GeneralDate.ymd(2021, 4, 29), GeneralDate.ymd(2021, 4, 29), GeneralDate.ymd(2021, 5, 26), 4.0),
			
			new Fixture(GeneralDate.ymd(2021, 8, 19), GeneralDate.ymd(2021, 8, 19), GeneralDate.ymd(2021, 9, 15), 4.0),
			new Fixture(GeneralDate.ymd(2021, 9, 15), GeneralDate.ymd(2021, 8, 19), GeneralDate.ymd(2021, 9, 15), 4.0),
			
			new Fixture(GeneralDate.ymd(2022, 3, 3), GeneralDate.ymd(2022, 3, 3), GeneralDate.ymd(2022, 3, 30), 4.0),
			new Fixture(GeneralDate.ymd(2022, 3, 30), GeneralDate.ymd(2022, 3, 3), GeneralDate.ymd(2022, 3, 30), 4.0),
			
			new Fixture(GeneralDate.ymd(2022, 4, 1), GeneralDate.ymd(2022, 3, 31), GeneralDate.ymd(2022, 4, 27), 4.0),
		};

		@Theory
		public void test(Fixture caseTest) {
			
			val holidayAcqManageByYMD = new HolidayAcqManageByYMD(	GeneralDate.ymd(2021, 4, 1)//起算日
					,	new FourWeekDays(4.0)
						);
			
			//Act
			HolidayAcqManaPeriod result = holidayAcqManageByYMD.getManagementPeriod( require, caseTest.param );
			
			//Assert
			assertThat( result.getPeriod().start()).isEqualTo( caseTest.expectStartDate );
			assertThat( result.getPeriod().end()).isEqualTo( caseTest.expectEndDate );
			assertThat( result.getHolidayDays().v() ).isEqualTo( caseTest.expectHolidayDays );
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
