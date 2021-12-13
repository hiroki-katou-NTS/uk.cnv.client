package nts.uk.ctx.at.aggregation.dom.scheduletable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.aggregation.dom.common.ActualWorkingTimeHelperInAgregation;
import nts.uk.ctx.at.aggregation.dom.common.AttendanceTimeHelperInAggregation;
import nts.uk.ctx.at.aggregation.dom.common.IntegrationOfDailyHelperInAggregation;
import nts.uk.ctx.at.aggregation.dom.common.PremiumTimeHelperInAggregation;
import nts.uk.ctx.at.aggregation.dom.common.TimeLeavingOfDailyAttdHelperInAggregation;
import nts.uk.ctx.at.aggregation.dom.common.TotalWorkingTimeHelperInAggregation;
import nts.uk.ctx.at.aggregation.dom.common.WorkInfoOfDailyAttdHelperInAggregation;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.time.TimeWithDayAttr;

@RunWith(JMockit.class)
public class OneDayEmployeeAttendanceInfoTest {
	
	@Injectable
	OneDayEmployeeAttendanceInfo.Require require;
	
	private static final TimeWithDayAttr startTime1 = TimeWithDayAttr.hourMinute(8, 30);
	private static final TimeWithDayAttr endTime1 = TimeWithDayAttr.hourMinute(17, 30);
	private static final TimeWithDayAttr startTime2 = TimeWithDayAttr.hourMinute(20, 00);
	private static final TimeWithDayAttr endTime2 = TimeWithDayAttr.hourMinute(25, 00);
	
	private static final AttendanceTime totalTime = new AttendanceTime(600);
	private static final AttendanceTime actualTime = new AttendanceTime(550);
	private static final AttendanceTime workTime = new AttendanceTime(400);
	
	@SuppressWarnings("serial")
	private static final Map<Integer, AttendanceTime> premiumTimeMap = new HashMap<Integer, AttendanceTime>(){{
		put(1, new AttendanceTime(100));
		put(2, new AttendanceTime(200));
		put(3, new AttendanceTime(300));
		put(4, new AttendanceTime(400));
		put(5, new AttendanceTime(500));
	}};
	
	@Test
	public void testCreate_DayOff() {
		
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), null ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		// Act
		val result = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		// Assert
		assertThat( result.getEmployeeId() ).isEqualTo( "employeeId" );
		assertThat( result.getDate() ).isEqualTo( GeneralDate.ymd(2021, 7, 1) );
		
