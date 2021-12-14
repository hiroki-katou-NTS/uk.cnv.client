package nts.uk.ctx.at.schedule.dom.workschedule.domainservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatus;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.schedule.dom.workschedule.domainservice.WorkScheManaStatusService.Require;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;

@RunWith(JMockit.class)
public class WorkScheManaStatusServiceTest {

	@Injectable
	private Require require;

	/**
	 * $社員の予定管理状態.勤務予定が必要か() is false
	 */
	@Test
	public void testGetScheduleManagement() {
		List<String> lstEmployeeID = Arrays.asList("emp1");
		DatePeriod period = new DatePeriod(GeneralDate.today(), GeneralDate.today());
		
		ScheManaStatuTempo scheManaStatuTempo = new ScheManaStatuTempo("emp1", GeneralDate.today(),
				ScheManaStatus.CLOSED, Optional.empty(), Optional.empty());
		new MockUp<ScheManaStatuTempo>() {
			@Mock
			public ScheManaStatuTempo create(ScheManaStatuTempo.Require require, String employeeID, GeneralDate date) {
				return scheManaStatuTempo;
			}
		};
		
		new MockUp<ScheManaStatus>() {
			@Mock
			public boolean  needCreateWorkSchedule(){
				return false;
			}
		};
		
		Map<ScheManaStatuTempo, Optional<WorkSchedule>> data = WorkScheManaStatusService.getScheduleManagement(require,
				lstEmployeeID, period);
		assertThat(data.entrySet()).extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(scheManaStatuTempo, Optional.empty()));

	}
	
	/**
	 * $社員の予定管理状態.勤務予定が必要か() is true
	 * require.勤務予定を取得する( 社員ID, $ ) is empty
	 */
	@Test
	public void testGetScheduleManagement_1() {
		List<String> lstEmployeeID = Arrays.asList("emp1");
		DatePeriod period = new DatePeriod(GeneralDate.today(), GeneralDate.today());
		
		ScheManaStatuTempo scheManaStatuTempo = new ScheManaStatuTempo("emp1", GeneralDate.today(),
				ScheManaStatus.SCHEDULE_MANAGEMENT, Optional.empty(), Optional.empty());
		new MockUp<ScheManaStatuTempo>() {
			@Mock
			public ScheManaStatuTempo create(ScheManaStatuTempo.Require require, String employeeID, GeneralDate date) {
				return scheManaStatuTempo;
			}
		};
		
		new Expectations() {
			{
				require.get(anyString, GeneralDate.today());
			}
		};
		new MockUp<ScheManaStatus>() {
			@Mock
			public boolean  needCreateWorkSchedule(){
				return true;
			}
		};
		Map<ScheManaStatuTempo, Optional<WorkSchedule>> data = WorkScheManaStatusService.getScheduleManagement(require,
				lstEmployeeID, period);
		assertThat(data.entrySet()).extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(scheManaStatuTempo, Optional.empty()));

	}
	
	/**
	 * $社員の予定管理状態.勤務予定が必要か() is true
	 * require.勤務予定を取得する( 社員ID, $ ) not empty
	 * 
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

		ScheManaStatuTempo scheManaStatuTempo = new ScheManaStatuTempo("emp1", GeneralDate.today(),
				ScheManaStatus.ON_LEAVE, Optional.empty(), Optional.empty());
		new MockUp<ScheManaStatuTempo>() {
			@Mock
			public ScheManaStatuTempo create(ScheManaStatuTempo.Require require, String employeeID, GeneralDate date) {
				return scheManaStatuTempo;
			}
		};
		
		new Expectations() {
			{
				require.get(anyString, GeneralDate.today());
				result = Optional.of(workSchedule);
			}
		};
		new MockUp<ScheManaStatus>() {
			@Mock
			public boolean  needCreateWorkSchedule(){
				return true;
			}
		};
		
		Map<ScheManaStatuTempo, Optional<WorkSchedule>> data = WorkScheManaStatusService.getScheduleManagement(require,
				lstEmployeeID, period);
		assertThat(data.entrySet()).extracting(d -> d.getKey(), d -> d.getValue())
				.containsExactly(tuple(scheManaStatuTempo, Optional.of(workSchedule)));

	}

}
