package nts.uk.ctx.at.record.dom.jobmanagement.workchangeableperiodsetting;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ElapsedMonths;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting.ManHourRecordReferenceSetting;


/**
 * 
 * @author chungnt
 *
 */

//@RunWith(JMockit.class)
public class WorkChangeablePeriodSettingTest {

//	@Injectable
//	private ManHourRecordReferenceSetting.Require require;
//
//	private String employeeId = "employeeId";
//
//	private GeneralDate date = GeneralDate.today();
//
//	@Test
//	public void testCaseDefault() {
//
//		new Expectations() {
//			{
//				require.get();
//
//				require.getPeriod(employeeId, date);
//				result = new DatePeriod(date, date);
//			}
//		};
//
//		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(0, ElapsedMonths.class));
//
//		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);
//
//		assertThat(result).isEqualTo(GeneralDate.today());
//	}
//
//	@Test
//	public void testCaseOneMonthAgo() {
//
//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(EnumAdaptor.valueOf(1, ElapsedMonths.class));
//
//		new Expectations() {
//			{
//				require.get();
//				result = setting;
//
//				require.getPeriod(employeeId, date);
//				result = new DatePeriod(date, date);
//			}
//		};
//
//		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(0, ElapsedMonths.class));
//
//		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);
//
//		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-1).month());
//	}
//
//	@Test
//	public void testCaseTowMonthAgo() {
//
//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(EnumAdaptor.valueOf(2, ElapsedMonths.class));
//
//		new Expectations() {
//			{
//				require.get();
//				result = setting;
//
//				require.getPeriod(employeeId, date);
//				result = new DatePeriod(date, date);
//			}
//		};
//
//		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(0, ElapsedMonths.class));
//
//		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);
//
//		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-2).month());
//	}
//
//	@Test
//	public void testCaseThreeMonthAgo() {
//
//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(EnumAdaptor.valueOf(3, ElapsedMonths.class));
//
//		new Expectations() {
//			{
//				require.get();
//				result = setting;
//
//				require.getPeriod(employeeId, date);
//				result = new DatePeriod(date, date);
//			}
//		};
//
//		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(0, ElapsedMonths.class));
//
//		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);
//
//		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-3).month());
//	}
//
//	@Test
//	public void testCaseFourMonthAgo() {
//
//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(EnumAdaptor.valueOf(4, ElapsedMonths.class));
//
//		new Expectations() {
//			{
//				require.get();
//				result = setting;
//
//				require.getPeriod(employeeId, date);
//				result = new DatePeriod(date, date);
//			}
//		};
//
//		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(0, ElapsedMonths.class));
//
//		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);
//
//		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-4).month());
//	}
//
//	@Test
//	public void testCaseFiveMonthAgo() {
//
//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(EnumAdaptor.valueOf(5, ElapsedMonths.class));
//
//		new Expectations() {
//			{
//				require.get();
//				result = setting;
//
//				require.getPeriod(employeeId, date);
//				result = new DatePeriod(date, date);
//			}
//		};
//
//		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(0, ElapsedMonths.class));
//
//		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);
//
//		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-5).month());
//	}

}