		val resultMap = result.getAttendanceItemInfoMap();
		assertThat( resultMap.keySet() ).containsOnly(ScheduleTableAttendanceItem.WORK_TYPE);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.WORK_TYPE) ).isEqualTo("work-type-code");
	}
	
	@Test
	public void testCreate_WorkDay_workOneTime() {
		
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		val attendanceLeave = TimeLeavingOfDailyAttdHelperInAggregation.create(
				startTime1,
				endTime1,
				Optional.empty(),
				Optional.empty());
		val attendanceTime = AttendanceTimeHelperInAggregation.createWithActualWorkingTimeOfDaily(
								ActualWorkingTimeHelperInAgregation.create(
										TotalWorkingTimeHelperInAggregation.create(
												totalTime, 
												actualTime, 
												workTime),
										PremiumTimeHelperInAggregation.create(premiumTimeMap)));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.of(attendanceLeave), // 日別勤怠の出退勤
				Optional.of(attendanceTime)); // 日別勤怠の勤怠時間
		
		// Act
		val result = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		// Assert
		assertThat( result.getEmployeeId() ).isEqualTo( "employeeId" );
		assertThat( result.getDate() ).isEqualTo( GeneralDate.ymd(2021, 7, 1) );
		
		val resultMap = result.getAttendanceItemInfoMap();
		assertThat( resultMap.keySet() ).containsExactlyInAnyOrder(
				ScheduleTableAttendanceItem.WORK_TYPE,
				ScheduleTableAttendanceItem.WORK_TIME,
				ScheduleTableAttendanceItem.START_TIME,
				ScheduleTableAttendanceItem.END_TIME,
				ScheduleTableAttendanceItem.TOTAL_WORKING_HOURS,
				ScheduleTableAttendanceItem.WORKING_HOURS,
				ScheduleTableAttendanceItem.ACTUAL_WORKING_HOURS,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_1,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_2,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_3,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_4,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_5);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.WORK_TYPE) ).isEqualTo("work-type-code");
		assertThat( resultMap.get(ScheduleTableAttendanceItem.WORK_TIME) ).isEqualTo("work-time-code");
		assertThat( resultMap.get(ScheduleTableAttendanceItem.START_TIME) ).isEqualTo(startTime1);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.END_TIME) ).isEqualTo(endTime1);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.TOTAL_WORKING_HOURS) ).isEqualTo(totalTime);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.WORKING_HOURS) ).isEqualTo(workTime);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.ACTUAL_WORKING_HOURS) ).isEqualTo(actualTime);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_1) ).isEqualTo(premiumTimeMap.get(1));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_2) ).isEqualTo(premiumTimeMap.get(2));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_3) ).isEqualTo(premiumTimeMap.get(3));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_4) ).isEqualTo(premiumTimeMap.get(4));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_5) ).isEqualTo(premiumTimeMap.get(5));
	}
	
	@Test
	public void testCreate_WorkDay_workTwoTimes() {
		
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		val attendanceLeave = TimeLeavingOfDailyAttdHelperInAggregation.create(
				startTime1,
				endTime1,
				Optional.of(startTime2),
				Optional.of(endTime2));
		val attendanceTime = AttendanceTimeHelperInAggregation.createWithActualWorkingTimeOfDaily(
								ActualWorkingTimeHelperInAgregation.create(
										TotalWorkingTimeHelperInAggregation.create(
												totalTime, 
												actualTime, 
												workTime),
										PremiumTimeHelperInAggregation.create(premiumTimeMap)));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.of(attendanceLeave), // 日別勤怠の出退勤
				Optional.of(attendanceTime)); // 日別勤怠の勤怠時間
		
		// Act
		val result = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		// Assert
		assertThat( result.getEmployeeId() ).isEqualTo( "employeeId" );
		assertThat( result.getDate() ).isEqualTo( GeneralDate.ymd(2021, 7, 1) );
		
		val resultMap = result.getAttendanceItemInfoMap();
		assertThat( resultMap.keySet() ).containsExactlyInAnyOrder(
				ScheduleTableAttendanceItem.WORK_TYPE,
				ScheduleTableAttendanceItem.WORK_TIME,
				ScheduleTableAttendanceItem.START_TIME,
				ScheduleTableAttendanceItem.END_TIME,
				ScheduleTableAttendanceItem.START_TIME_2,
				ScheduleTableAttendanceItem.END_TIME_2,
				ScheduleTableAttendanceItem.TOTAL_WORKING_HOURS,
				ScheduleTableAttendanceItem.WORKING_HOURS,
				ScheduleTableAttendanceItem.ACTUAL_WORKING_HOURS,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_1,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_2,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_3,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_4,
				ScheduleTableAttendanceItem.LABOR_COST_TIME_5);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.WORK_TYPE) ).isEqualTo("work-type-code");
		assertThat( resultMap.get(ScheduleTableAttendanceItem.WORK_TIME) ).isEqualTo("work-time-code");
		assertThat( resultMap.get(ScheduleTableAttendanceItem.START_TIME) ).isEqualTo(startTime1);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.END_TIME) ).isEqualTo(endTime1);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.START_TIME_2) ).isEqualTo(startTime2);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.END_TIME_2) ).isEqualTo(endTime2);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.TOTAL_WORKING_HOURS) ).isEqualTo(totalTime);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.WORKING_HOURS) ).isEqualTo(workTime);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.ACTUAL_WORKING_HOURS) ).isEqualTo(actualTime);
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_1) ).isEqualTo(premiumTimeMap.get(1));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_2) ).isEqualTo(premiumTimeMap.get(2));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_3) ).isEqualTo(premiumTimeMap.get(3));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_4) ).isEqualTo(premiumTimeMap.get(4));
		assertThat( resultMap.get(ScheduleTableAttendanceItem.LABOR_COST_TIME_5) ).isEqualTo(premiumTimeMap.get(5));
	}
	
	@Test
	public void testGetShiftMaster_DayOff(@Injectable ShiftMaster shiftMaster) {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), null ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		new Expectations() {{
			require.getShiftMaster("work-type-code", Optional.empty());
			result = Optional.of(shiftMaster);
		}};
		
		// Act
		val result = target.getShiftMaster(require);
		
		// Assert
		assertThat(result.get()).isEqualTo(shiftMaster);
	}
	
	@Test
	public void testGetShiftMaster_WorkDay_ShiftMasterNotExist() {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		new Expectations() {{
			require.getShiftMaster("work-type-code", Optional.of("work-time-code"));
			// result = Optional.empty();
		}};
		
		// Act
		val result = target.getShiftMaster(require);
		
		// Assert
		assertThat(result).isEmpty();
	}
	
	@Test
	public void testGetShiftMaster_WorkDay_ShiftMasterExist(@Injectable ShiftMaster shiftMaster) {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		new Expectations() {{
			require.getShiftMaster("work-type-code", Optional.of("work-time-code"));
			result = Optional.of(shiftMaster);
		}};
		
		// Act
		val result = target.getShiftMaster(require);
		
		// Assert
		assertThat(result.get()).isEqualTo(shiftMaster);
	}
	
	@Test
	public void testGetWorkType_notExist() {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		new Expectations() {{
			require.getWorkType("work-type-code");
			// Optional.empty()
		}};
		
		// Act
		val result = target.getWorkType(require);
		
		// Assert
		assertThat(result).isEmpty();
	}
	
	@Test
	public void testGetWorkType_exist(@Injectable WorkType workType) {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		new Expectations() {{
			require.getWorkType("work-type-code");
			result = Optional.of(workType);
		}};
		
		// Act
		val result = target.getWorkType(require);
		
		// Assert
		assertThat(result.get()).isEqualTo(workType);
	}
	
	@Test
	public void testGetWorkTime_DayOff() {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), null ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		// Act
		val result = target.getWorkTime(require);
		
		// Assert
		assertThat(result).isEmpty();
	}
	
	@Test
	public void testGetWorkTime_WorkDay_WorkTimeNotExist() {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		new Expectations() {{
			require.getWorkTimeSetting("work-time-code");
			// result = Optional.empty();
		}};
		
		// Act
		val result = target.getWorkTime(require);
		
		// Assert
		assertThat(result).isEmpty();
	}
	
	@Test
	public void testGetWorkTime_WorkDay_WorkTimeExist(@Injectable WorkTimeSetting workTimeSetting) {
		// Arrange
		val workInfo = WorkInfoOfDailyAttdHelperInAggregation.createByWorkInformation(
				new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") ));
		
		val dailyData = IntegrationOfDailyHelperInAggregation.create(
				"employeeId", 
				GeneralDate.ymd(2021, 07, 01), 
				workInfo, // 日別勤怠の勤務情報
				Optional.empty(), // 日別勤怠の出退勤
				Optional.empty()); // 日別勤怠の勤怠時間
		
		val target = OneDayEmployeeAttendanceInfo.create(dailyData);
		
		new Expectations() {{
			require.getWorkTimeSetting("work-time-code");
			result = Optional.of(workTimeSetting);
		}};
		
		// Act
		val result = target.getWorkTime(require);
		
		// Assert
		assertThat(result.get()).isEqualTo(workTimeSetting);
	}

}
