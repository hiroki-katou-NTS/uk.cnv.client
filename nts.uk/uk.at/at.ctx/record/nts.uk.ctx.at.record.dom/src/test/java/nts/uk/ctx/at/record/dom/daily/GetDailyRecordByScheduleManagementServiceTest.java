package nts.uk.ctx.at.record.dom.daily;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.daily.GetDailyRecordByScheduleManagementService.Require;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.employeeworkway.WorkingStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

@RunWith(JMockit.class)
public class GetDailyRecordByScheduleManagementServiceTest {

	@Injectable
	private Require require;

	/**
	 * $社員の就業状態.勤務予定が必要か() is false
	 */
	@Test
	public void testGet() {

		List<String> lstEmployeeID = Arrays.asList("emp1");
		DatePeriod period = new DatePeriod(GeneralDate.today(), GeneralDate.today());

		val status = Helper.createEmployeeWorkingStatus("emp1", GeneralDate.today(), WorkingStatus.CLOSED);
		new MockUp<EmployeeWorkingStatus>() {
			@Mock
			public EmployeeWorkingStatus create(
						@SuppressWarnings("unused") EmployeeWorkingStatus.Require require
					,	@SuppressWarnings("unused") String employeeID
					,	@SuppressWarnings("unused") GeneralDate date
			) {
				return status;
			}
		};

		Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> data =
				GetDailyRecordByScheduleManagementService.get(require, lstEmployeeID, period);
		assertThat(data.entrySet())
				.extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(status, Optional.empty()));

	}

	/**
	 * $社員の就業状態.勤務予定が必要か() is true
	 * require.日別実績を取得する( 社員ID, $ )	is empty
	 */
	@Test
	public void testGet_1() {

		List<String> lstEmployeeID = Arrays.asList("emp1");
		DatePeriod period = new DatePeriod(GeneralDate.today(), GeneralDate.today());

		val status = Helper.createEmployeeWorkingStatus("emp1", GeneralDate.today(), WorkingStatus.SCHEDULE_MANAGEMENT);
		new MockUp<EmployeeWorkingStatus>() {
			@Mock
			public EmployeeWorkingStatus create(
						@SuppressWarnings("unused") EmployeeWorkingStatus.Require require
					,	@SuppressWarnings("unused") String employeeID
					,	@SuppressWarnings("unused") GeneralDate date
			) {
				return status;
			}
		};

		new Expectations() {
			{
				require.getDailyResults(anyString, GeneralDate.today());
			}
		};

		Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> data =
				GetDailyRecordByScheduleManagementService.get(require, lstEmployeeID, period);
		assertThat(data.entrySet())
				.extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(status, Optional.empty()));

	}

	/**
	 * $社員の就業状態.勤務予定が必要か() is true
	 * require.日別実績を取得する( 社員ID, $ )	is not empty
	 */
	@Test
	public void testGet_2() {

		List<String> lstEmployeeID = Arrays.asList("emp1");
		DatePeriod period = new DatePeriod(GeneralDate.today(), GeneralDate.today());
		IntegrationOfDaily integrationOfDaily = new IntegrationOfDaily("emp1", GeneralDate.today(), null, null, null,
				Optional.empty(), new ArrayList<>(), Optional.empty(), new BreakTimeOfDailyAttd(),
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty(), new ArrayList<>(), Optional.empty(), new ArrayList<>(),new ArrayList<>(),new ArrayList<>(), Optional.empty()) ;

		val status = Helper.createEmployeeWorkingStatus("emp1", GeneralDate.today(), WorkingStatus.SCHEDULE_MANAGEMENT);
		new MockUp<EmployeeWorkingStatus>() {
			@Mock
			public EmployeeWorkingStatus create(
						@SuppressWarnings("unused") EmployeeWorkingStatus.Require require
					,	@SuppressWarnings("unused") String employeeID
					,	@SuppressWarnings("unused") GeneralDate date
			) {
				return status;
			}
		};

		new Expectations() {
			{
				require.getDailyResults(anyString, GeneralDate.today());
				result = Optional.of(integrationOfDaily);
			}
		};

		Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> data =
				GetDailyRecordByScheduleManagementService.get(require, lstEmployeeID, period);
		assertThat(data.entrySet())
				.extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(status, Optional.of(integrationOfDaily)));

	}



	private static class Helper {

		/**
		 * 社員の就業状態を作成する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @param status 予定管理状態
		 * @return
		 */
		public static EmployeeWorkingStatus createEmployeeWorkingStatus(String employeeId, GeneralDate date, WorkingStatus status) {
			return new EmployeeWorkingStatus( employeeId, date, status, Optional.empty(), Optional.empty(), Optional.empty() );
		}

	}

}
