package nts.uk.ctx.at.record.dom.daily.ouen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;


/**
 * 
 * @author chungnt
 *
 */

@RunWith(JMockit.class)
public class CalculateAttendanceTimeBySupportWorkServiceTest {

	@Injectable
	private CalculateAttendanceTimeBySupportWorkService.Require require;

	private String empId = "empId";
	private GeneralDate ymd = GeneralDate.today();
	private List<OuenWorkTimeSheetOfDailyAttendance> ouenWorkTimeSheetOfDailyAttendance = new ArrayList<>();
	private IntegrationOfDaily integrationOfDaily = CalculateAttendanceTimeBySupportWorkServiceHelper
			.getIntegrationOfDaily();

	// Test $日別勤怠 isNotPersent
	@Test
	public void test() {

		new Expectations() {
			{
				require.get(empId, new DatePeriod(ymd, ymd));
			}
		};

		Optional<IntegrationOfDaily> result = CalculateAttendanceTimeBySupportWorkService.calculate(require, empId, ymd,
				ouenWorkTimeSheetOfDailyAttendance);

		assertThat((result).isPresent()).isFalse();
	}

	// Test $日別勤怠 isPersent
	// $計算結果 isNotPersent
	@Test
	public void test1() {

		new Expectations() {
			{
				require.get(empId, new DatePeriod(ymd, ymd));
				result = Optional.of(integrationOfDaily);
				
				require.calculationIntegrationOfDaily(integrationOfDaily, EnumAdaptor.valueOf(0, ExecutionType.class));
			}
		};

		Optional<IntegrationOfDaily> result = CalculateAttendanceTimeBySupportWorkService.calculate(require, empId, ymd,
				ouenWorkTimeSheetOfDailyAttendance);

		assertThat((result).isPresent()).isTrue();
	}

}
