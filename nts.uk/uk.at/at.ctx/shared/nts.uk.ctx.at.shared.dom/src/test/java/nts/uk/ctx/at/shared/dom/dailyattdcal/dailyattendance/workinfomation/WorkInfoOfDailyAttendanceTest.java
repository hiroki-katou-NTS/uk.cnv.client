package nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.workinfomation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.WorkInfoAndTimeZone;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance.Require;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.time.TimeWithDayAttr;

@RunWith(JMockit.class)
public class WorkInfoOfDailyAttendanceTest {
	@Injectable
	private Require require;

	@Test
	public void getters() {
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getWorkInfoOfDailyAttendanceDefault();
		NtsAssert.invokeGetters(workInfoOfDailyAttendance);
	}
	
	/**
	 * 勤務実績の勤務情報.出勤・休日系の判定(require) == optional.empty
	 */
	@Test
	public void testGetWorkStyle() {
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getWorkInfoOfDailyAttendanceDefault();
		new MockUp<WorkInformation>() {
			@Mock
			public Optional<WorkStyle> getWorkStyle(WorkInformation.Require require) {
				return Optional.empty();
			}
		};
		Optional<WorkStyle> workstyle = workInfoOfDailyAttendance.getWorkStyle(require);
		assertThat(workstyle.isPresent()).isFalse();
	}
	
	/**
	 * 勤務実績の勤務情報.出勤・休日系の判定(require) == WorkStyle.ONE_DAY_REST
	 */
	@Test
	public void testGetWorkStyle_1() {
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getWorkInfoOfDailyAttendanceDefault();
		new MockUp<WorkInformation>() {
			@Mock
			public Optional<WorkStyle> getWorkStyle(WorkInformation.Require require) {
				return Optional.of(WorkStyle.ONE_DAY_REST);
			}
		};
		Optional<WorkStyle> workstyle = workInfoOfDailyAttendance.getWorkStyle(require);
		assertThat(workstyle.get().equals(WorkStyle.ONE_DAY_REST)).isTrue();
	}
	
	/**
	 * 勤務実績の勤務情報.出勤・休日系の判定(require) == WorkStyle.MORNING_WORK
	 */
	@Test
	public void testGetWorkStyle_2() {
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getWorkInfoOfDailyAttendanceDefault();
		new MockUp<WorkInformation>() {
			@Mock
			public Optional<WorkStyle> getWorkStyle(WorkInformation.Require require) {
				return Optional.of(WorkStyle.MORNING_WORK);
			}
		};
		Optional<WorkStyle> workstyle = workInfoOfDailyAttendance.getWorkStyle(require);
		assertThat(workstyle.get().equals(WorkStyle.MORNING_WORK)).isTrue();
	}
	
	/**
	 * 勤務実績の勤務情報.出勤・休日系の判定(require) == WorkStyle.AFTERNOON_WORK
	 */
	@Test
	public void testGetWorkStyle_3() {
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getWorkInfoOfDailyAttendanceDefault();
		new MockUp<WorkInformation>() {
			@Mock
			public Optional<WorkStyle> getWorkStyle(WorkInformation.Require require) {
				return Optional.of(WorkStyle.AFTERNOON_WORK);
			}
		};
		Optional<WorkStyle> workstyle = workInfoOfDailyAttendance.getWorkStyle(require);
		assertThat(workstyle.get().equals(WorkStyle.AFTERNOON_WORK)).isTrue();
	}
	
	/**
	 * 勤務実績の勤務情報.出勤・休日系の判定(require) == WorkStyle.ONE_DAY_WORK
	 */
	@Test
	public void testGetWorkStyle_4() {
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getWorkInfoOfDailyAttendanceDefault();
		new MockUp<WorkInformation>() {
			@Mock
			public Optional<WorkStyle> getWorkStyle(WorkInformation.Require require) {
				return Optional.of(WorkStyle.ONE_DAY_WORK);
			}
		};
		Optional<WorkStyle> workstyle = workInfoOfDailyAttendance.getWorkStyle(require);
		assertThat(workstyle.get().equals(WorkStyle.ONE_DAY_WORK)).isTrue();
	}
	
