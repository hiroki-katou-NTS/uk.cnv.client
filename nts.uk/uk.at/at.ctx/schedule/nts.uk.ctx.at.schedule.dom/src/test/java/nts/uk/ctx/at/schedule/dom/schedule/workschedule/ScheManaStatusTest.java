package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

public class ScheManaStatusTest {

	@Test
	public void getters() {
		ScheManaStatus scheManaStatus = ScheManaStatus.valueOf(1);
		NtsAssert.invokeGetters(scheManaStatus);
	}
	
	@Test
	public void test() {
		ScheManaStatus scheManaStatus = ScheManaStatus.valueOf(0);
		assertThat(scheManaStatus).isEqualTo(ScheManaStatus.NOT_ENROLLED);
		scheManaStatus = ScheManaStatus.valueOf(1);
		assertThat(scheManaStatus).isEqualTo(ScheManaStatus.INVALID_DATA);
		scheManaStatus = ScheManaStatus.valueOf(2);
		assertThat(scheManaStatus).isEqualTo(ScheManaStatus.DO_NOT_MANAGE_SCHEDULE);
		scheManaStatus = ScheManaStatus.valueOf(3);
		assertThat(scheManaStatus).isEqualTo(ScheManaStatus.ON_LEAVE);
		scheManaStatus = ScheManaStatus.valueOf(4);
		assertThat(scheManaStatus).isEqualTo(ScheManaStatus.CLOSED);
		scheManaStatus = ScheManaStatus.valueOf(5);
		assertThat(scheManaStatus).isEqualTo(ScheManaStatus.SCHEDULE_MANAGEMENT);
		
	}
	
	@Test
	public void testNeedCreateWorkSchedule() {
		ScheManaStatus scheManaStatus = ScheManaStatus.valueOf(0);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isFalse();
		scheManaStatus = ScheManaStatus.valueOf(1);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isFalse();
		scheManaStatus = ScheManaStatus.valueOf(2);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isFalse();
		scheManaStatus = ScheManaStatus.valueOf(3);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isTrue();
		scheManaStatus = ScheManaStatus.valueOf(4);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isTrue();
		scheManaStatus = ScheManaStatus.valueOf(5);
		assertThat(scheManaStatus.needCreateWorkSchedule()).isTrue();
		
	} 

}
