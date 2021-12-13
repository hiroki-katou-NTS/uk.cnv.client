package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.withinworkinghours;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.common.timerounding.Unit;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class LateLeaveEarlyTimeSheetTest {

	@Test
	public void getAfterRoundingAsLate_1MIN_DOWN() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(8, 30), TimeWithDayAttr.hourMinute(8, 39)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLate(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(8, 30).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(8, 39).getDayTime());
	}

	@Test
	public void getAfterRoundingAsLate_30MIN_UP() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(8, 30), TimeWithDayAttr.hourMinute(8, 39)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_30MIN, Rounding.ROUNDING_UP));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLate(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(8, 30).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(9, 00).getDayTime());
	}
	
	@Test
	public void getAfterRoundingAsLate_5MIN_DOWN() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(8, 30), TimeWithDayAttr.hourMinute(8, 39)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_5MIN, Rounding.ROUNDING_DOWN));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLate(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(8, 30).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(8, 35).getDayTime());
	}
	
	@Test
	public void getAfterRoundingAsLate_60MIN_DOWN() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(8, 30), TimeWithDayAttr.hourMinute(8, 39)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_60MIN, Rounding.ROUNDING_DOWN));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLate(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(8, 30).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(8, 30).getDayTime());
	}
	
	@Test
	public void getAfterRoundingAsLeaveEarly_1MIN_DOWN() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(17, 20), TimeWithDayAttr.hourMinute(17, 30)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_1MIN, Rounding.ROUNDING_DOWN));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLeaveEarly(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 20).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 30).getDayTime());
	}

	@Test
	public void getAfterRoundingAsLeaveEarly_30MIN_UP() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(17, 20), TimeWithDayAttr.hourMinute(17, 30)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_30MIN, Rounding.ROUNDING_UP));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLeaveEarly(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 00).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 30).getDayTime());
	}
	
	@Test
	public void getAfterRoundingAsLeaveEarly_5MIN_DOWN() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(17, 21), TimeWithDayAttr.hourMinute(17, 30)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_5MIN, Rounding.ROUNDING_DOWN));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLeaveEarly(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 25).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 30).getDayTime());
	}
	
	@Test
	public void getAfterRoundingAsLeaveEarly_60MIN_DOWN() {
		LateLeaveEarlyTimeSheet target  = new LateLeaveEarlyTimeSheet(
				new TimeSpanForDailyCalc(TimeWithDayAttr.hourMinute(17, 20), TimeWithDayAttr.hourMinute(17, 30)),
				new TimeRoundingSetting(Unit.ROUNDING_TIME_60MIN, Rounding.ROUNDING_DOWN));
		TimeSpanForDailyCalc result = target.getAfterRoundingAsLeaveEarly(); 
		assertThat(result.getStart().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 30).getDayTime());
		assertThat(result.getEnd().getDayTime()).isEqualTo(TimeWithDayAttr.hourMinute(17, 30).getDayTime());
	}
}
