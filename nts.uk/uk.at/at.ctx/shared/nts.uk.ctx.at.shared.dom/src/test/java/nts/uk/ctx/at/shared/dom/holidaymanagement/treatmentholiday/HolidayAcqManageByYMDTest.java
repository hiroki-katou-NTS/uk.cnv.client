package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import mockit.Injectable;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.days.FourWeekDays;

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
	public void test_make4Weeks() {
		HolidayAcqManageByYMD holidayAcqManageByYMD = new HolidayAcqManageByYMD(GeneralDate.today(), new FourWeekDays(4.0));
		DatePeriod datePeriod = holidayAcqManageByYMD.make4Weeks(GeneralDate.ymd(2020, 11, 11), GeneralDate.ymd(2020, 11, 12));
		assertThat(datePeriod).isEqualTo(new DatePeriod(GeneralDate.ymd(2020, 11, 11), GeneralDate.ymd(2020, 12, 8)));
	}
	
	
	@Test
	public void test_getStartDateType() {
		HolidayAcqManageByYMD holidayAcqManageByYMD = new HolidayAcqManageByYMD(GeneralDate.today(), new FourWeekDays(4.0));
		StartDateClassification result = holidayAcqManageByYMD.getStartDateType();
		
		assertThat(result).isEqualTo(StartDateClassification.SPECIFY_YMD);
	}
	
	@Test
	public void test_getManagementPeriod() {
		HolidayAcqManageByYMD holidayAcqManageByYMD = new HolidayAcqManageByYMD(GeneralDate.ymd(2020, 11, 12), new FourWeekDays(4.0));
		HolidayAcqManaPeriod result = holidayAcqManageByYMD.getManagementPeriod(require, GeneralDate.ymd(2020, 11, 11));
		
		assertThat(result.getHolidayDays().v()).isEqualTo(4.0);
		assertThat(result.getPeriod())
				.isEqualTo(new DatePeriod(GeneralDate.ymd(2020, 11, 12), GeneralDate.ymd(2020, 12, 9)));
	}

	/**
	 * 起算月日 = 1/1
	 */
	@Test
	public void test_get28days() {
		HolidayAcqManageByYMD holidayAcqManageByYMD = new HolidayAcqManageByYMD(GeneralDate.ymd(2021, 1, 1), new FourWeekDays(4.0));
		
		/**
		 * ケース1	基準日 = 2021/1/1		期待値：2021/1/1 - 2021/1/28
		 */
		DatePeriod result = holidayAcqManageByYMD.get28Days(require, GeneralDate.ymd(2021, 1, 1));
		assertThat(result.start()).isEqualTo(GeneralDate.ymd(2021, 1, 1));
		assertThat(result.end()).isEqualTo(GeneralDate.ymd(2021, 1, 28));

		/**
		 * ケース2	基準日 = 2021/1/28	期待値：2021/1/1 - 2021/1/28
		 */
		result = holidayAcqManageByYMD.get28Days(require, GeneralDate.ymd(2021, 1, 28));
		assertThat(result.start()).isEqualTo(GeneralDate.ymd(2021, 1, 1));
		assertThat(result.end()).isEqualTo(GeneralDate.ymd(2021, 1, 28));

		/**
		 * ケース3	基準日 = 2021/1/29	期待値：2021/1/29 - 2021/2/25
		 */
		result = holidayAcqManageByYMD.get28Days(require, GeneralDate.ymd(2021, 1, 29));
		assertThat(result.start()).isEqualTo(GeneralDate.ymd(2021, 1, 29));
		assertThat(result.end()).isEqualTo(GeneralDate.ymd(2021, 2, 25));

		/**
		 * ケース4	基準日 = 2022/1/1		期待値：2021/12/31 - 2022/1/27
		 */
		result = holidayAcqManageByYMD.get28Days(require, GeneralDate.ymd(2022, 1, 1));
		assertThat(result.start()).isEqualTo(GeneralDate.ymd(2021, 12, 31));
		assertThat(result.end()).isEqualTo(GeneralDate.ymd(2022, 1, 27));
		
	}
	
}