	@Test
	public void testCreate_scheduleTimeSheets_empty(
			@Injectable WorkInformation workInfo,
			@Injectable CalculationState calculationState,
			@Injectable NotUseAttribute backStraightAtr,
			@Injectable NotUseAttribute goStraightAtr,
			@Injectable DayOfWeek dayOfWeek
			) {
		
		// Arrange
		val WorkInfoAndTimeZone = WorkInfoOfDailyAttendanceHelper.createWorkInfoAndTimeZone(Collections.emptyList());
		new Expectations() { {
			workInfo.getWorkInfoAndTimeZone(require);
			result = Optional.of(WorkInfoAndTimeZone);
		}};
		
		// Action
		WorkInfoOfDailyAttendance target = WorkInfoOfDailyAttendance.create(require, 
																		workInfo, 
																		calculationState, 
																		backStraightAtr, 
																		goStraightAtr, 
																		dayOfWeek);
		
		// Assert
		assertThat(target.getRecordInfo()).isEqualTo(workInfo);
		assertThat( target.getCalculationState() ).isEqualTo( calculationState );
		assertThat( target.getBackStraightAtr() ).isEqualTo( backStraightAtr );
		assertThat( target.getGoStraightAtr() ).isEqualTo( goStraightAtr );
		assertThat( target.getDayOfWeek() ).isEqualTo( dayOfWeek );
		assertThat(target.getScheduleTimeSheets()).isEmpty(); // emptyList
	}
	
	@Test
	public void testCreate_scheduleTimeSheets_not_empty(
			@Injectable WorkInformation workInfo,
			@Injectable CalculationState calculationState,
			@Injectable NotUseAttribute backStraightAtr,
			@Injectable NotUseAttribute goStraightAtr,
			@Injectable DayOfWeek dayOfWeek
			) {
		
		// Arrange
		
		List<TimeZone> listTimeZone = Arrays.asList(
				new TimeZone(new TimeWithDayAttr(10), new TimeWithDayAttr(20)),
				new TimeZone(new TimeWithDayAttr(30), new TimeWithDayAttr(40)));
		val WorkInfoAndTimeZone = WorkInfoOfDailyAttendanceHelper.createWorkInfoAndTimeZone(listTimeZone);
		
		new Expectations(workInfo) {{
			workInfo.getWorkInfoAndTimeZone(require);
			result = Optional.of(WorkInfoAndTimeZone);
		}};
		
		// Action
		WorkInfoOfDailyAttendance target = WorkInfoOfDailyAttendance.create(require, 
																		workInfo, 
																		calculationState, 
																		backStraightAtr, 
																		goStraightAtr, 
																		dayOfWeek);
		
		// Assert
		assertThat( target.getRecordInfo() ).isEqualTo( workInfo );
		assertThat( target.getCalculationState() ).isEqualTo( calculationState );
		assertThat( target.getBackStraightAtr() ).isEqualTo( backStraightAtr );
		assertThat( target.getGoStraightAtr() ).isEqualTo( goStraightAtr );
		assertThat( target.getDayOfWeek() ).isEqualTo( dayOfWeek );
		assertThat( target.getScheduleTimeSheets() )
			.extracting(
				e -> e.getWorkNo().v(),
				e -> e.getAttendance().v(),
				e -> e.getLeaveWork().v())
			.containsExactly(
				tuple(1, 10, 20),
				tuple(2, 30, 40));
	}

	/**
	 * input:  workInfo.isAttendanceRate(require) = false
	 * output: false
	 * 
	 */
	
	@Test
	public void isAttendanceRate_False() {
		val workInfo = new WorkInformation("01", "01");
		val workInfoOfDailyAtt = WorkInfoOfDailyAttendanceHelper.createByWorkInformation(workInfo);
		new Expectations(workInfo) {
			{
				workInfo.isAttendanceRate(require);
				result = false;
			}
		};
				
		assertThat(workInfoOfDailyAtt.isAttendanceRate(require)).isFalse();
		
	}
	
	/**
	 * input:  workInfo.isAttendanceRate(require) = true
	 * output: true
	 * 
	 */
	@Test
	public void isAttendanceRate_true() {
		val workInfo = new WorkInformation("01", "01");
		val workInfoOfDailyAtt = WorkInfoOfDailyAttendanceHelper.createByWorkInformation(workInfo);
		new Expectations(workInfo) {
			{
				workInfo.isAttendanceRate(require);
				result = true;
			}
		};
				
		assertThat(workInfoOfDailyAtt.isAttendanceRate(require)).isTrue();
		
	}
	
