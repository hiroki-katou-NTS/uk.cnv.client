package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
/**
 * 
 * @author tutk
 *
 */
public class ChangeClockArtTest {
	@Test
	public void getters() {
		ChangeClockAtr changeClockArt = ChangeClockAtr.valueOf(1);//dummy
		NtsAssert.invokeGetters(changeClockArt);
	}
	@Test
	public void test() {
		ChangeClockAtr changeClockArt = ChangeClockAtr.valueOf(1);//dummy
		assertThat(changeClockArt).isEqualTo(ChangeClockAtr.WORKING_OUT);
		changeClockArt = ChangeClockAtr.valueOf(20);
		assertThat(changeClockArt).isEqualTo(null);
	}
	
	/**
	 * changeClockArt == ChangeClockArt.WORKING_OUT is true 
	 * changeClockArt != ChangeClockArt.WORKING_OUT is false
	 */
	@Test
	public void testCheckWorkingOut_1() {
		ChangeClockAtr changeClockArt = ChangeClockAtr.valueOf(1);
		assertThat(changeClockArt.checkWorkingOut()).isTrue();
		changeClockArt = ChangeClockAtr.valueOf(0);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(2);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(3);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(4);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(5);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(6);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(7);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(8);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(9);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(10);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
		changeClockArt = ChangeClockAtr.valueOf(11);
		assertThat(changeClockArt.checkWorkingOut()).isFalse();
	}
	
}
