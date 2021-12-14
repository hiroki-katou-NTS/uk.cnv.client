package nts.uk.ctx.at.shared.dom.common.timerounding;

import org.junit.Test;

import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;

public class UnitRoundTest {
	
	@Test
	public void roundDownOver15(){
		
		val unit = Unit.ROUNDING_TIME_15MIN;
		
		val r1 = unit.roundDownOver(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDownOver(-2 * 60 + 10); /** -1:50*/
		assertThat(r2).isEqualTo(-2 * 60); /** -2:00*/
		
		val r3 = unit.roundDownOver(-2 * 60 + 33); /** -1:27*/
		assertThat(r3).isEqualTo(-2 * 60 + 30); /** -1:30*/
		
		val r4 = unit.roundDownOver(-2 * 60 + 15); /** -1:45*/
		assertThat(r4).isEqualTo(-2 * 60 + 30); /** -1:30*/
		
		val r5 = unit.roundDownOver(-2 * 60 + 30); /** -1:30*/
		assertThat(r5).isEqualTo(-2 * 60 + 30); /** -1:30*/
		
		val r6 = unit.roundDownOver(-2 * 60 + 44); /** -1:16*/
		assertThat(r6).isEqualTo(-2 * 60 + 30); /** -1:30*/
		 
		val r7 = unit.roundDownOver(-2 * 60 + 45); /** -1:15*/
		assertThat(r7).isEqualTo(-1 * 60); /** -1:00*/
		 
		val r8 = unit.roundDownOver(-1 * 60); /** -1:00*/
		assertThat(r8).isEqualTo(-1 * 60); /** -1:00*/
		
		val r9 = unit.roundDownOver(-1 * 60 + 29); /** -0:31*/
		assertThat(r9).isEqualTo(-1 * 60 + 30); /** -0:30*/
		
		val r11 = unit.roundDownOver(0 * 60); /** 0:00*/
		assertThat(r11).isEqualTo(0 * 60); /** 0:00*/
		
		val r12 = unit.roundDownOver(0 * 60 + 10); /** 0:10*/
		assertThat(r12).isEqualTo(0 * 60); /** 0:00*/
		
		val r13 = unit.roundDownOver(0 * 60 + 15); /** 0:15*/
		assertThat(r13).isEqualTo(0 * 60 + 30); /** 0:30*/
		
		val r14 = unit.roundDownOver(0 * 60 + 22); /** 0:22*/
		assertThat(r14).isEqualTo(0 * 60 + 30); /** 0:30*/
		
		val r15 = unit.roundDownOver(0 * 60 + 30); /** 0:30*/
		assertThat(r15).isEqualTo(0 * 60 + 30); /** 0:30*/
		
		val r16 = unit.roundDownOver(0 * 60 + 44); /** 0:44*/
		assertThat(r16).isEqualTo(0 * 60 + 30); /** 0:30*/
		 
		val r17 = unit.roundDownOver(0 * 60 + 45); /** 0:45*/
		assertThat(r17).isEqualTo(1 * 60); /** 1:00*/
		 
		val r18 = unit.roundDownOver(1 * 60); /** 1:00*/
		assertThat(r18).isEqualTo(1 * 60); /** 1:00*/
		
		val r19 = unit.roundDownOver(1 * 60 + 45); /** 1:45*/
		assertThat(r19).isEqualTo(2 * 60); /** 2:00*/
		
		val r20 = unit.roundDownOver(1 * 60 + 22); /** 1:22*/
		assertThat(r20).isEqualTo(1 * 60 + 30); /** 1:30*/
	}
	
	@Test
	public void roundDownOver30(){
		
		val unit = Unit.ROUNDING_TIME_30MIN;
		
		val r1 = unit.roundDownOver(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDownOver(-2 * 60 + 10); /** -1:50*/
		assertThat(r2).isEqualTo(-2 * 60); /** -2:00*/
		
		val r3 = unit.roundDownOver(-2 * 60 + 33); /** -1:27*/
		assertThat(r3).isEqualTo(-1 * 60); /** -1:00*/
		
		val r4 = unit.roundDownOver(-2 * 60 + 15); /** -1:45*/
		assertThat(r4).isEqualTo(-2 * 60); /** -2:00*/
		
		val r5 = unit.roundDownOver(-2 * 60 + 30); /** -1:30*/
		assertThat(r5).isEqualTo(-1 * 60); /** -1:00*/
		
		val r6 = unit.roundDownOver(-2 * 60 + 44); /** -1:16*/
		assertThat(r6).isEqualTo(-1 * 60); /** -1:00*/
		 
		val r7 = unit.roundDownOver(-2 * 60 + 45); /** -1:15*/
		assertThat(r7).isEqualTo(-1 * 60); /** -1:00*/
		 
		val r8 = unit.roundDownOver(-1 * 60); /** -1:00*/
		assertThat(r8).isEqualTo(-1 * 60); /** -1:00*/
		
		val r9 = unit.roundDownOver(-1 * 60 + 29); /** -0:31*/
		assertThat(r9).isEqualTo(-1 * 60); /** -1:00*/
		
		val r11 = unit.roundDownOver(0 * 60); /** 0:00*/
		assertThat(r11).isEqualTo(0 * 60); /** 0:00*/
		
		val r12 = unit.roundDownOver(0 * 60 + 10); /** 0:10*/
		assertThat(r12).isEqualTo(0 * 60); /** 0:00*/
		
		val r13 = unit.roundDownOver(0 * 60 + 15); /** 0:15*/
		assertThat(r13).isEqualTo(0 * 60); /** 0:00*/
		
		val r14 = unit.roundDownOver(0 * 60 + 22); /** 0:22*/
		assertThat(r14).isEqualTo(0 * 60); /** 0:00*/
		
		val r15 = unit.roundDownOver(0 * 60 + 30); /** 0:30*/
		assertThat(r15).isEqualTo(1 * 60); /** 1:00*/
		
		val r16 = unit.roundDownOver(0 * 60 + 44); /** 0:44*/
		assertThat(r16).isEqualTo(1 * 60); /** 1:00*/
		 
		val r17 = unit.roundDownOver(0 * 60 + 45); /** 0:45*/
		assertThat(r17).isEqualTo(1 * 60); /** 1:00*/
		 
		val r18 = unit.roundDownOver(1 * 60); /** 1:00*/
		assertThat(r18).isEqualTo(1 * 60); /** 1:00*/
		
		val r19 = unit.roundDownOver(1 * 60 + 44); /** 1:44*/
		assertThat(r19).isEqualTo(2 * 60); /** 2:00*/
		
		val r20 = unit.roundDownOver(1 * 60 + 22); /** 1:22*/
		assertThat(r20).isEqualTo(1 * 60); /** 1:00*/
	}
	
	@Test
	public void roundDown5() {
		
		val unit = Unit.ROUNDING_TIME_5MIN;
		
		val r1 = unit.roundDown(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDown(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 10); /** -1:50*/
		
		val r3 = unit.roundDown(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-2 * 60); /** -2:00*/
		
		val r4 = unit.roundDown(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundDown(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 10); /** 2:10*/
		
		val r6 = unit.roundDown(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60); /** 2:00*/
	}
	
	@Test
	public void roundUp5() {
		
		val unit = Unit.ROUNDING_TIME_5MIN;
		
		val r1 = unit.roundUp(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundUp(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 15); /** -1:45*/
		
		val r3 = unit.roundUp(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-2 * 60 + 5); /** -2:55*/
		
		val r4 = unit.roundUp(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundUp(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 15); /** 2:15*/
		
		val r6 = unit.roundUp(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60 + 5); /** 2:05*/
	}
	
	@Test
	public void roundDown6() {
		
		val unit = Unit.ROUNDING_TIME_6MIN;
		
		val r1 = unit.roundDown(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDown(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 12); /** -1:48*/
		
		val r3 = unit.roundDown(-2 * 60 + 8); /** -1:52*/
		assertThat(r3).isEqualTo(-2 * 60 + 6); /** -1:54*/
		
		val r4 = unit.roundDown(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundDown(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 12); /** 2:12*/
		
		val r6 = unit.roundDown(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60); /** 2:00*/
	}
	
	@Test
	public void roundUp6() {
		
		val unit = Unit.ROUNDING_TIME_6MIN;
		
		val r1 = unit.roundUp(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundUp(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 12); /** -1:48*/
		
		val r3 = unit.roundUp(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-2 * 60 + 6); /** -2:54*/
		
		val r4 = unit.roundUp(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundUp(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 12); /** 2:12*/
		
		val r6 = unit.roundUp(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60 + 6); /** 2:06*/
	}
	
	@Test
	public void roundDown10() {
		
		val unit = Unit.ROUNDING_TIME_10MIN;
		
		val r1 = unit.roundDown(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDown(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 10); /** -1:50*/
		
		val r3 = unit.roundDown(-2 * 60 + 8); /** -1:52*/
		assertThat(r3).isEqualTo(-2 * 60); /** -2:00*/
		
		val r4 = unit.roundDown(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundDown(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 10); /** 2:10*/
		
		val r6 = unit.roundDown(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60); /** 2:00*/
	}
	
	@Test
	public void roundUp10() {
		
		val unit = Unit.ROUNDING_TIME_10MIN;
		
		val r1 = unit.roundUp(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundUp(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 20); /** -1:40*/
		
		val r3 = unit.roundUp(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-2 * 60 + 10); /** -2:50*/
		
		val r4 = unit.roundUp(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundUp(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 20); /** 2:20*/
		
		val r6 = unit.roundUp(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60 + 10); /** 2:10*/
	}
	
	@Test
	public void roundDown15() {
		
		val unit = Unit.ROUNDING_TIME_15MIN;
		
		val r1 = unit.roundDown(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDown(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60); /** -2:00*/
		
		val r3 = unit.roundDown(-2 * 60 + 8); /** -1:52*/
		assertThat(r3).isEqualTo(-2 * 60); /** -2:00*/
		
		val r4 = unit.roundDown(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundDown(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60); /** 2:00*/
		
		val r6 = unit.roundDown(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60); /** 2:00*/
	}
	
	@Test
	public void roundUp15() {
		
		val unit = Unit.ROUNDING_TIME_15MIN;
		
		val r1 = unit.roundUp(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundUp(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 15); /** -1:45*/
		
		val r3 = unit.roundUp(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-2 * 60 + 15); /** -2:45*/
		
		val r4 = unit.roundUp(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundUp(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 15); /** 2:15*/
		
		val r6 = unit.roundUp(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60 + 15); /** 2:15*/
	}
	
	@Test
	public void roundDown20() {
		
		val unit = Unit.ROUNDING_TIME_20MIN;
		
		val r1 = unit.roundDown(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDown(-2 * 60 + 22); /** -1:38*/
		assertThat(r2).isEqualTo(-2 * 60 + 20); /** -1:40*/
		
		val r3 = unit.roundDown(-2 * 60 + 8); /** -1:52*/
		assertThat(r3).isEqualTo(-2 * 60); /** -2:00*/
		
		val r4 = unit.roundDown(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundDown(2 * 60 + 22); /** 2:22*/
		assertThat(r5).isEqualTo(2 * 60 + 20); /** 2:00*/
		
		val r6 = unit.roundDown(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60); /** 2:00*/
	}
	
	@Test
	public void roundUp20() {
		
		val unit = Unit.ROUNDING_TIME_20MIN;
		
		val r1 = unit.roundUp(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundUp(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 20); /** -1:40*/
		
		val r3 = unit.roundUp(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-2 * 60 + 20); /** -2:40*/
		
		val r4 = unit.roundUp(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundUp(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 20); /** 2:20*/
		
		val r6 = unit.roundUp(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60 + 20); /** 2:20*/
	}
	
	@Test
	public void roundDown30() {
		
		val unit = Unit.ROUNDING_TIME_30MIN;
		
		val r1 = unit.roundDown(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDown(-2 * 60 + 32); /** -1:28*/
		assertThat(r2).isEqualTo(-2 * 60 + 30); /** -1:30*/
		
		val r3 = unit.roundDown(-2 * 60 + 8); /** -1:52*/
		assertThat(r3).isEqualTo(-2 * 60); /** -2:00*/
		
		val r4 = unit.roundDown(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundDown(2 * 60 + 33); /** 2:33*/
		assertThat(r5).isEqualTo(2 * 60 + 30); /** 2:00*/
		
		val r6 = unit.roundDown(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60); /** 2:00*/
	}
	
	@Test
	public void roundUp30() {
		
		val unit = Unit.ROUNDING_TIME_30MIN;
		
		val r1 = unit.roundUp(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundUp(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-2 * 60 + 30); /** -1:30*/
		
		val r3 = unit.roundUp(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-2 * 60 + 30); /** -1:30*/
		
		val r4 = unit.roundUp(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundUp(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(2 * 60 + 30); /** 2:30*/
		
		val r6 = unit.roundUp(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60 + 30); /** 2:30*/
	}
	
	@Test
	public void roundDown60() {
		
		val unit = Unit.ROUNDING_TIME_60MIN;
		
		val r1 = unit.roundDown(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundDown(-2 * 60 + 32); /** -1:28*/
		assertThat(r2).isEqualTo(-2 * 60); /** -2:00*/
		
		val r3 = unit.roundDown(-2 * 60 + 8); /** -1:52*/
		assertThat(r3).isEqualTo(-2 * 60); /** -2:00*/
		
		val r4 = unit.roundDown(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundDown(2 * 60 + 33); /** 2:33*/
		assertThat(r5).isEqualTo(2 * 60); /** 2:00*/
		
		val r6 = unit.roundDown(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(2 * 60); /** 2:00*/
	}
	
	@Test
	public void roundUp60() {
		
		val unit = Unit.ROUNDING_TIME_60MIN;
		
		val r1 = unit.roundUp(-2 * 60); /** -2:00*/
		assertThat(r1).isEqualTo(-2 * 60); /** -2:00*/
		
		val r2 = unit.roundUp(-2 * 60 + 12); /** -1:48*/
		assertThat(r2).isEqualTo(-1 * 60); /** -1:00*/
		
		val r3 = unit.roundUp(-2 * 60 + 3); /** -1:57*/
		assertThat(r3).isEqualTo(-1 * 60); /** -1:00*/
		
		val r4 = unit.roundUp(2 * 60); /** 2:00*/
		assertThat(r4).isEqualTo(2 * 60); /** 2:00*/
		
		val r5 = unit.roundUp(2 * 60 + 12); /** 2:12*/
		assertThat(r5).isEqualTo(3 * 60); /** 2:30*/
		
		val r6 = unit.roundUp(2 * 60 + 3); /** 2:03*/
		assertThat(r6).isEqualTo(3 * 60); /** 2:30*/
	}
}
