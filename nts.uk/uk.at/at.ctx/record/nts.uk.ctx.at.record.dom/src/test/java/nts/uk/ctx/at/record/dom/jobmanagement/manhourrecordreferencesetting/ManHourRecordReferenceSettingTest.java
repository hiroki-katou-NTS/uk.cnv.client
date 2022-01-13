package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecordreferencesetting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

/**
 * 
 * @author chungnt
 *
 */

@RunWith(JMockit.class)
public class ManHourRecordReferenceSettingTest {

	@Injectable
	private ManHourRecordReferenceSetting.Require require;

	private String employeeId = "employeeId";

	private String cid = "cid";

	private String userId = "userId";

	private GeneralDate date = GeneralDate.today();

	@Test
	public void testCaseDefault() {

		new Expectations() {
			{
				require.getPeriod(employeeId, date);
				result = new DatePeriod(date, date);
			}
		};

		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(0, ElapsedMonths.class), EnumAdaptor.valueOf(0, ReferenceRange.class));

		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);

		assertThat(result).isEqualTo(GeneralDate.today());
	}

	@Test
	public void testCaseOneMonthAgo() {

//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(1, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		new Expectations() {
			{

				require.getPeriod(employeeId, date);
				result = new DatePeriod(date, date);
			}
		};

		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(1, ElapsedMonths.class), EnumAdaptor.valueOf(0, ReferenceRange.class));

		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);

		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-1).month());
	}

	@Test
	public void testCaseTowMonthAgo() {

//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(2, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		new Expectations() {
			{
//				require.get();
//				result = setting;

				require.getPeriod(employeeId, date);
				result = new DatePeriod(date, date);
			}
		};

		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(2, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);

		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-2).month());
	}

	@Test
	public void testCaseThreeMonthAgo() {

//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(3, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		new Expectations() {
			{
//				require.get();
//				result = setting;

				require.getPeriod(employeeId, date);
				result = new DatePeriod(date, date);
			}
		};

		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(3, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);

		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-3).month());
	}

	@Test
	public void testCaseFourMonthAgo() {

//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(4, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		new Expectations() {
			{
//				require.get();
//				result = setting;

				require.getPeriod(employeeId, date);
				result = new DatePeriod(date, date);
			}
		};

		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(4, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);

		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-4).month());
	}

	@Test
	public void testCaseFiveMonthAgo() {

//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(5, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		new Expectations() {
			{
//				require.get();
//				result = setting;

				require.getPeriod(employeeId, date);
				result = new DatePeriod(date, date);
			}
		};

		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(5, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);

		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-5).month());
	}

	@Test
	public void testCaseSixMonthAgo() {

//		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
//				EnumAdaptor.valueOf(6, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		new Expectations() {
			{
//				require.get();
//				result = setting;

				require.getPeriod(employeeId, date);
				result = new DatePeriod(date, date);
			}
		};

		ManHourRecordReferenceSetting periodSetting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(6, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		GeneralDate result = periodSetting.getWorkCorrectionStartDate(require, employeeId);

		assertThat(result.month()).isEqualTo(GeneralDate.today().addMonths(-6).month());
	}

	// return require.getWorkPlace(userId, employeeId, baseDate);
	@Test
	public void Method2_1() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("Key1", "Value1");
		map.put("Key2", "Value2");

		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(0, ElapsedMonths.class),ReferenceRange.SAME_AS_EMPLOYEE_REFERENCE_RANGE_OF_ROLE);

		new Expectations() {
			{
				require.getWorkPlace(userId, employeeId, date);
				result = map;
			}
		};

		Map<String, String> result = setting.getWorkCorrectionStartDate(require, cid, userId, employeeId, date);

		assertThat(result.get("Key1")).isEqualTo("Value1");
		assertThat(result.get("Key2")).isEqualTo("Value2");
	}

	// return require.getByCID(companyId, baseDate);
	@Test
	public void Method2_2() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("Key1", "Value1");
		map.put("Key2", "Value2");

		ManHourRecordReferenceSetting setting = new ManHourRecordReferenceSetting(
				EnumAdaptor.valueOf(0, ElapsedMonths.class), ReferenceRange.ALL_EMPLOYEES);

		new Expectations() {
			{
				require.getByCID(cid, date);
				result = map;
			}
		};

		Map<String, String> result = setting.getWorkCorrectionStartDate(require, cid, userId, employeeId, date);

		assertThat(result.get("Key1")).isEqualTo("Value1");
		assertThat(result.get("Key2")).isEqualTo("Value2");
	}

}
