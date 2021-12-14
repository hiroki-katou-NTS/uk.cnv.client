package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
/**
 * 
 * @author tutk
 *
 */
public class ChangeClockArtTest {
	@Test
	public void getters() {
		ChangeClockArt changeClockArt = ChangeClockArt.valueOf(1);//dummy
		NtsAssert.invokeGetters(changeClockArt);
	}
	@Test
	public void test() {
		ChangeClockArt changeClockArt = ChangeClockArt.valueOf(1);//dummy
		assertThat(changeClockArt).isEqualTo(ChangeClockArt.WORKING_OUT);
		changeClockArt = ChangeClockArt.valueOf(20);
		assertThat(changeClockArt).isEqualTo(null);
	}
	
	/**
	 * changeClockArt == ChangeClockArt.WORKING_OUT is true 
	 * changeClockArt != ChangeClockArt.WORKING_OUT is false
	 */
	@Test
	public void testCheckWorkingOut_1() {
		ChangeClockArt changeClockArt = ChangeClockArt.valueOf(1);
		assertThat(changeClockArt.checkWorkingOut()).isTrue();
		changeClockArt = ChangeClockArt.valueOf(0);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(2);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(3);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(4);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(5);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(6);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(7);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(8);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(9);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(10);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockArt.valueOf(11);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
	}
	
}
