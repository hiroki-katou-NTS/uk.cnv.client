package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

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
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.GetWorkScheduleByScheduleManagementService.Require;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.employeeworkway.WorkingStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;

@RunWith(JMockit.class)
public class GetWorkScheduleByScheduleManagementServiceTest {

	@Injectable
	private Require require;

	/**
	 * $社員の就業状態.勤務予定が必要か() is false
	 */
	@Test
	public void testGetScheduleManagement() {

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

		Map<EmployeeWorkingStatus, Optional<WorkSchedule>> data =
				GetWorkScheduleByScheduleManagementService.getScheduleManagement(require, lstEmployeeID, period);
		assertThat(data.entrySet())
				.extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(status, Optional.empty()));

	}

	/**
	 * $社員の就業状態.勤務予定が必要か() is true
	 * require.勤務予定を取得する( 社員ID, $ ) is empty
	 */
	@Test
	public void testGetScheduleManagement_1() {

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
				require.get(anyString, GeneralDate.today());
			}
		};

		Map<EmployeeWorkingStatus, Optional<WorkSchedule>> data =
				GetWorkScheduleByScheduleManagementService.getScheduleManagement(require, lstEmployeeID, period);
		assertThat(data.entrySet())
				.extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(status, Optional.empty()));

	}

	/**
	 * $社員の就業状態.勤務予定が必要か() is true
	 * require.勤務予定を取得する( 社員ID, $ ) not empty
	 */
	@Test
	public void testGetScheduleManagement_2() {

		List<String> lstEmployeeID = Arrays.asList("emp1");
		DatePeriod period = new DatePeriod(GeneralDate.today(), GeneralDate.today());
		WorkSchedule workSchedule = new WorkSchedule("employeeID",
				GeneralDate.today(), ConfirmedATR.CONFIRMED, null, null, new BreakTimeOfDailyAttd(),
				new ArrayList<>(),
				TaskSchedule.createWithEmptyList(),
				Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty());

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
				require.get(anyString, GeneralDate.today());
				result = Optional.of(workSchedule);
			}
		};

		Map<EmployeeWorkingStatus, Optional<WorkSchedule>> data =
				GetWorkScheduleByScheduleManagementService.getScheduleManagement(require, lstEmployeeID, period);
		assertThat(data.entrySet())
				.extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(status, Optional.of(workSchedule)));

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