	/*
	 * テストしたい内容
	 * 
	 * →勤務情報と始業終業を変更する
	 * 　①勤務種類を変更する＝true　→勤務情報の勤務種類を変更する
　 *　②就業時間帯を変更する＝true　→勤務情報の就業時間帯を変更する
	 * 
	 * 準備するデータ
	 * 
	 */

	@Test
	public void testReflectWorkType() {
		
		WorkInfoOfDailyAttendance workInfoDomain = create("001", "001");//前勤務情報と始業終業
		WorkInformation workInfo = new WorkInformation("002", //勤務種類
																								"003");//就業時間帯
		//①勤務種類を変更する＝true　→勤務情報の勤務種類を変更する
		workInfoDomain.changeWorkSchedule(require, workInfo, true, false);
		assertThat(workInfoDomain.getRecordInfo().getWorkTypeCode().v()).isEqualTo("002");
		assertThat(workInfoDomain.getRecordInfo().getWorkTimeCode().v()).isEqualTo("001");
		
		// ①勤務種類を変更する＝true →勤務情報の勤務種類を変更する
		workInfoDomain = create("001", "001");// 前勤務情報と始業終業
		workInfoDomain.changeWorkSchedule(require, workInfo, false, true);
		assertThat(workInfoDomain.getRecordInfo().getWorkTypeCode().v()).isEqualTo("001");
		assertThat(workInfoDomain.getRecordInfo().getWorkTimeCode().v()).isEqualTo("003");
		
	}
	
	/*
	 * テストしたい内容
	 * 
	 * →勤務情報と始業終業を変更する
	 * 　①始業終業時間帯を変更する
	 *         →勤務Noを保存した：更新
	 * 　　→勤務Noを保存しない：作成
	 * 準備するデータ
	 * 
	 */

	@Test
	public void testWorkTimeSchedule(@Mocked WorkInformation workInfoDomainMock) {
		
		List<ScheduleTimeSheet> scheduleTimeSheets = new ArrayList<ScheduleTimeSheet>();
		scheduleTimeSheets.add(new ScheduleTimeSheet(1, 101, 201));//
		WorkInfoOfDailyAttendance workInfoDomain = create(scheduleTimeSheets);
		WorkInformation workInfo = new WorkInformation("002", //勤務種類
																								"003");//就業時間帯
		
		new Expectations() {
			{
				workInfoDomainMock.getWorkInfoAndTimeZone(require);
				result = Optional.of(new WorkInfoAndTimeZone(new WorkType(), Optional.empty(),
						Arrays.asList(new TimeZone(new TimeWithDayAttr(100), new TimeWithDayAttr(200)), //No1
												new TimeZone(new TimeWithDayAttr(300), new TimeWithDayAttr(400)))));//No2
			}
		};
		
		workInfoDomain.changeWorkSchedule(require, workInfo, true, true);
		
		assertThat(workInfoDomain.getScheduleTimeSheets())
				.extracting(x -> x.getWorkNo().v(), x -> x.getAttendance().v(), x -> x.getLeaveWork().v())
				.contains(Tuple.tuple(1, 100, 200), //勤務Noを保存した：更新
						        Tuple.tuple(2, 300, 400));//勤務Noを保存しない：作成
		
	}
	private WorkInfoOfDailyAttendance create(List<ScheduleTimeSheet> scheduleTimeSheets) {
		return new WorkInfoOfDailyAttendance(new WorkInformation("001", "001"),
				CalculationState.No_Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use, DayOfWeek.FRIDAY,
				scheduleTimeSheets, Optional.empty());
	}
	
	private WorkInfoOfDailyAttendance create(String workTypeCode, String workTimeCode) {
		return new WorkInfoOfDailyAttendance(new WorkInformation(workTypeCode, workTimeCode),
				CalculationState.No_Calculated, NotUseAttribute.Not_use, NotUseAttribute.Not_use, DayOfWeek.FRIDAY,
				new ArrayList<>(), Optional.empty());
	}
}
