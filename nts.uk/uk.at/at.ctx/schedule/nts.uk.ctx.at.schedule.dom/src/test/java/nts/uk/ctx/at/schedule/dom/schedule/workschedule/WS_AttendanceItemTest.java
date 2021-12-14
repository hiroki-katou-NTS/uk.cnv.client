package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class WS_AttendanceItemTest {

	@Test
	public void testIsBreakTime() {

		// work-type
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartTime1.ID)).isFalse();

		// 1
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime1.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime1.ID)).isTrue();

		// 2
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime2.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime2.ID)).isTrue();

		// 3
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime3.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime3.ID)).isTrue();

		// 4
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime4.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime4.ID)).isTrue();

		// 5
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime5.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime5.ID)).isTrue();

		// 6
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime6.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime6.ID)).isTrue();

		// 7
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime7.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime7.ID)).isTrue();

		// 8
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime8.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime8.ID)).isTrue();

		// 9
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime9.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime9.ID)).isTrue();

		// 10
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.StartBreakTime10.ID)).isTrue();
		assertThat(WS_AttendanceItem.isBreakTime(WS_AttendanceItem.EndBreakTime10.ID)).isTrue();
	}

	@Test
	public void testGetBreakTimeItemWithSize0() {
		List<WS_AttendanceItem> result0 = WS_AttendanceItem.getBreakTimeItemWithSize(0);
		assertThat(result0).isEmpty();
	}

	public void testGetBreakTimeItemWithSize1() {
		List<WS_AttendanceItem> result1 = WS_AttendanceItem.getBreakTimeItemWithSize(1);
		assertThat(result1).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1);
	}

	public void testGetBreakTimeItemWithSize2() {
		List<WS_AttendanceItem> result2 = WS_AttendanceItem.getBreakTimeItemWithSize(2);
		assertThat(result2).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2);
	}

	public void testGetBreakTimeItemWithSize3() {
		List<WS_AttendanceItem> result3 = WS_AttendanceItem.getBreakTimeItemWithSize(3);
		assertThat(result3).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3);
	}

	public void testGetBreakTimeItemWithSize4() {
		List<WS_AttendanceItem> result4 = WS_AttendanceItem.getBreakTimeItemWithSize(4);
		assertThat(result4).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3, 
				WS_AttendanceItem.StartBreakTime4, 
				WS_AttendanceItem.EndBreakTime4);
	}

	public void testGetBreakTimeItemWithSize5() {
		List<WS_AttendanceItem> result5 = WS_AttendanceItem.getBreakTimeItemWithSize(5);
		assertThat(result5).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3, 
				WS_AttendanceItem.StartBreakTime4, 
				WS_AttendanceItem.EndBreakTime4,
				WS_AttendanceItem.StartBreakTime5, 
				WS_AttendanceItem.EndBreakTime5);
	}

	public void testGetBreakTimeItemWithSize6() {
		List<WS_AttendanceItem> result6 = WS_AttendanceItem.getBreakTimeItemWithSize(6);
		assertThat(result6).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3, 
				WS_AttendanceItem.StartBreakTime4, 
				WS_AttendanceItem.EndBreakTime4,
				WS_AttendanceItem.StartBreakTime5, 
				WS_AttendanceItem.EndBreakTime5, 
				WS_AttendanceItem.StartBreakTime6,
				WS_AttendanceItem.EndBreakTime6);
	}

	public void testGetBreakTimeItemWithSize7() {
		List<WS_AttendanceItem> result7 = WS_AttendanceItem.getBreakTimeItemWithSize(7);
		assertThat(result7).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3, 
				WS_AttendanceItem.StartBreakTime4, 
				WS_AttendanceItem.EndBreakTime4,
				WS_AttendanceItem.StartBreakTime5, 
				WS_AttendanceItem.EndBreakTime5, 
				WS_AttendanceItem.StartBreakTime6,
				WS_AttendanceItem.EndBreakTime6, 
				WS_AttendanceItem.StartBreakTime7, 
				WS_AttendanceItem.EndBreakTime7);
	}

	public void testGetBreakTimeItemWithSize8() {
		List<WS_AttendanceItem> result8 = WS_AttendanceItem.getBreakTimeItemWithSize(8);
		assertThat(result8).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3, 
				WS_AttendanceItem.StartBreakTime4, 
				WS_AttendanceItem.EndBreakTime4,
				WS_AttendanceItem.StartBreakTime5, 
				WS_AttendanceItem.EndBreakTime5,
				WS_AttendanceItem.StartBreakTime6,
				WS_AttendanceItem.EndBreakTime6, 
				WS_AttendanceItem.StartBreakTime7, 
				WS_AttendanceItem.EndBreakTime7,
				WS_AttendanceItem.StartBreakTime8, 
				WS_AttendanceItem.EndBreakTime8);
	}

	public void testGetBreakTimeItemWithSize9() {
		List<WS_AttendanceItem> result9 = WS_AttendanceItem.getBreakTimeItemWithSize(9);
		assertThat(result9).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3, 
				WS_AttendanceItem.StartBreakTime4, 
				WS_AttendanceItem.EndBreakTime4,
				WS_AttendanceItem.StartBreakTime5, 
				WS_AttendanceItem.EndBreakTime5, 
				WS_AttendanceItem.StartBreakTime6,
				WS_AttendanceItem.EndBreakTime6, 
				WS_AttendanceItem.StartBreakTime7,
				WS_AttendanceItem.EndBreakTime7,
				WS_AttendanceItem.StartBreakTime8, 
				WS_AttendanceItem.EndBreakTime8);
	}

	public void testGetBreakTimeItemWithSize10() {
		List<WS_AttendanceItem> result10 = WS_AttendanceItem.getBreakTimeItemWithSize(10);
		assertThat(result10).containsExactly(
				WS_AttendanceItem.StartBreakTime1, 
				WS_AttendanceItem.EndBreakTime1,
				WS_AttendanceItem.StartBreakTime2, 
				WS_AttendanceItem.EndBreakTime2, 
				WS_AttendanceItem.StartBreakTime3,
				WS_AttendanceItem.EndBreakTime3, 
				WS_AttendanceItem.StartBreakTime4, 
				WS_AttendanceItem.EndBreakTime4,
				WS_AttendanceItem.StartBreakTime5, 
				WS_AttendanceItem.EndBreakTime5, 
				WS_AttendanceItem.StartBreakTime6,
				WS_AttendanceItem.EndBreakTime6, 
				WS_AttendanceItem.StartBreakTime7, 
				WS_AttendanceItem.EndBreakTime7,
				WS_AttendanceItem.StartBreakTime8, 
				WS_AttendanceItem.EndBreakTime8,
				WS_AttendanceItem.StartBreakTime9, 
				WS_AttendanceItem.EndBreakTime9,
				WS_AttendanceItem.StartBreakTime10, 
				WS_AttendanceItem.EndBreakTime10);
	}

}
