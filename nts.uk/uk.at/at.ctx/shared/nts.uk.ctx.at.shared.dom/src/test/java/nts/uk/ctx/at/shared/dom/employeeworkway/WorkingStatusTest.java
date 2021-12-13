package nts.uk.ctx.at.shared.dom.employeeworkway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.employeeworkway.WorkingStatus;

public class WorkingStatusTest {

	@Test
	public void getters() {
		WorkingStatus scheManaStatus = WorkingStatus.valueOf(1);
		NtsAssert.invokeGetters(scheManaStatus);
	}
	
	@Test
	public void test() {
		WorkingStatus scheManaStatus = WorkingStatus.valueOf(0);
		assertThat(scheManaStatus).isEqualTo(WorkingStatus.NOT_ENROLLED);
		scheManaStatus = WorkingStatus.valueOf(1);
		assertThat(scheManaStatus).isEqualTo(WorkingStatus.INVALID_DATA);
		scheManaStatus = WorkingStatus.valueOf(2);
		assertThat(scheManaStatus).isEqualTo(WorkingStatus.DO_NOT_MANAGE_SCHEDULE);
		scheManaStatus = WorkingStatus.valueOf(3);
		assertThat(scheManaStatus).isEqualTo(WorkingStatus.ON_LEAVE);
		scheManaStatus = WorkingStatus.valueOf(4);
		assertThat(scheManaStatus).isEqualTo(WorkingStatus.CLOSED);
		scheManaStatus = WorkingStatus.valueOf(5);
		assertThat(scheManaStatus).isEqualTo(WorkingStatus.SCHEDULE_MANAGEMENT);
		
	}
	
	@Test
	public void testNeedCreateWorkSchedule() {
		WorkingStatus scheManaStatus = WorkingStatus.valueOf(0);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isFalse();
		scheManaStatus = WorkingStatus.valueOf(1);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isFalse();
		scheManaStatus = WorkingStatus.valueOf(2);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isFalse();
		scheManaStatus = WorkingStatus.valueOf(3);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isTrue();
		scheManaStatus = WorkingStatus.valueOf(4);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isTrue();
		scheManaStatus = WorkingStatus.valueOf(5);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isTrue();
		
	} 

}
