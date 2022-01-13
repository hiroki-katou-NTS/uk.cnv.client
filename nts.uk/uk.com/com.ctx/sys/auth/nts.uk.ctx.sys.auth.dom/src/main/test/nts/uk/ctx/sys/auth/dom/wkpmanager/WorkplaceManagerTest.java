package nts.uk.ctx.sys.auth.dom.wkpmanager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import lombok.val;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public class WorkplaceManagerTest {
	@Test
	public void getters() {
		val workplaceId = "workplaceId";
		val employeeId = "employeeId";
		val period = new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(2030, 12,31));
		val workplaceManager = WorkplaceManager.createNew(workplaceId, employeeId, period);
		NtsAssert.invokeGetters(workplaceManager);
	}
	
	@Test
	public void createNewTest() {
		val workplaceId = "workplaceId";
		val employeeId = "employeeId";
		val period = new DatePeriod(GeneralDate.ymd(2000, 1, 1), GeneralDate.ymd(2030, 12,31));
		val workplaceManager = WorkplaceManager.createNew(workplaceId, employeeId, period);
		
		assertThat(workplaceManager.getWorkplaceId()).isEqualTo(workplaceId);
		assertThat(workplaceManager.getEmployeeId()).isEqualTo(employeeId);
		assertThat(workplaceManager.getHistoryPeriod().start()).isEqualTo(period.start());
		assertThat(workplaceManager.getHistoryPeriod().end()).isEqualTo(period.end());
	}
}
